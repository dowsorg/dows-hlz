package org.dows.hep.biz.tenant.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.response.QuestionOptionWithAnswerResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.api.tenant.casus.CasePeriodEnum;
import org.dows.hep.api.tenant.casus.response.CaseOrgQuestionnaireResponse;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnaireResponse;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.ExptQuestionnaireStateEnum;
import org.dows.hep.api.user.experiment.dto.ExptQuestionnaireOptionDTO;
import org.dows.hep.biz.tenant.casus.TenantCaseOrgQuestionnaireBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseQuestionnaireBiz;
import org.dows.hep.entity.ExperimentQuestionnaireEntity;
import org.dows.hep.entity.ExperimentQuestionnaireItemEntity;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.service.ExperimentQuestionnaireItemService;
import org.dows.hep.service.ExperimentQuestionnaireService;
import org.dows.hep.service.ExperimentSettingService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final ExperimentSettingService experimentSettingService;

    /**
     * @param
     * @return
     * @author fhb
     * @description 预生成知识考点问卷-分配实验的时候调用
     * @date 2023/6/3 15:33
     */
    public void preHandleExperimentQuestionnaire(String experimentInstanceId, String caseInstanceId) {
        // 没有包含沙盘模式就退出
        String sandSetting = getSandSetting(experimentInstanceId);
        if (StrUtil.isBlank(sandSetting)) {
            return;
        }

        List<String> experimentGroupIds = baseBiz.listExperimentGroupIds(experimentInstanceId);
        preHandleExperimentQuestionnaire(experimentInstanceId, caseInstanceId, experimentGroupIds);
    }

    private String getSandSetting(String experimentInstanceId) {
        String sandSetting = "";
        List<ExperimentSettingEntity> experimentSettings = experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                .list();
        for (ExperimentSettingEntity expSetting : experimentSettings) {
            String configKey = expSetting.getConfigKey();
            if (ExperimentSetting.SandSetting.class.getName().equals(configKey)) {
                sandSetting = expSetting.getConfigJsonVals();
            }
        }
        return sandSetting;
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
                .filter(StrUtil::isNotBlank)
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
        experimentGroupIds.forEach(groupId -> periodOrgCollect.forEach((period, orgCollect) -> {
            if (!orgCollect.isEmpty()) {
                orgCollect.forEach((org, orgQuestionnaire) -> {
                    if (BeanUtil.isNotEmpty(orgQuestionnaire)) {
                        // experiment-questionnaire
                        ExperimentQuestionnaireEntity entity = ExperimentQuestionnaireEntity.builder()
                                .experimentQuestionnaireId(baseBiz.getIdStr())
                                .experimentInstanceId(experimentInstanceId)
                                .periods(period)
                                .periodSequence(CasePeriodEnum.getByCode(period).getSeq())
                                .experimentOrgId(org)
                                .experimentGroupId(groupId)
                                .experimentAccountId(null)
                                .questionnaireName(orgQuestionnaire.getQuestionSectionName())
                                .state(ExptQuestionnaireStateEnum.NOT_STARTED.getCode())
                                .build();
                        entityList.add(entity);

                        // experiment-questionnaire-item
                        if (CollUtil.isNotEmpty(collect)) {
                            List<ExperimentQuestionnaireItemEntity> localItemList = new ArrayList<>();
                            // set questionnaire-item
                            QuestionSectionResponse questionSectionResponse = collect.get(orgQuestionnaire.getCaseQuestionnaireId());
                            List<QuestionSectionItemResponse> sectionItemList = Optional.ofNullable(questionSectionResponse)
                                    .map(QuestionSectionResponse::getSectionItemList)
                                    .orElse(new ArrayList<>());
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
                    }
                });
            }
        }));

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
                .questionCateg(questionResponse.getQuestionCategName())
                .questionType(questionResponse.getQuestionType())
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

        List<ExptQuestionnaireOptionDTO> options = new ArrayList<>();
        optionWithAnswerList.forEach(item -> {
            ExptQuestionnaireOptionDTO option = ExptQuestionnaireOptionDTO.builder()
                    .id(item.getQuestionOptionsId())
                    .title(item.getOptionTitle())
                    .value(item.getOptionValue())
                    .build();
            options.add(option);
        });
        return JSON.toJSONString(options);
    }

    private String getRightValues(List<QuestionOptionWithAnswerResponse> optionWithAnswerList) {
        if (CollUtil.isEmpty(optionWithAnswerList)) {
            return "";
        }

        List<ExptQuestionnaireOptionDTO> options = new ArrayList<>();
        optionWithAnswerList.forEach(item -> {
            if (item.getRightAnswer() != null && item.getRightAnswer() == Boolean.TRUE) {
                ExptQuestionnaireOptionDTO option = ExptQuestionnaireOptionDTO.builder()
                        .id(item.getQuestionOptionsId())
                        .title(item.getOptionTitle())
                        .value(item.getOptionValue())
                        .build();
                options.add(option);
            }
        });
        return JSON.toJSONString(options);
    }
}
