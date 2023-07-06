package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.annotation.CalcCode;
import org.dows.hep.api.base.indicator.request.RsCalculateCompetitiveScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsCalculateMoneyScoreRequestRs;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.ExperimentScoringException;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.biz.base.indicator.RsCalculateBiz;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentScoringEntity;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentScoringService;
import org.dows.hep.service.ExperimentSettingService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final ExperimentGroupService experimentGroupService;
    private final RsCalculateBiz rsCalculateBiz;

    private BigDecimal getTotalScore(
        BigDecimal knowledgeWeight, BigDecimal knowledgeScore,
        BigDecimal healthIndexWeight, BigDecimal healthIndexScore,
        BigDecimal medicalRatioWeight, BigDecimal medicalRatioScore
    ) {
        BigDecimal finalKnowledgeScore = knowledgeScore.multiply(knowledgeWeight);
        BigDecimal finalHealthIndexScore = healthIndexScore.multiply(healthIndexWeight);
        BigDecimal finalMedicalRatioScoreScore = medicalRatioScore.multiply(medicalRatioWeight);
        return finalKnowledgeScore.add(finalHealthIndexScore).add(finalMedicalRatioScoreScore);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpd(String experimentInstanceId, Integer period) throws ExecutionException, InterruptedException {
        List<ExperimentGroupEntity> experimentGroupEntityList = new ArrayList<>();
        CompletableFuture<Void> populateExperimentGroupEntityListCF = getPopulateExperimentGroupEntityListCF(experimentInstanceId, experimentGroupEntityList);

        AtomicInteger scoringCountAtomicInteger = new AtomicInteger(1);
        CompletableFuture<Void> populateScoringCountCF = getPopulateScoringCountCF(scoringCountAtomicInteger, experimentInstanceId, period);

        AtomicReference<Float> knowledgeWeightAtomicReference = new AtomicReference<>(0F);
        AtomicReference<Float> healthIndexWeightAtomicReference = new AtomicReference<>(0F);
        AtomicReference<Float> medicalRatioWeightAtomicReference = new AtomicReference<>(0F);
        CompletableFuture<Void> populateWeightCF = getPopulateWeightCF(knowledgeWeightAtomicReference, healthIndexWeightAtomicReference, medicalRatioWeightAtomicReference, experimentInstanceId);

        Map<String, BigDecimal> questionnaireScoreMap = new HashMap<>();
        CompletableFuture<Void> populateQuestionnaireScoreMapCF = getPopulateQuestionnaireScoreMapCF(questionnaireScoreMap, experimentInstanceId, period);

        Map<String, BigDecimal> kExperimentGroupIdVGroupCompetitiveScoreMap = new HashMap<>();
        CompletableFuture<Void> populateKExperimentGroupIdVGroupCompetitiveScoreMapCF = getPopulateKExperimentGroupIdVGroupCompetitiveScoreMapCF(kExperimentGroupIdVGroupCompetitiveScoreMap, experimentInstanceId, period);

        Map<String, BigDecimal> kExperimentGroupIdVGroupMoneyScoreMap = new HashMap<>();
        CompletableFuture<Void> populateKExperimentGroupIdVGroupMoneyScoreMapCF = getPopulateKExperimentGroupIdVGroupMoneyScoreMapCF(kExperimentGroupIdVGroupMoneyScoreMap, experimentInstanceId, period);

        CompletableFuture.allOf(populateExperimentGroupEntityListCF, populateScoringCountCF, populateWeightCF,
            populateQuestionnaireScoreMapCF, populateKExperimentGroupIdVGroupCompetitiveScoreMapCF, populateKExperimentGroupIdVGroupMoneyScoreMapCF).get();

        List<ExperimentScoringEntity> experimentScoringEntityList = new ArrayList<>();
        CompletableFuture<Void> populateExperimentScoringEntityListCF = getPopulateExperimentScoringEntityListCF(
            experimentScoringEntityList, experimentGroupEntityList, scoringCountAtomicInteger,
            knowledgeWeightAtomicReference, healthIndexWeightAtomicReference, medicalRatioWeightAtomicReference,
            questionnaireScoreMap, kExperimentGroupIdVGroupCompetitiveScoreMap, kExperimentGroupIdVGroupMoneyScoreMap,
            experimentInstanceId, period
        );
        populateExperimentScoringEntityListCF.get();
        experimentScoringService.saveOrUpdateBatch(experimentScoringEntityList);
    }

    private CompletableFuture<Void> getPopulateExperimentGroupEntityListCF(String experimentInstanceId, List<ExperimentGroupEntity> experimentGroupEntityList) {
        return CompletableFuture.runAsync(() -> {
            experimentGroupEntityList.addAll(experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, experimentInstanceId)
                .list());
        });
    }

    private CompletableFuture<Void> getPopulateScoringCountCF(AtomicInteger scoringCountAtomicInteger, String experimentInstanceId, Integer period) {
        return CompletableFuture.runAsync(() -> {
            experimentScoringService.lambdaQuery()
                .eq(ExperimentScoringEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentScoringEntity::getPeriods, period)
                .list()
                .stream()
                .map(ExperimentScoringEntity::getScoringCount)
                .max(Integer::compareTo)
                .ifPresent(a -> scoringCountAtomicInteger.set(a + 1));
        });
    }

    private CompletableFuture<Void> getPopulateWeightCF(
        AtomicReference<Float> knowledgeWeightAtomicReference,
        AtomicReference<Float> healthIndexWeightAtomicReference,
        AtomicReference<Float> medicalRatioWeightAtomicReference,
        String experimentInstanceId
        ) {
        return CompletableFuture.runAsync(() -> {
            experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentSettingEntity::getConfigKey, ExperimentSetting.SandSetting.class.getName())
                .oneOpt()
                .ifPresent(experimentSettingEntity -> {
                    ExperimentSetting.SandSetting sandSetting = JSONUtil.toBean(experimentSettingEntity.getConfigJsonVals(), ExperimentSetting.SandSetting.class);
                    knowledgeWeightAtomicReference.set(sandSetting.getKnowledgeWeight());
                    healthIndexWeightAtomicReference.set(sandSetting.getHealthIndexWeight());
                    medicalRatioWeightAtomicReference.set(sandSetting.getMedicalRatioWeight());
                });
        });
    }

    private CompletableFuture<Void> getPopulateQuestionnaireScoreMapCF(
        Map<String, BigDecimal> questionnaireScoreMap, String experimentInstanceId, Integer period) {
        return CompletableFuture.runAsync(() -> {
            questionnaireScoreMap.putAll(experimentQuestionnaireScoreBiz.listExptQuestionnaireScore(experimentInstanceId, period));
        });
    }

    private CompletableFuture<Void> getPopulateKExperimentGroupIdVGroupCompetitiveScoreMapCF(
        Map<String, BigDecimal> kExperimentGroupIdVGroupCompetitiveScoreMap, String experimentInstanceId, Integer period) {
        return CompletableFuture.runAsync(() -> {
            RsCalculateCompetitiveScoreRsResponse rsCalculateCompetitiveScoreRsResponse = rsCalculateBiz.rsCalculateCompetitiveScore(RsCalculateCompetitiveScoreRequestRs
                .builder()
                .experimentId(experimentInstanceId)
                .periods(period)
                .build());
            List<GroupCompetitiveScoreRsResponse> groupCompetitiveScoreRsResponseList = rsCalculateCompetitiveScoreRsResponse.getGroupCompetitiveScoreRsResponseList();
            if (Objects.nonNull(groupCompetitiveScoreRsResponseList) && groupCompetitiveScoreRsResponseList.isEmpty()) {
                groupCompetitiveScoreRsResponseList.forEach(groupCompetitiveScoreRsResponse -> {
                    String experimentGroupId = groupCompetitiveScoreRsResponse.getExperimentGroupId();
                    BigDecimal groupCompetitiveScore = groupCompetitiveScoreRsResponse.getGroupCompetitiveScore();
                    kExperimentGroupIdVGroupCompetitiveScoreMap.put(experimentGroupId, groupCompetitiveScore);
                });
            }
        });
    }

    private CompletableFuture<Void> getPopulateKExperimentGroupIdVGroupMoneyScoreMapCF(
        Map<String, BigDecimal> kExperimentGroupIdVGroupMoneyScoreMap, String experimentInstanceId, Integer period
    ) {
        return CompletableFuture.runAsync(() -> {
            RsCalculateMoneyScoreRsResponse rsCalculateMoneyScoreRsResponse = rsCalculateBiz.rsCalculateMoneyScore(RsCalculateMoneyScoreRequestRs
                .builder()
                .experimentId(experimentInstanceId)
                .periods(period)
                .build());
            List<GroupMoneyScoreRsResponse> groupMoneyScoreRsResponseList = rsCalculateMoneyScoreRsResponse.getGroupMoneyScoreRsResponseList();
            if (Objects.isNull(groupMoneyScoreRsResponseList) || groupMoneyScoreRsResponseList.isEmpty()) {
                groupMoneyScoreRsResponseList.forEach(groupMoneyScoreRsResponse -> {
                    String experimentGroupId = groupMoneyScoreRsResponse.getExperimentGroupId();
                    BigDecimal groupMoneyScore = groupMoneyScoreRsResponse.getGroupMoneyScore();
                    kExperimentGroupIdVGroupMoneyScoreMap.put(experimentGroupId, groupMoneyScore);
                });
            }
        });
    }

    private CompletableFuture<Void> getPopulateExperimentScoringEntityListCF(
        List<ExperimentScoringEntity> experimentScoringEntityList,
        List<ExperimentGroupEntity> experimentGroupEntityList,
        AtomicInteger scoringCountAtomicInteger,
        AtomicReference<Float> knowledgeWeightAtomicReference,
        AtomicReference<Float> healthIndexWeightAtomicReference,
        AtomicReference<Float> medicalRatioWeightAtomicReference,
        Map<String, BigDecimal> questionnaireScoreMap,
        Map<String, BigDecimal> kExperimentGroupIdVGroupCompetitiveScoreMap,
        Map<String, BigDecimal> kExperimentGroupIdVGroupMoneyScoreMap,
        String experimentInstanceId,
        Integer period
    ) {
        return CompletableFuture.runAsync(() -> {
            experimentGroupEntityList.forEach(experimentGroupEntity -> {
                String experimentGroupId = experimentGroupEntity.getExperimentGroupId();
                String groupName = experimentGroupEntity.getGroupName();
                BigDecimal questionnaireScoreBigDecimal = questionnaireScoreMap.get(experimentGroupId);
                if (Objects.isNull(questionnaireScoreBigDecimal)) {
                    questionnaireScoreBigDecimal = BigDecimal.ZERO;
                }
                BigDecimal groupCompetitiveScoreBigDecimal = kExperimentGroupIdVGroupCompetitiveScoreMap.get(experimentGroupId);
                if (Objects.isNull(groupCompetitiveScoreBigDecimal)) {
                    groupCompetitiveScoreBigDecimal = BigDecimal.ZERO;
                }
                BigDecimal groupIdVGroupMoneyScoreBigDecimal = kExperimentGroupIdVGroupMoneyScoreMap.get(experimentGroupId);
                if (Objects.isNull(groupIdVGroupMoneyScoreBigDecimal)) {
                    groupIdVGroupMoneyScoreBigDecimal = BigDecimal.ZERO;
                }
                BigDecimal totalScoreBigDecimal = getTotalScore(
                    BigDecimal.valueOf(knowledgeWeightAtomicReference.get()), questionnaireScoreBigDecimal,
                    BigDecimal.valueOf(healthIndexWeightAtomicReference.get()), groupCompetitiveScoreBigDecimal,
                    BigDecimal.valueOf(medicalRatioWeightAtomicReference.get()), groupIdVGroupMoneyScoreBigDecimal
                );
                experimentScoringEntityList.add(ExperimentScoringEntity
                    .builder()
                    .experimentScoringId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstanceId)
                    .experimentGroupId(experimentGroupId)
                    .experimentGroupName(groupName)
                    .knowledgeScore(questionnaireScoreBigDecimal.setScale(2, RoundingMode.DOWN).toString())
                    .healthIndexScore(groupCompetitiveScoreBigDecimal.setScale(2, RoundingMode.DOWN).toString())
                    .treatmentPercentScore(groupIdVGroupMoneyScoreBigDecimal.setScale(2, RoundingMode.DOWN).toString())
                    .totalScore(totalScoreBigDecimal.setScale(2, RoundingMode.DOWN).toString())
                    .scoringCount(scoringCountAtomicInteger.get())
                    .periods(period)
                    .build());
            });
        });
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
        List<ExperimentTotalRankItemResponse> experimentTotalRankItemResponseList = new ArrayList<>();
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
            List<ExperimentTotalRankGroupItemResponse> experimentTotalRankGroupItemResponseList = new ArrayList<>();
            AtomicReference<String> atomicReferenceExperimentGroupName = new AtomicReference<>();
            AtomicReference<Double> atomicReferenceAllPeriodsTotalScore = new AtomicReference<>(0D);
            kPeriodVExperimentScoringEntityMap.forEach((period, experimentScoringEntity) -> {
                if (StringUtils.isBlank(atomicReferenceExperimentGroupName.get())) {
                    atomicReferenceExperimentGroupName.set(experimentScoringEntity.getExperimentGroupName());
                }
                Double currentTotalScore = atomicReferenceAllPeriodsTotalScore.get();
                String totalScore = experimentScoringEntity.getTotalScore();
                Float weight = kPeriodVWeightMap.get(period.toString());
                currentTotalScore += Double.parseDouble(totalScore)*weight/100;
                atomicReferenceAllPeriodsTotalScore.set(currentTotalScore);
                experimentTotalRankGroupItemResponseList.add(ExperimentTotalRankGroupItemResponse
                    .builder()
                    .totalScore(experimentScoringEntity.getTotalScore())
                    .periods(period)
                    .build());
            });
            experimentTotalRankGroupItemResponseList.sort(Comparator.comparing(ExperimentTotalRankGroupItemResponse::getPeriods));
            experimentTotalRankItemResponseList.add(ExperimentTotalRankItemResponse
                .builder()
                .experimentGroupName(atomicReferenceExperimentGroupName.get())
                .allPeriodsTotalScore(atomicReferenceAllPeriodsTotalScore.get().toString())
                .experimentTotalRankGroupItemResponseList(experimentTotalRankGroupItemResponseList)
                .build());
        });
        /* runsix:算每期列表 */
        kPeriodVKExperimentGroupIdVExperimentScoringEntityMap.forEach((period, kExperimentGroupIdVExperimentScoringEntityMap) -> {
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
            Collections.reverse(experimentRankGroupItemResponseList);
            experimentRankItemResponse.setExperimentRankGroupItemResponseList(experimentRankGroupItemResponseList);
        });

        /* runsix:sort */
        experimentTotalRankItemResponseList.sort(Comparator.comparing(a -> Double.parseDouble(a.getAllPeriodsTotalScore())));
        Collections.reverse(experimentTotalRankItemResponseList);
        experimentTotalRankItemResponseList.forEach(experimentTotalRankItemResponse -> {
            List<ExperimentTotalRankGroupItemResponse> experimentTotalRankGroupItemResponseList = experimentTotalRankItemResponse.getExperimentTotalRankGroupItemResponseList();
            experimentTotalRankGroupItemResponseList.sort(Comparator.comparingInt(ExperimentTotalRankGroupItemResponse::getPeriods));
            experimentTotalRankItemResponse.setExperimentTotalRankGroupItemResponseList(experimentTotalRankGroupItemResponseList);
        });
        return ExperimentRankResponse
            .builder()
            .totalPeriod(totalPeriods)
            .experimentRankItemResponseList(experimentRankItemResponseList)
            .experimentTotalRankItemResponseList(experimentTotalRankItemResponseList)
            .build();
    }
}
