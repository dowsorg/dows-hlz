package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.annotation.CalcCode;
import org.dows.hep.api.base.indicator.response.ExperimentRankGroupItemResponse;
import org.dows.hep.api.base.indicator.response.ExperimentRankItemResponse;
import org.dows.hep.api.base.indicator.response.ExperimentRankResponse;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.ExperimentScoringException;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.entity.ExperimentScoringEntity;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.service.ExperimentScoringService;
import org.dows.hep.service.ExperimentSettingService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实验计分BIZ
 * todo
 * post/healthIndexScoring/健康指数分数计算
 * post/knowledgeScoring/知识考点分数得分
 * post/treatmentPercentScoring/医疗占比得分
 * post/operateRightScoring/操作准确度得分
 * post//竞争性得分
 * post/totalScoring/总分
 * 此处先计算分数，独立的功能点，供其他bean调用，并将生成的数据保存，用于排行和出报告
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentScoringBiz {

    private final ExperimentGroupBiz experimentGroupBiz;
    private final ExperimentQuestionnaireScoreBiz experimentQuestionnaireScoreBiz;
    private final IdGenerator idGenerator;
    private final ExperimentSettingBiz experimentSettingBiz;
    private final ExperimentSettingService experimentSettingService;

    private final ExperimentScoringService experimentScoringService;
    private final ExperimentTimerBiz experimentTimerBiz;

    public void saveOrUpd(String experimentInstanceId, Integer period) {
        // 获取该实验的实验小组
        List<ExperimentGroupResponse> experimentGroupResponses = experimentGroupBiz.listGroup(experimentInstanceId);
        if (CollUtil.isEmpty(experimentGroupResponses)) {
            return;
        }

        // 获取 该实验该期的 `知识答题得分`， 按照实验小组来分组
        Map<String, BigDecimal> questionnaireScoreMap = experimentQuestionnaireScoreBiz.listExptQuestionnaireScore(experimentInstanceId, period);
        // 获取 该实验该期的 `知识答题得分`， 按照实验小组来分组
        // 获取 该实验该期的 `知识答题得分`， 按照实验小组来分组
        // 获取 该实验该期的 `知识答题得分`， 按照实验小组来分组

        // 获取 该实验该期的 `得分记录`
        List<ExperimentScoringEntity> oriEntityList = listExptScoring(experimentInstanceId, period);
        // 按照 `实验小组` 分组
        Map<String, ExperimentScoringEntity> oriGroupIdCollect = new HashMap<>();
        if (CollUtil.isNotEmpty(oriEntityList)) {
            oriGroupIdCollect = oriEntityList.stream()
                    .collect(Collectors.toMap(ExperimentScoringEntity::getExperimentGroupId, item -> item, (v1, v2) -> v1));
        }

        // 存表
        List<ExperimentScoringEntity> result = new ArrayList<>();
        for (ExperimentGroupResponse group : experimentGroupResponses) {
            String experimentGroupId = group.getExperimentGroupId();
            // 该组 `知识考点` 得分
            BigDecimal questionnaireScore = questionnaireScoreMap.get(experimentGroupId);
            String knowledgeScore = String.valueOf(questionnaireScore.floatValue());

            ExperimentScoringEntity oriEntity = oriGroupIdCollect.get(experimentGroupId);
            if (BeanUtil.isEmpty(oriEntity)) {
                ExperimentScoringEntity entity = ExperimentScoringEntity.builder()
                        .experimentScoringId(idGenerator.nextIdStr())
                        .experimentInstanceId(experimentInstanceId)
                        .experimentGroupId(experimentGroupId)
                        .knowledgeScore(knowledgeScore)
                        .scoringCount(1)
                        .periods(period)
                        .build();
                result.add(entity);
            } else {
                Integer scoringCount = oriEntity.getScoringCount() == null ? 1 : oriEntity.getScoringCount();
                oriEntity.setScoringCount(scoringCount);
                oriEntity.setKnowledgeScore(knowledgeScore);
                result.add(oriEntity);
            }
        }
        experimentScoringService.saveOrUpdateBatch(result);
    }

    private List<ExperimentScoringEntity> listExptScoring(String exptInstanceId, Integer period) {
        return experimentScoringService.lambdaQuery()
                .eq(ExperimentScoringEntity::getExperimentInstanceId, exptInstanceId)
                .eq(ExperimentScoringEntity::getPeriods, period)
                .list();
    }


    /**
     * 知识考点分数得分，并存表
     */
    @CalcCode(code = EnumCalcCode.hepKnowledgeCalculator)
    public void hepKnowledgeScoring() {
        //experimentSettingBiz.getCalcRule(experimentInstanceId);
        //todo logic clac

    }

    /**
     * 医疗占比得分，并存表
     */
    @CalcCode(code = EnumCalcCode.hepTreatmentPercentCalculator)
    public void hepTreatmentPercentScoring() {
        //experimentSettingBiz.getCalcRule(experimentInstanceId);
    }

    /**
     * 操作准确度得分，并存表
     */
    @CalcCode(code = EnumCalcCode.hepOperateRightCalculator)
    public void hepOperateRightScoring() {
        //experimentSettingBiz.getCalcRule(experimentInstanceId);

    }

    /**
     * 总分，并存表
     */
    @CalcCode(code = EnumCalcCode.hepTotalScoreCalculator)
    public void hepTotalScoring() {

    }

    public ExperimentRankResponse getRank(String experimentId) {
        Map<Integer, ExperimentRankItemResponse> kPeriodVExperimentRankItemResponseMap = new HashMap<>();
        List<ExperimentRankItemResponse> experimentRankItemResponseList = new ArrayList<>();
        ExperimentSettingEntity experimentSettingEntity = experimentSettingService.lambdaQuery()
            .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentId)
            .eq(ExperimentSettingEntity::getConfigKey, ExperimentSetting.SandSetting.class.getName())
            .oneOpt()
            .orElseThrow(() -> {
                log.error("experimentId:{} has no sandSetting", experimentId);
                throw new ExperimentScoringException(EnumESC.VALIDATE_EXCEPTION);
            });
        ExperimentSetting.SandSetting sandSetting = JSONUtil.toBean(experimentSettingEntity.getConfigJsonVals(), ExperimentSetting.SandSetting.class);
        Integer periods = sandSetting.getPeriods();
        for (int i = 1; i <= periods; i++) {
            ExperimentRankItemResponse experimentRankItemResponse = ExperimentRankItemResponse
                .builder()
                .periods(i)
                .experimentRankGroupItemResponseList(new ArrayList<>())
                .build();
            kPeriodVExperimentRankItemResponseMap.put(i, experimentRankItemResponse);
        }
        Map<Integer, Map<String, ExperimentScoringEntity>> kPeriodVKExperimentGroupIdVExperimentScoringEntityMap = new HashMap<>();
        experimentScoringService.lambdaQuery()
            .eq(ExperimentScoringEntity::getExperimentInstanceId, experimentId)
            .list()
            .forEach(experimentScoringEntity -> {
                String experimentGroupId = experimentScoringEntity.getExperimentGroupId();
                Integer periods1 = experimentScoringEntity.getPeriods();
                Map<String, ExperimentScoringEntity> kExperimentGroupIdVExperimentScoringEntityMap = kPeriodVKExperimentGroupIdVExperimentScoringEntityMap.get(periods1);
                if (Objects.isNull(kExperimentGroupIdVExperimentScoringEntityMap)) {
                    kExperimentGroupIdVExperimentScoringEntityMap = new HashMap<>();
                }
                ExperimentScoringEntity experimentScoringEntity1 = kExperimentGroupIdVExperimentScoringEntityMap.get(experimentGroupId);
                if (Objects.isNull(experimentScoringEntity1)) {
                    kExperimentGroupIdVExperimentScoringEntityMap.put(experimentGroupId, experimentScoringEntity);
                } else {
                    Integer scoringCount = experimentScoringEntity.getScoringCount();
                    Integer scoringCount1 = experimentScoringEntity1.getScoringCount();
                    if (scoringCount > scoringCount1) {
                        kExperimentGroupIdVExperimentScoringEntityMap.put(experimentGroupId, experimentScoringEntity);
                    }
                }
                kPeriodVKExperimentGroupIdVExperimentScoringEntityMap.put(periods1, kExperimentGroupIdVExperimentScoringEntityMap);
            });
        kPeriodVKExperimentGroupIdVExperimentScoringEntityMap.forEach((period, kExperimentGroupIdVExperimentScoringEntityMap) -> {
            ExperimentRankItemResponse experimentRankItemResponse = kPeriodVExperimentRankItemResponseMap.get(period);
            List<ExperimentRankGroupItemResponse> experimentRankGroupItemResponseList = experimentRankItemResponse.getExperimentRankGroupItemResponseList();
            kExperimentGroupIdVExperimentScoringEntityMap.forEach((experimentGroupId, experimentScoringEntity) -> {
                experimentRankGroupItemResponseList.add(ExperimentRankGroupItemResponse
                    .builder()
                    .experimentGroupName(experimentScoringEntity.getExperimentGroupName())
                    .healthIndexScore(experimentScoringEntity.getHealthIndexScore())
                    .knowledgeScore(experimentScoringEntity.getKnowledgeScore())
                    .treatmentPercentScore(experimentScoringEntity.getTreatmentPercentScore())
                    .totalScore(experimentScoringEntity.getTotalScore())
                    .periods(experimentScoringEntity.getPeriods())
                    .build());
            });
            experimentRankGroupItemResponseList.sort(Comparator.comparing(ExperimentRankGroupItemResponse::getTotalScore));
            experimentRankItemResponse.setExperimentRankGroupItemResponseList(experimentRankGroupItemResponseList);
            kPeriodVExperimentRankItemResponseMap.put(period, experimentRankItemResponse);
        });
        /* runsix:sort */
        kPeriodVExperimentRankItemResponseMap.forEach((period, experimentRankItemResponse) -> {
            experimentRankItemResponseList.add(experimentRankItemResponse);
        });
        experimentRankItemResponseList.sort(Comparator.comparingInt(ExperimentRankItemResponse::getPeriods));
        experimentRankItemResponseList.forEach(experimentRankItemResponse -> {
            List<ExperimentRankGroupItemResponse> experimentRankGroupItemResponseList = experimentRankItemResponse.getExperimentRankGroupItemResponseList();
            if (Objects.nonNull(experimentRankGroupItemResponseList)) {
                experimentRankGroupItemResponseList.sort((a, b) -> {
                    double result = Double.parseDouble(a.getTotalScore()) - Double.parseDouble(b.getTotalScore());
                    if (0 == result) {
                        return 0;
                    } else if (result > 0) {
                        return -1;
                    } else {
                        return 1;
                    }
                });
                experimentRankItemResponse.setExperimentRankGroupItemResponseList(experimentRankGroupItemResponseList);
            }
        });
        return ExperimentRankResponse
            .builder()
            .experimentRankItemResponseList(experimentRankItemResponseList)
            .build();
    }
}
