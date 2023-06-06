package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.api.tenant.casus.request.CaseSettingRequest;
import org.dows.hep.api.tenant.casus.response.CaseSettingResponse;
import org.dows.hep.entity.CaseSettingEntity;
import org.dows.hep.service.CaseSettingService;
import org.springframework.stereotype.Service;

/**
* @description project descr:案例:案例问卷设置
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class TenantCaseSettingBiz {
    private final TenantCaseBaseBiz baseBiz;
    private final CaseSettingService caseSettingService;
    /**
    * @param
    * @return
    * @说明: 新增和更新案例问卷设置
    * @关联表: caseSetting
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String saveOrUpdCaseSetting(CaseSettingRequest request ) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        CaseSettingEntity entity = convertRequest2Entity(request);
        caseSettingService.saveOrUpdate(entity);

        return entity.getCaseSettingId();
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
    public CaseSettingResponse getCaseSetting(String caseInstanceId ) {
        if (StrUtil.isBlank(caseInstanceId)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        LambdaQueryWrapper<CaseSettingEntity> queryWrapper = new LambdaQueryWrapper<CaseSettingEntity>()
                .eq(CaseSettingEntity::getCaseInstanceId, caseInstanceId);
        CaseSettingEntity entity = caseSettingService.getOne(queryWrapper);

        return BeanUtil.copyProperties(entity, CaseSettingResponse.class);
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