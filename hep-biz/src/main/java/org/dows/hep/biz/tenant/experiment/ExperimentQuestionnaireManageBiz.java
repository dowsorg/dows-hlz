package org.dows.hep.biz.tenant.experiment;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.response.QuestionOptionWithAnswerResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.api.tenant.casus.response.CaseOrgQuestionnaireResponse;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnaireResponse;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.ExptQuestionnaireStateEnum;
import org.dows.hep.biz.tenant.casus.TenantCaseOrgQuestionnaireBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseQuestionnaireBiz;
import org.dows.hep.entity.ExperimentQuestionnaireEntity;
import org.dows.hep.entity.ExperimentQuestionnaireItemEntity;
import org.dows.hep.service.ExperimentQuestionnaireItemService;
import org.dows.hep.service.ExperimentQuestionnaireService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description 对应于案例处知识考点
 * @date 2023/6/3 14:19
 */
@Service
@RequiredArgsConstructor
public class ExperimentQuestionnaireManageBiz {
    private final ExperimentManageBaseBiz baseBiz;
    private final TenantCaseOrgQuestionnaireBiz tenantCaseOrgQuestionnaireBiz;
    private final TenantCaseQuestionnaireBiz tenantCaseQuestionnaireBiz;
    private final ExperimentQuestionnaireService experimentQuestionnaireService;
    private final ExperimentQuestionnaireItemService experimentQuestionnaireItemService;

    /**
     * @param
     * @return
     * @author fhb
     * @description 预生成知识考点问卷-分配实验的时候调用
     * @date 2023/6/3 15:33
     */
    public void preHandleExperimentQuestionnaire(String experimentInstanceId, String caseInstanceId) {
        List<String> experimentGroupIds = baseBiz.listExperimentGroupIds(experimentInstanceId);
        preHandleExperimentQuestionnaire(experimentInstanceId, caseInstanceId, experimentGroupIds);
    }

