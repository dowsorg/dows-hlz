package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.materials.MaterialsESCEnum;
import org.dows.hep.api.base.question.QuestionESCEnum;
import org.dows.hep.api.enums.EnumStatus;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.api.tenant.casus.CaseQuestionnaireDistributionEnum;
import org.dows.hep.api.tenant.casus.CaseScoreModeEnum;
import org.dows.hep.api.tenant.casus.request.CaseInstanceCopyRequest;
import org.dows.hep.api.tenant.casus.request.CaseInstancePageRequest;
import org.dows.hep.api.tenant.casus.request.CaseInstanceRequest;
import org.dows.hep.api.tenant.casus.request.CaseSettingRequest;
import org.dows.hep.api.tenant.casus.response.CaseInstancePageResponse;
import org.dows.hep.api.tenant.casus.response.CaseInstanceResponse;
import org.dows.hep.entity.CaseInstanceEntity;
import org.dows.hep.service.CaseInstanceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lait.zhang
 * @description project descr:案例:案例管理
 * @date 2023年4月23日 上午9:44:34
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TenantCaseManageBiz {
    private final CaseInstanceService caseInstanceService;
    private final TenantCaseBaseBiz baseBiz;
    private final TenantCaseNoticeBiz caseNoticeBiz;
    private final TenantCaseSchemeBiz caseSchemeBiz;
    private final TenantCaseQuestionnaireBiz caseQuestionnaireBiz;
    private final TenantCaseSettingBiz caseSettingBiz;

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

        CaseInstanceEntity caseInstanceEntity = convertRequest2Entity(caseInstanceRequest);
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
    public String copyCaseInstance(CaseInstanceCopyRequest request) {
        String oriCaseInstanceId = request.getOriCaseInstanceId();
        String targetCaseInstanceName = request.getTargetCaseInstanceName();
        if (StrUtil.isBlank(oriCaseInstanceId) || StrUtil.isBlank(targetCaseInstanceName)) {
            throw new BizException(CaseESCEnum.DATA_NULL);
        }

        // copy base-info
        CaseInstanceEntity caseInstanceEntity = copyCaseInstance0(oriCaseInstanceId, targetCaseInstanceName);
        // copy case-scheme
        caseSchemeBiz.copyCaseScheme(oriCaseInstanceId, caseInstanceEntity);
        // todo copy case-org
        // copy case-notice
        caseNoticeBiz.copyCaseNotice(oriCaseInstanceId, caseInstanceEntity);
        // todo copy case-questionnaire
        caseQuestionnaireBiz.copyCaseQuestionnaire(oriCaseInstanceId, caseInstanceEntity);

        return caseInstanceEntity.getCaseInstanceId();
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
    public IPage<CaseInstancePageResponse> pageCaseInstance(CaseInstancePageRequest caseInstancePageRequest ) {
        if (BeanUtil.isEmpty(caseInstancePageRequest)) {
            return new Page<>();
        }

        String accountId = caseInstancePageRequest.getAccountId();
        boolean isAdmin = baseBiz.isAdministrator(accountId);
        Page<CaseInstanceEntity> pageRequest = new Page<>(caseInstancePageRequest.getPageNo(), caseInstancePageRequest.getPageSize());
        Page<CaseInstanceEntity> pageResult = caseInstanceService.lambdaQuery()
                .eq(!isAdmin, CaseInstanceEntity::getAccountId, accountId)
                .eq(caseInstancePageRequest.getAppId() != null, CaseInstanceEntity::getAppId, caseInstancePageRequest.getAppId())
                .eq(caseInstancePageRequest.getState() != null, CaseInstanceEntity::getState, caseInstancePageRequest.getState())
                .like(StrUtil.isNotBlank(caseInstancePageRequest.getKeyword()), CaseInstanceEntity::getCaseName, caseInstancePageRequest.getKeyword())
                .page(pageRequest);
        Page<CaseInstancePageResponse> result = baseBiz.convertPage(pageResult, CaseInstancePageResponse.class);
        fillResult(result, caseInstancePageRequest.getAccountId());
        return result;
    }

    private void fillResult(Page<CaseInstancePageResponse> result, String requestAccountId) {
        if (BeanUtil.isEmpty(result)) {
            throw new BizException(MaterialsESCEnum.DATA_NULL);
        }

        List<CaseInstancePageResponse> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return;
        }

        // set userName
        for (CaseInstancePageResponse record: records) {
            String accountId = record.getAccountId();
            String userName = baseBiz.getUserName(accountId);
            record.setUserName(userName);

            if (requestAccountId.equals(accountId)) {
                record.setCanOperate(Boolean.TRUE);
            } else {
                record.setCanOperate(Boolean.FALSE);
            }
        }
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

        CaseInstanceEntity caseInstanceEntity = getById(caseInstanceId);
        result = BeanUtil.copyProperties(caseInstanceEntity, CaseInstanceResponse.class);
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
    public CaseInstanceEntity getById(String caseInstanceId) {
        LambdaQueryWrapper<CaseInstanceEntity> queryWrapper = new LambdaQueryWrapper<CaseInstanceEntity>()
                .eq(CaseInstanceEntity::getCaseInstanceId, caseInstanceId);
        return caseInstanceService.getOne(queryWrapper);
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

        return changeEnable(caseInstanceId, EnumStatus.ENABLE);
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

        return changeEnable(caseInstanceId, EnumStatus.DISABLE);
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

    private CaseInstanceEntity convertRequest2Entity(CaseInstanceRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        CaseInstanceEntity result = CaseInstanceEntity.builder()
                .appId(baseBiz.getAppId())
                .caseInstanceId(request.getCaseInstanceId())
                .caseMapBackground(request.getCaseMapBackground())
                .caseName(request.getCaseName())
                .casePic(request.getCasePic())
                .caseType(request.getCaseType())
                .descr(request.getDescr())
                .guide(request.getGuide())
                .accountId(request.getAccountId())
                .accountName(request.getAccountName())
                .state(request.getState())
                .build();

        String uniqueId = result.getCaseInstanceId();
        if (StrUtil.isBlank(uniqueId)) {
            result.setCaseInstanceId(baseBiz.getIdStr());
            result.setCaseIdentifier(baseBiz.getIdStr());
            result.setVer(baseBiz.getLastVer());

            // set default setting
            CaseSettingRequest caseSettingRequest = CaseSettingRequest.builder()
                    .caseInstanceId(result.getCaseInstanceId())
                    .caseSettingId(null)
                    .scoreMode(CaseScoreModeEnum.STRICT.name())
                    .allotMode(CaseQuestionnaireDistributionEnum.RANDOM.name())
                    .ext(null)
                    .build();
            caseSettingBiz.saveOrUpdCaseSetting(caseSettingRequest);
        } else {
            CaseInstanceEntity entity = getById(uniqueId);
            if (BeanUtil.isEmpty(entity)) {
                throw new BizException(CaseESCEnum.DATA_NULL);
            }
            result.setId(entity.getId());
        }
        return result;
    }

    private CaseInstanceEntity copyCaseInstance0(String oriCaseInstanceId, String caseInstanceName) {
        // get ori
        CaseInstanceEntity oriEntity = getById(oriCaseInstanceId);
        // copy
        CaseInstanceEntity newEntity = BeanUtil.copyProperties(oriEntity, CaseInstanceEntity.class);
        newEntity.setId(null);
        newEntity.setCaseInstanceId(baseBiz.getIdStr());
        newEntity.setCaseIdentifier(baseBiz.getIdStr());
        newEntity.setVer(baseBiz.getLastVer());
        newEntity.setCaseName(caseInstanceName);
        caseInstanceService.save(newEntity);
        return newEntity;
    }

    private boolean changeEnable(String caseInstanceId, EnumStatus enable) {
        LambdaUpdateWrapper<CaseInstanceEntity> updateWrapper = new LambdaUpdateWrapper<CaseInstanceEntity>()
                .eq(CaseInstanceEntity::getCaseInstanceId, caseInstanceId)
                .set(CaseInstanceEntity::getState, enable.getCode());
        return caseInstanceService.update(updateWrapper);
    }
}