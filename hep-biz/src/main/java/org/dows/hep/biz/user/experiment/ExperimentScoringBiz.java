package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.annotation.CalcCode;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.ExperimentScoringException;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.entity.ExperimentScoringEntity;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.service.ExperimentScoringService;
import org.dows.hep.service.ExperimentSettingService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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
        List<ExperimentRankItemResponse> experimentRankItemResponseList = new ArrayList<>();
        ExperimentTotalRankResponse experimentTotalRankResponse = ExperimentTotalRankResponse
            .builder()
            .experimentTotalRankItemResponseList(new ArrayList<>())
            .experimentAllPeriodTotalRankResponse(ExperimentAllPeriodTotalRankResponse
                .builder()
                .experimentTotalRankGroupItemResponseList(new ArrayList<>())
                .build())
            .build();
        Map<Integer, ExperimentRankItemResponse> kPeriodVExperimentRankItemResponseMap = new HashMap<>();
        ExperimentSettingEntity experimentSettingEntity = experimentSettingService.lambdaQuery()
            .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentId)
            .eq(ExperimentSettingEntity::getConfigKey, ExperimentSetting.SandSetting.class.getName())
            .oneOpt()
            .orElseThrow(() -> {
                log.error("experimentId:{} has no sandSetting", experimentId);
                throw new ExperimentScoringException(EnumESC.VALIDATE_EXCEPTION);
            });
        ExperimentSetting.SandSetting sandSetting = JSONUtil.toBean(experimentSettingEntity.getConfigJsonVals(), ExperimentSetting.SandSetting.class);
        Map<String, Float> kPeriodVWeightMap = sandSetting.getWeightMap();
        Integer totalPeriods = sandSetting.getPeriods();
        for (int i = 1; i <= totalPeriods; i++) {
            ExperimentRankItemResponse experimentRankItemResponse = ExperimentRankItemResponse
                .builder()
                .periods(i)
                .experimentRankGroupItemResponseList(new ArrayList<>())
                .build();
            kPeriodVExperimentRankItemResponseMap.put(i, experimentRankItemResponse);
        }
        Map<Integer, Map<String, ExperimentScoringEntity>> kPeriodVKExperimentGroupIdVExperimentScoringEntityMap = new HashMap<>();
        Map<String, Map<Integer, ExperimentScoringEntity>> kExperimentGroupIdVKPeriodVExperimentScoringEntityMap = new HashMap<>();
        experimentScoringService.lambdaQuery()
            .eq(ExperimentScoringEntity::getExperimentInstanceId, experimentId)
            .list()
            .forEach(experimentScoringEntity -> {
                String experimentGroupId = experimentScoringEntity.getExperimentGroupId();
                Integer periods1 = experimentScoringEntity.getPeriods();
                /* runsix:populate kExperimentGroupIdVKPeriodVExperimentScoringEntityMap */
                Map<Integer, ExperimentScoringEntity> kPeriodVExperimentScoringEntityMap = kExperimentGroupIdVKPeriodVExperimentScoringEntityMap.get(experimentGroupId);
                if (Objects.isNull(kPeriodVExperimentScoringEntityMap)) {
                    kPeriodVExperimentScoringEntityMap = new HashMap<>();
                }
                ExperimentScoringEntity experimentScoringEntity2 = kPeriodVExperimentScoringEntityMap.get(periods1);
                if (Objects.isNull(experimentScoringEntity2)) {
                    kPeriodVExperimentScoringEntityMap.put(periods1, experimentScoringEntity);
                } else {
                    Integer scoringCount = experimentScoringEntity.getScoringCount();
                    Integer scoringCount2 = experimentScoringEntity2.getScoringCount();
                    if (scoringCount > scoringCount2) {
                        kPeriodVExperimentScoringEntityMap.put(periods1, experimentScoringEntity);
                    }
                }
                kExperimentGroupIdVKPeriodVExperimentScoringEntityMap.put(experimentGroupId, kPeriodVExperimentScoringEntityMap);
                /* runsix:populate kPeriodVKExperimentGroupIdVExperimentScoringEntityMap */
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
        /* runsix:算所有期数得分 */
        kExperimentGroupIdVKPeriodVExperimentScoringEntityMap.forEach((experimentGroupId, kPeriodVExperimentScoringEntityMap) -> {
            ExperimentAllPeriodTotalRankResponse experimentAllPeriodTotalRankResponse1 = experimentTotalRankResponse.getExperimentAllPeriodTotalRankResponse();
            List<ExperimentTotalRankGroupItemResponse> experimentTotalRankGroupItemResponseList = experimentAllPeriodTotalRankResponse1.getExperimentTotalRankGroupItemResponseList();
            AtomicReference<String> atomicReferenceExperimentGroupName = new AtomicReference<>();
            AtomicReference<Double> atomicReferenceAllPeriodsTotalScore = new AtomicReference<>(0D);
            kPeriodVExperimentScoringEntityMap.forEach((period, experimentScoringEntity) -> {
                if (StringUtils.isBlank(atomicReferenceExperimentGroupName.get())) {
                    atomicReferenceExperimentGroupName.set(experimentScoringEntity.getExperimentGroupName());
                }
                Double currentTotalScore = atomicReferenceAllPeriodsTotalScore.get();
                String totalScore = experimentScoringEntity.getTotalScore();
                Float weight = kPeriodVWeightMap.get(period.toString());
                currentTotalScore =+ Double.parseDouble(totalScore)*weight;
                atomicReferenceAllPeriodsTotalScore.set(currentTotalScore);
            });
            experimentTotalRankGroupItemResponseList.add(ExperimentTotalRankGroupItemResponse
                .builder()
                .experimentGroupName(atomicReferenceExperimentGroupName.get())
                .totalScore(atomicReferenceAllPeriodsTotalScore.get().toString())
                .build());
            experimentAllPeriodTotalRankResponse1.setExperimentTotalRankGroupItemResponseList(experimentTotalRankGroupItemResponseList);
        });
        /* runsix:算每期列表 */
        kPeriodVKExperimentGroupIdVExperimentScoringEntityMap.forEach((period, kExperimentGroupIdVExperimentScoringEntityMap) -> {
            /* runsix:每一期总分列表数据 */
            List<ExperimentTotalRankItemResponse> experimentTotalRankItemResponseList = experimentTotalRankResponse.getExperimentTotalRankItemResponseList();
            List<ExperimentTotalRankGroupItemResponse> experimentTotalRankGroupItemResponseList = new ArrayList<>();
            /* runsix:12345期数据 */
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
                experimentTotalRankGroupItemResponseList.add(ExperimentTotalRankGroupItemResponse
                    .builder()
                    .experimentGroupName(experimentScoringEntity.getExperimentGroupName())
                    .totalScore(experimentScoringEntity.getTotalScore())
                    .periods(experimentScoringEntity.getPeriods())
                    .build());
            });
            experimentRankGroupItemResponseList.sort(Comparator.comparing(ExperimentRankGroupItemResponse::getTotalScore));
            experimentRankItemResponse.setExperimentRankGroupItemResponseList(experimentRankGroupItemResponseList);
            kPeriodVExperimentRankItemResponseMap.put(period, experimentRankItemResponse);
            ExperimentTotalRankItemResponse experimentTotalRankItemResponse = ExperimentTotalRankItemResponse
                .builder()
                .periods(period)
                .experimentTotalRankGroupItemResponseList(experimentTotalRankGroupItemResponseList)
                .build();
            experimentTotalRankItemResponseList.add(experimentTotalRankItemResponse);
            experimentTotalRankResponse.setExperimentTotalRankItemResponseList(experimentTotalRankItemResponseList);
        });
        /* runsix:sort */
        kPeriodVExperimentRankItemResponseMap.forEach((period, experimentRankItemResponse) -> {
            experimentRankItemResponseList.add(experimentRankItemResponse);
        });
        experimentRankItemResponseList.sort(Comparator.comparingInt(ExperimentRankItemResponse::getPeriods));
        experimentRankItemResponseList.forEach(experimentRankItemResponse -> {
            List<ExperimentRankGroupItemResponse> experimentRankGroupItemResponseList = experimentRankItemResponse.getExperimentRankGroupItemResponseList();
            Collections.reverse(experimentRankGroupItemResponseList);
            experimentRankItemResponse.setExperimentRankGroupItemResponseList(experimentRankGroupItemResponseList);
        });

        /* runsix:sort */
        List<ExperimentTotalRankItemResponse> experimentTotalRankItemResponseList = experimentTotalRankResponse.getExperimentTotalRankItemResponseList();
        experimentTotalRankItemResponseList.sort(Comparator.comparingInt(ExperimentTotalRankItemResponse::getPeriods));
        experimentTotalRankItemResponseList.forEach(experimentTotalRankItemResponse -> {
            List<ExperimentTotalRankGroupItemResponse> experimentTotalRankGroupItemResponseList = experimentTotalRankItemResponse.getExperimentTotalRankGroupItemResponseList();
            experimentTotalRankGroupItemResponseList.sort(Comparator.comparing(ExperimentTotalRankGroupItemResponse::getTotalScore));
            Collections.reverse(experimentTotalRankGroupItemResponseList);
            experimentTotalRankItemResponse.setExperimentTotalRankGroupItemResponseList(experimentTotalRankGroupItemResponseList);
        });
        experimentTotalRankResponse.setExperimentTotalRankItemResponseList(experimentTotalRankItemResponseList);
        ExperimentAllPeriodTotalRankResponse experimentAllPeriodTotalRankResponse = experimentTotalRankResponse.getExperimentAllPeriodTotalRankResponse();
        List<ExperimentTotalRankGroupItemResponse> experimentTotalRankGroupItemResponseList = experimentAllPeriodTotalRankResponse.getExperimentTotalRankGroupItemResponseList();
        experimentTotalRankGroupItemResponseList.sort(Comparator.comparing(ExperimentTotalRankGroupItemResponse::getTotalScore));
        Collections.reverse(experimentTotalRankGroupItemResponseList);
        experimentAllPeriodTotalRankResponse.setExperimentTotalRankGroupItemResponseList(experimentTotalRankGroupItemResponseList);
        experimentTotalRankResponse.setExperimentAllPeriodTotalRankResponse(experimentAllPeriodTotalRankResponse);
        return ExperimentRankResponse
            .builder()
            .totalPeriod(totalPeriods)
            .experimentRankItemResponseList(experimentRankItemResponseList)
            .experimentTotalRankResponse(experimentTotalRankResponse)
            .build();
    }
}
