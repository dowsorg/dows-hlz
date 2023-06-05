package org.dows.hep.biz.tenant.experiment;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.response.CaseOrgQuestionnaireResponse;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.ExptQuestionnaireStateEnum;
import org.dows.hep.biz.tenant.casus.TenantCaseOrgQuestionnaireBiz;
import org.dows.hep.entity.ExperimentQuestionnaireEntity;
import org.dows.hep.service.ExperimentQuestionnaireService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fhb
 * @description 对应于案例处知识考点
 * @date 2023/6/3 14:19
 */
@Service
@RequiredArgsConstructor
public class ExperimentQuestionnaireManageBiz {
    private final IdGenerator idGenerator;
    private final TenantCaseOrgQuestionnaireBiz tenantCaseOrgQuestionnaireBiz;
    private final ExperimentQuestionnaireService experimentQuestionnaireService;

    /**
     * @param
     * @return
     * @author fhb
     * @description 预生成知识考点问卷
     * @date 2023/6/3 15:33
     */
    public void preHandleExperimentQuestionnaire(String experimentInstanceId, String caseInstanceId, List<String> experimentGroupIds) {
        Assert.notNull(experimentInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(caseInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notEmpty(experimentGroupIds, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());

        // 期数-机构分组
        Map<String, Map<String, CaseOrgQuestionnaireResponse>> periodOrgCollect = tenantCaseOrgQuestionnaireBiz.listSelectedQuestionnaires(caseInstanceId);
        if (CollUtil.isEmpty(periodOrgCollect)) {
            return;
        }

        // 为每个小组分配试卷
        List<ExperimentQuestionnaireEntity> entityList = new ArrayList<>();
        experimentGroupIds.forEach(groupId -> {
            periodOrgCollect.forEach((period, orgCollect) -> {
                if (!orgCollect.isEmpty()) {
                    orgCollect.forEach((org, orgQuestionnaire) -> {
                        String caseQuestionnaireId = orgQuestionnaire.getCaseQuestionnaireId();
                        ExperimentQuestionnaireEntity entity = ExperimentQuestionnaireEntity.builder()
                                .experimentQuestionnaireId(idGenerator.nextIdStr())
                                .experimentInstanceId(experimentInstanceId)
                                .experimentOrgId(org)
                                .caseQuestionnaireId(caseQuestionnaireId)
                                .questionSectionResultId(null)
                                .periods(period)
                                .experimentGroupId(groupId)
                                .experimentAccountId(null)
                                .state(ExptQuestionnaireStateEnum.NOT_STARTED.getCode())
                                .build();
                        entityList.add(entity);
                    });
                }
            });
        });

        experimentQuestionnaireService.saveBatch(entityList);
    }
}
