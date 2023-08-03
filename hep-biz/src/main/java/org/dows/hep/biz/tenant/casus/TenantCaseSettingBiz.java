package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.constant.RedisKeyConst;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.api.tenant.casus.CaseQuestionnaireDistributionEnum;
import org.dows.hep.api.tenant.casus.CaseScoreModeEnum;
import org.dows.hep.api.tenant.casus.request.CaseSettingRequest;
import org.dows.hep.api.tenant.casus.response.CaseSettingResponse;
import org.dows.hep.entity.CaseSettingEntity;
import org.dows.hep.service.CaseSettingService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author lait.zhang
 * @description project descr:案例:案例问卷设置
 * @date 2023年4月23日 上午9:44:34
 */
@Service
@RequiredArgsConstructor
public class TenantCaseSettingBiz {
    private final TenantCaseBaseBiz baseBiz;
    private final CaseSettingService caseSettingService;

    private final RedissonClient redissonClient;

    /**
     * @param request - 案例设置请求
     * @return java.lang.String
     * @author fhb
     * @description 新增或更新案例设置
     * @date 2023/8/3 10:49
     */
    public String saveOrUpdCaseSetting(CaseSettingRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }
        String caseInstanceId = request.getCaseInstanceId();
        if (StrUtil.isBlank(caseInstanceId)) {
            throw new BizException("新增或更新案例设置时：案例ID不能为空");
        }

        // 如果新增
        RLock lock = redissonClient.getLock(RedisKeyConst.HEP_LOCK_CASE_SETTING + caseInstanceId);
        try {
            if (lock.tryLock(-1, 10, TimeUnit.SECONDS)) {
                String caseSettingId = request.getCaseSettingId();
                if (StrUtil.isBlank(caseSettingId)) {
                    CaseSettingResponse caseSetting = getCaseSetting(caseInstanceId);
                    if (BeanUtil.isNotEmpty(caseSetting)) {
                        throw new BizException("新增案例设置时： 案例设置已存在，请勿重复添加");
                    }
                }

                CaseSettingEntity entity = convertRequest2Entity(request);
                caseSettingService.saveOrUpdate(entity);
                return entity.getCaseSettingId();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return "";
    }

    /**
     * @param
     * @return
     * @说明: 获取案例问卷设置
     * @关联表: caseSetting
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public CaseSettingResponse getCaseSetting(String caseInstanceId) {
        if (StrUtil.isBlank(caseInstanceId)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        LambdaQueryWrapper<CaseSettingEntity> queryWrapper = new LambdaQueryWrapper<CaseSettingEntity>()
                .eq(CaseSettingEntity::getCaseInstanceId, caseInstanceId);
        CaseSettingEntity entity = caseSettingService.getOne(queryWrapper);

        return BeanUtil.copyProperties(entity, CaseSettingResponse.class);
    }

    /**
     * @param caseInstanceIds - 案例实例ID集合
     * @return java.lang.Boolean
     * @author fhb
     * @description 删除案例设置
     * @date 2023/7/24 17:39
     */
    public Boolean delCaseSettingByCaseInstanceId(List<String> caseInstanceIds) {
        if (CollUtil.isEmpty(caseInstanceIds)) {
            return Boolean.FALSE;
        }

        LambdaQueryWrapper<CaseSettingEntity> remWrapper = new LambdaQueryWrapper<CaseSettingEntity>()
                .in(CaseSettingEntity::getCaseInstanceId, caseInstanceIds);
        return caseSettingService.remove(remWrapper);
    }

    private CaseSettingEntity convertRequest2Entity(CaseSettingRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        CaseSettingEntity result = CaseSettingEntity.builder()
                .caseSettingId(request.getCaseSettingId())
                .caseInstanceId(request.getCaseInstanceId())
                .scoreMode(request.getScoreMode())
                .allotMode(request.getAllotMode())
                .ext(request.getExt())
                .build();

        String uniqueId = result.getCaseSettingId();
        if (StrUtil.isBlank(uniqueId)) {
            result.setCaseSettingId(baseBiz.getIdStr());
            if (StrUtil.isBlank(result.getScoreMode())) {
                result.setScoreMode(CaseScoreModeEnum.STRICT.name());
            }
            if (StrUtil.isBlank(result.getAllotMode())) {
                result.setAllotMode(CaseQuestionnaireDistributionEnum.RANDOM.name());
            }
        } else {
            CaseSettingEntity entity = getById(uniqueId);
            if (BeanUtil.isEmpty(entity)) {
                throw new BizException(CaseESCEnum.DATA_NULL);
            }
            result.setId(entity.getId());
        }

        return result;

    }

    private CaseSettingEntity getById(String caseSettingId) {
        LambdaQueryWrapper<CaseSettingEntity> queryWrapper = new LambdaQueryWrapper<CaseSettingEntity>()
                .eq(CaseSettingEntity::getCaseSettingId, caseSettingId);
        return caseSettingService.getOne(queryWrapper);
    }

}