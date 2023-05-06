package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.enums.EnumStatus;
import org.dows.hep.api.tenant.casus.request.CaseInstancePageRequest;
import org.dows.hep.api.tenant.casus.request.CaseInstanceRequest;
import org.dows.hep.api.tenant.casus.response.CaseInstancePageResponse;
import org.dows.hep.api.tenant.casus.response.CaseInstanceResponse;
import org.dows.hep.entity.CaseInstanceEntity;
import org.dows.hep.service.CaseInstanceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:案例:案例管理
 * @date 2023年4月23日 上午9:44:34
 */
@RequiredArgsConstructor
@Service
public class TenantCaseManageBiz {
    private final BaseTenantCaseBiz baseBiz;
    private final CaseInstanceService caseInstanceService;

    /**
     * @param
     * @return
     * @说明: 创建和更新案例
     * @关联表: caseInstanceRequest
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public String saveOrUpdCaseInstance(CaseInstanceRequest caseInstanceRequest) {
        if (BeanUtil.isEmpty(caseInstanceRequest)) {
            return "";
        }

        if (StrUtil.isBlank(caseInstanceRequest.getCaseInstanceId())) {
            caseInstanceRequest.setAccountId(baseBiz.getAppId());
            caseInstanceRequest.setCaseInstanceId(baseBiz.getIdStr());
            caseInstanceRequest.setCaseIdentifier(baseBiz.getIdStr());
            caseInstanceRequest.setVer(baseBiz.getLastVer());
        }
        CaseInstanceEntity caseInstanceEntity = BeanUtil.copyProperties(caseInstanceRequest, CaseInstanceEntity.class);
        caseInstanceService.saveOrUpdate(caseInstanceEntity);
        return caseInstanceEntity.getCaseInstanceId();
    }

    /**
    * @param
    * @return
    * @说明: 复制
    * @关联表: caseInstance
    * @工时: 8H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String copyCaseInstance(String oriCaseInstanceId ) {
        return new String();
    }

    /**
    * @param
    * @return
    * @说明: 列表
    * @关联表: caseInstance
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Page<CaseInstancePageResponse> pageCaseInstance(CaseInstancePageRequest caseInstancePageRequest ) {
        Page<CaseInstancePageResponse> result = new Page<>();
        if (BeanUtil.isEmpty(caseInstancePageRequest)) {
            return result;
        }

        Page<CaseInstanceEntity> pageRequest = new Page<>(caseInstancePageRequest.getPageNo(), caseInstancePageRequest.getPageSize());
        Page<CaseInstanceEntity> pageResult = caseInstanceService.lambdaQuery()
                .eq(caseInstancePageRequest.getAppId() != null, CaseInstanceEntity::getAppId, caseInstancePageRequest.getAppId())
                .like(StrUtil.isNotBlank(caseInstancePageRequest.getKeyword()), CaseInstanceEntity::getCaseName, caseInstancePageRequest.getKeyword())
                .page(pageRequest);

        List<CaseInstanceEntity> records = pageResult.getRecords();
        if (records == null || records.isEmpty()) {
            return result;
        }

        List<CaseInstancePageResponse> pageResponseList = records.stream()
                .map(item -> BeanUtil.copyProperties(item, CaseInstancePageResponse.class))
                .collect(Collectors.toList());
        result.setRecords(pageResponseList);
        return result;
    }
    /**
    * @param
    * @return
    * @说明: 获取案例详情
    * @关联表: caseInstance
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public CaseInstanceResponse getCaseInstance(String caseInstanceId) {
        CaseInstanceResponse result = new CaseInstanceResponse();
        if (StrUtil.isBlank(caseInstanceId)) {
            return result;
        }

        LambdaQueryWrapper<CaseInstanceEntity> queryWrapper = new LambdaQueryWrapper<CaseInstanceEntity>()
                .eq(CaseInstanceEntity::getCaseInstanceId, caseInstanceId);
        CaseInstanceEntity caseInstanceEntity = caseInstanceService.getOne(queryWrapper);
        result = BeanUtil.copyProperties(caseInstanceEntity, CaseInstanceResponse.class);
        return result;
    }

    /**
     * @param
     * @return
     * @说明: 启用
     * @关联表: caseInstance
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean enabledQuestion(String caseInstanceId) {
        if (StrUtil.isBlank(caseInstanceId)) {
            return false;
        }

        LambdaUpdateWrapper<CaseInstanceEntity> updateWrapper = new LambdaUpdateWrapper<CaseInstanceEntity>()
                .eq(CaseInstanceEntity::getCaseInstanceId, caseInstanceId)
                .set(CaseInstanceEntity::getState, EnumStatus.ENABLE.getCode());
        return caseInstanceService.update(updateWrapper);
    }

    /**
     * @param
     * @return
     * @说明: 禁用
     * @关联表: caseInstance
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean disabledQuestion(String caseInstanceId) {
        if (StrUtil.isBlank(caseInstanceId)) {
            return false;
        }

        LambdaUpdateWrapper<CaseInstanceEntity> updateWrapper = new LambdaUpdateWrapper<CaseInstanceEntity>()
                .eq(CaseInstanceEntity::getCaseInstanceId, caseInstanceId)
                .set(CaseInstanceEntity::getState, EnumStatus.DISABLE.getCode());
        return caseInstanceService.update(updateWrapper);
    }

    /**
     * @param
     * @return
     * @说明: 删除
     * @关联表: caseInstance
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean delCaseInstance(List<String> caseInstanceIds) {
        if (caseInstanceIds == null || caseInstanceIds.isEmpty()) {
            return Boolean.FALSE;
        }

        LambdaQueryWrapper<CaseInstanceEntity> queryWrapper = new LambdaQueryWrapper<CaseInstanceEntity>()
                .in(CaseInstanceEntity::getCaseInstanceId, caseInstanceIds);
        return caseInstanceService.remove(queryWrapper);
    }
}