    private void preHandleExperimentQuestionnaire(String experimentInstanceId, String caseInstanceId, List<String> experimentGroupIds) {
        Assert.notNull(experimentInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(caseInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notEmpty(experimentGroupIds, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());

        // 期数-机构分组
        Map<String, Map<String, CaseOrgQuestionnaireResponse>> periodOrgCollect = tenantCaseOrgQuestionnaireBiz.mapSelectedQuestionnaires(caseInstanceId);
        if (CollUtil.isEmpty(periodOrgCollect)) {
            return;
        }
        List<String> caseQuestionnaireIds = periodOrgCollect.values()
                .stream()
                .flatMap(item -> item.values().stream())
                .map(CaseOrgQuestionnaireResponse::getCaseQuestionnaireId)
                .toList();
        if (CollUtil.isEmpty(caseQuestionnaireIds)) {
            return;
        }
        List<CaseQuestionnaireResponse> caseQuestionnaireResponses = tenantCaseQuestionnaireBiz.listByIds(caseQuestionnaireIds);
        if (CollUtil.isEmpty(caseQuestionnaireResponses)) {
            return;
        }
        Map<String, QuestionSectionResponse> collect = caseQuestionnaireResponses.stream()
                .collect(Collectors.toMap(CaseQuestionnaireResponse::getCaseQuestionnaireId, CaseQuestionnaireResponse::getQuestionSectionResponse));


        // 为每个小组分配试卷
        List<ExperimentQuestionnaireEntity> entityList = new ArrayList<>();
        List<ExperimentQuestionnaireItemEntity> itemEntityList = new ArrayList<>();
        experimentGroupIds.forEach(groupId -> {
            periodOrgCollect.forEach((period, orgCollect) -> {
                if (!orgCollect.isEmpty()) {
                    orgCollect.forEach((org, orgQuestionnaire) -> {
                        // experiment-questionnaire
                        ExperimentQuestionnaireEntity entity = ExperimentQuestionnaireEntity.builder()
                                .experimentQuestionnaireId(baseBiz.getIdStr())
                                .experimentInstanceId(experimentInstanceId)
                                .periods(period)
                                .experimentOrgId(org)
                                .experimentGroupId(groupId)
                                .experimentAccountId(null)
                                .questionnaireName(orgQuestionnaire.getQuestionSectionName())
                                .state(ExptQuestionnaireStateEnum.NOT_STARTED.getCode())
                                .build();
                        entityList.add(entity);

                        // experiment-questionnaire-item
                        List<ExperimentQuestionnaireItemEntity> localItemList = new ArrayList<>();
                        String caseQuestionnaireId = orgQuestionnaire.getCaseQuestionnaireId();
                        if (CollUtil.isNotEmpty(collect)) {
                            // set item
                            QuestionSectionResponse questionSectionResponse = collect.get(caseQuestionnaireId);
                            List<QuestionSectionItemResponse> sectionItemList = questionSectionResponse.getSectionItemList();
                            if (CollUtil.isNotEmpty(sectionItemList)) {
                                sectionItemList.forEach(sectionItem -> {
                                    QuestionResponse question = sectionItem.getQuestion();
                                    List<ExperimentQuestionnaireItemEntity> itemEntities = convertToFlatList(question);
                                    localItemList.addAll(itemEntities);
                                });
                            }
                            // sort
                            for (int i = 0; i < localItemList.size(); i++) {
                                ExperimentQuestionnaireItemEntity item = localItemList.get(i);
                                item.setSeq(i);
                                item.setExperimentQuestionnaireId(entity.getExperimentQuestionnaireId());
                            }
                            itemEntityList.addAll(localItemList);
                        }
                    });
                }
            });
        });

        experimentQuestionnaireService.saveBatch(entityList);
        experimentQuestionnaireItemService.saveBatch(itemEntityList);
    }

    private List<ExperimentQuestionnaireItemEntity> convertToFlatList(QuestionResponse questionResponse) {
        List<ExperimentQuestionnaireItemEntity> flatList = new ArrayList<>();
        flattenTree(questionResponse, flatList, "0");
        return flatList;
    }

    private void flattenTree(QuestionResponse questionResponse, List<ExperimentQuestionnaireItemEntity> flatList, String pid) {
        // 处理当前节点
        List<QuestionOptionWithAnswerResponse> optionWithAnswerList = questionResponse.getOptionWithAnswerList();
        String options = getOptions(optionWithAnswerList);
        String rightValues = getRightValues(optionWithAnswerList);
        ExperimentQuestionnaireItemEntity itemEntity = ExperimentQuestionnaireItemEntity.builder()
                .experimentQuestionnaireItemId(baseBiz.getIdStr())
                .experimentQuestionnaireItemPid(pid)
                .experimentQuestionnaireId(null)
                .questionTitle(questionResponse.getQuestionTitle())
                .questionDescr(questionResponse.getQuestionDescr())
                .questionOptions(options)
                .questionDetailedAnswer(questionResponse.getDetailedAnswer())
                .rightValue(rightValues)
                .seq(null)
                .questionResult(null)
                .build();
        flatList.add(itemEntity);

        // 判空
        List<QuestionResponse> children = questionResponse.getChildren();
        if (CollUtil.isEmpty(children)) {
            return;
        }

        // 处理子节点
        for (QuestionResponse child : questionResponse.getChildren()) {
            flattenTree(child, flatList, itemEntity.getExperimentQuestionnaireId());
        }
    }

    private String getOptions(List<QuestionOptionWithAnswerResponse> optionWithAnswerList) {
        if (CollUtil.isEmpty(optionWithAnswerList)) {
            return "";
        }

        ArrayList<Map> maps = new ArrayList<>();
        optionWithAnswerList.forEach(item -> {
            Map<String, String> map = new HashMap<>();
            map.put("title", item.getOptionTitle());
            map.put("value", item.getOptionValue());
            maps.add(map);
        });
        return JSON.toJSONString(maps);
    }

    private String getRightValues(List<QuestionOptionWithAnswerResponse> optionWithAnswerList) {
        if (CollUtil.isEmpty(optionWithAnswerList)) {
            return "";
        }

        return optionWithAnswerList.stream()
                .filter(QuestionOptionWithAnswerResponse::getRightAnswer)
                .map(QuestionOptionWithAnswerResponse::getOptionTitle)
                .collect(Collectors.joining(","));
    }
}
