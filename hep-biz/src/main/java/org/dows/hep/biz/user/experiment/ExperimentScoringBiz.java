package org.dows.hep.biz.user.experiment;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.Tag;
import org.apache.skywalking.apm.toolkit.trace.Tags;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.dows.hep.api.annotation.CalcCode;
import org.dows.hep.api.base.indicator.request.RsCalculateCompetitiveScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsCalculateMoneyScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsInitMoneyRequest;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.api.exception.ExperimentScoringException;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.dows.hep.biz.base.indicator.RsUtilBiz;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
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

    private final ExperimentQuestionnaireScoreBiz experimentQuestionnaireScoreBiz;
    private final IdGenerator idGenerator;
    private final ExperimentSettingService experimentSettingService;

    private final ExperimentRankingService experimentRankingService;

    private final ExperimentScoringService experimentScoringService;
    private final ExperimentTimerBiz experimentTimerBiz;
    private final ExperimentGroupService experimentGroupService;
    private final ExperimentPersonService experimentPersonService;
    private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
    private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
    private final RsUtilBiz rsUtilBiz;

    private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;

    private final OperateCostBiz operateCostBiz;
    private final OperateCostService operateCostService;

    private BigDecimal getWeightTotalScore(
            BigDecimal knowledgeWeight, BigDecimal knowledgeScore,
            BigDecimal healthIndexWeight, BigDecimal healthIndexScore,
            BigDecimal medicalRatioWeight, BigDecimal medicalRatioScore
    ) {
        BigDecimal finalKnowledgeScore = knowledgeScore.multiply(knowledgeWeight);
        BigDecimal finalHealthIndexScore = healthIndexScore.multiply(healthIndexWeight);
        BigDecimal finalMedicalRatioScoreScore = medicalRatioScore.multiply(medicalRatioWeight);
        return finalKnowledgeScore.add(finalHealthIndexScore).add(finalMedicalRatioScoreScore).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN);
    }

    private BigDecimal rsCalculateCompetitiveScore(BigDecimal currentHealthScore, BigDecimal defHealthScore, BigDecimal minHealthScore, BigDecimal maxHealthScore) {
        BigDecimal resultBigDecimal = null;
        if (currentHealthScore.compareTo(defHealthScore) >= 0) {
            if (maxHealthScore.compareTo(defHealthScore) == 0) {
                resultBigDecimal = currentHealthScore;
            } else {
                resultBigDecimal = (currentHealthScore.subtract(defHealthScore).divide(maxHealthScore.subtract(defHealthScore), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100 - 60))).add(BigDecimal.valueOf(60));
            }
        } else {
            if (minHealthScore.compareTo(defHealthScore) == 0) {
                resultBigDecimal = currentHealthScore;
            } else {
                resultBigDecimal = currentHealthScore.subtract(minHealthScore).divide(defHealthScore.subtract(minHealthScore), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(60));
            }
        }
        return resultBigDecimal;
    }

    public RsCalculateCompetitiveScoreRsResponse rsCalculateCompetitiveScore(RsCalculateCompetitiveScoreRequestRs rsCalculateCompetitiveScoreRequestRs) {
        List<GroupCompetitiveScoreRsResponse> groupCompetitiveScoreRsResponseList = new ArrayList<>();
        String experimentId = rsCalculateCompetitiveScoreRequestRs.getExperimentId();
        Integer periods = rsCalculateCompetitiveScoreRequestRs.getPeriods();
        Map<String, BigDecimal> kExperimentGroupIdVGroupCompetitiveScoreMap = new HashMap<>();

        Set<String> experimentPersonIdSet = new HashSet<>();
        Map<String, String> kExperimentPersonIdVCasePersonIdMap = new HashMap<>();
        Map<String, List<ExperimentPersonEntity>> kExperimentGroupIdVExperimentPersonEntityListMap = new HashMap<>();
        experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentInstanceId, experimentId)
                .list()
                .forEach(experimentPersonEntity -> {
                    String experimentPersonId = experimentPersonEntity.getExperimentPersonId();
                    experimentPersonIdSet.add(experimentPersonId);

                    String casePersonId = experimentPersonEntity.getCasePersonId();
                    kExperimentPersonIdVCasePersonIdMap.put(experimentPersonId, casePersonId);

                    String experimentGroupId = experimentPersonEntity.getExperimentGroupId();
                    List<ExperimentPersonEntity> experimentPersonEntityList = kExperimentGroupIdVExperimentPersonEntityListMap.get(experimentGroupId);
                    if (Objects.isNull(experimentPersonEntityList)) {
                        experimentPersonEntityList = new ArrayList<>();
                    }
                    experimentPersonEntityList.add(experimentPersonEntity);
                    kExperimentGroupIdVExperimentPersonEntityListMap.put(experimentGroupId, experimentPersonEntityList);
                });
        AtomicInteger groupExperimentPersonSizeAtomicInteger = new AtomicInteger(0);
        kExperimentGroupIdVExperimentPersonEntityListMap.forEach((ekExperimentGroupIdVExperimentPersonEntityListMap, experimentPersonEntityList) -> {
            if (Objects.nonNull(experimentPersonEntityList) && !experimentPersonEntityList.isEmpty()) {
                groupExperimentPersonSizeAtomicInteger.set(experimentPersonEntityList.size());
            }
        });

        Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
        Map<String, String> kExperimentIndicatorInstanceIdVExperimentPersonIdMap = new HashMap<>();
        Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentPersonIdVHealthExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
        if (!experimentPersonIdSet.isEmpty()) {
            experimentIndicatorInstanceRsService.lambdaQuery()
                    .eq(ExperimentIndicatorInstanceRsEntity::getExperimentId, experimentId)
                    .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.HEALTH_POINT.getType())
                    .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonIdSet)
                    .list()
                    .forEach(experimentIndicatorInstanceRsEntity -> {
                        String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
                        experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceId);

                        String experimentPersonId = experimentIndicatorInstanceRsEntity.getExperimentPersonId();
                        kExperimentIndicatorInstanceIdVExperimentPersonIdMap.put(experimentIndicatorInstanceId, experimentPersonId);

                        kExperimentPersonIdVHealthExperimentIndicatorInstanceRsEntityMap.put(experimentPersonId, experimentIndicatorInstanceRsEntity);
                    });
        }

        Map<String, ExperimentIndicatorValRsEntity> kHealthExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
        Map<String, BigDecimal> kCasePersonIdVMinHealthScoreMap = new HashMap<>();
        Map<String, BigDecimal> kCasePersonIdVMaxHealthScoreMap = new HashMap<>();
        if (!experimentIndicatorInstanceIdSet.isEmpty()) {
            experimentIndicatorValRsService.lambdaQuery()
                    .eq(ExperimentIndicatorValRsEntity::getExperimentId, experimentId)
                    .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
                    .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
                    .list()
                    .forEach(experimentIndicatorValRsEntity -> {
                        String indicatorInstanceId = experimentIndicatorValRsEntity.getIndicatorInstanceId();
                        kHealthExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(indicatorInstanceId, experimentIndicatorValRsEntity);

                        String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
                        BigDecimal currentBigDecimal = BigDecimal.valueOf(Double.parseDouble(currentVal));
                        String experimentPersonId = kExperimentIndicatorInstanceIdVExperimentPersonIdMap.get(indicatorInstanceId);
                        if (StringUtils.isNotBlank(experimentPersonId)) {
                            String casePersonId = kExperimentPersonIdVCasePersonIdMap.get(experimentPersonId);
                            if (StringUtils.isNotBlank(casePersonId)) {
                                BigDecimal minBigDecimal = kCasePersonIdVMinHealthScoreMap.get(casePersonId);
                                if (Objects.isNull(minBigDecimal)) {
                                    minBigDecimal = currentBigDecimal;
                                } else {
                                    if (currentBigDecimal.compareTo(minBigDecimal) < 0) {
                                        minBigDecimal = currentBigDecimal;
                                    }
                                }
                                kCasePersonIdVMinHealthScoreMap.put(casePersonId, minBigDecimal);

                                BigDecimal maxBigDecimal = kCasePersonIdVMaxHealthScoreMap.get(casePersonId);
                                if (Objects.isNull(maxBigDecimal)) {
                                    maxBigDecimal = currentBigDecimal;
                                } else {
                                    if (currentBigDecimal.compareTo(maxBigDecimal) > 0) {
                                        maxBigDecimal = currentBigDecimal;
                                    }
                                }
                                kCasePersonIdVMaxHealthScoreMap.put(casePersonId, maxBigDecimal);
                            }
                        }
                    });
        }

        kExperimentGroupIdVExperimentPersonEntityListMap.forEach((experimentGroupId, experimentPersonEntityList) -> {
            AtomicReference<BigDecimal> atomicReferenceTotalHealthScore = new AtomicReference<>(BigDecimal.ZERO);
            experimentPersonEntityList.forEach(experimentPersonEntity -> {
                String experimentPersonId = experimentPersonEntity.getExperimentPersonId();
                String casePersonId = experimentPersonEntity.getCasePersonId();
                BigDecimal minHealthScore = kCasePersonIdVMinHealthScoreMap.get(casePersonId);
                BigDecimal maxHealthScore = kCasePersonIdVMaxHealthScoreMap.get(casePersonId);
                ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kExperimentPersonIdVHealthExperimentIndicatorInstanceRsEntityMap.get(experimentPersonId);
                String defHealthScore = experimentIndicatorInstanceRsEntity.getDef();
                String healthExperimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
                ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kHealthExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(healthExperimentIndicatorInstanceId);
                String currentHealthScore = experimentIndicatorValRsEntity.getCurrentVal();
                BigDecimal resultHealthScore = rsCalculateCompetitiveScore(BigDecimal.valueOf(Double.parseDouble(currentHealthScore)), BigDecimal.valueOf(Double.parseDouble(defHealthScore)), minHealthScore, maxHealthScore);
                BigDecimal lastHealthScore = atomicReferenceTotalHealthScore.get();
                atomicReferenceTotalHealthScore.set(lastHealthScore.add(resultHealthScore));
            });
            kExperimentGroupIdVGroupCompetitiveScoreMap.put(experimentGroupId, atomicReferenceTotalHealthScore.get().divide(BigDecimal.valueOf(groupExperimentPersonSizeAtomicInteger.get()), 2, RoundingMode.DOWN));
        });
        kExperimentGroupIdVGroupCompetitiveScoreMap.forEach((experimentGroupId, groupCompetitiveScore) -> {
            groupCompetitiveScoreRsResponseList.add(GroupCompetitiveScoreRsResponse
                    .builder()
                    .experimentGroupId(experimentGroupId)
                    .groupCompetitiveScore(groupCompetitiveScore)
                    .build());
        });
        return RsCalculateCompetitiveScoreRsResponse
                .builder()
                .groupCompetitiveScoreRsResponseList(groupCompetitiveScoreRsResponseList)
                .build();
    }

    public RsCalculateMoneyScoreRsResponse rsCalculateMoneyScore(RsCalculateMoneyScoreRequestRs rsCalculateMoneyScoreRequestRs) {
        List<GroupMoneyScoreRsResponse> groupMoneyScoreRsResponseList = new ArrayList<>();
        Integer periods = rsCalculateMoneyScoreRequestRs.getPeriods();
        String experimentId = rsCalculateMoneyScoreRequestRs.getExperimentId();

        Map<String, String> kExperimentPersonIdVExperimentOrgGroupIdMap = new HashMap<>();
        Set<String> experimentPersonIdSet = new HashSet<>();
        experimentPersonService.lambdaQuery()
            .eq(ExperimentPersonEntity::getExperimentInstanceId, experimentId)
            .list()
            .forEach(experimentPersonEntity -> {
                String experimentPersonId = experimentPersonEntity.getExperimentPersonId();
                experimentPersonIdSet.add(experimentPersonId);

                kExperimentPersonIdVExperimentOrgGroupIdMap.put(experimentPersonId, experimentPersonEntity.getExperimentGroupId());
            });


        if (experimentPersonIdSet.isEmpty()) {return RsCalculateMoneyScoreRsResponse.builder().build();}

        Map<String, String> initMoneyByPeriods = experimentIndicatorInstanceRsBiz.getInitMoneyByPeriods(
            RsInitMoneyRequest
                .builder()
                .periods(periods)
                .experimentPersonIdSet(experimentPersonIdSet)
                .build()
        );
        if (initMoneyByPeriods.isEmpty()) {return RsCalculateMoneyScoreRsResponse.builder().build();}

        Map<String, BigDecimal> kExperimentOrgGroupIdVTotalMap = new HashMap<>();
        initMoneyByPeriods.forEach((experimentPersonId, money) -> {
            String experimentOrgGroupId = kExperimentPersonIdVExperimentOrgGroupIdMap.get(experimentPersonId);
            if (StringUtils.isBlank(experimentOrgGroupId)) {return;}
            BigDecimal bigDecimal = kExperimentOrgGroupIdVTotalMap.get(experimentOrgGroupId);
            if (Objects.isNull(bigDecimal)) {
                bigDecimal = BigDecimal.ZERO;
            }
            bigDecimal = bigDecimal.add(BigDecimal.valueOf(Double.parseDouble(money)));
            kExperimentOrgGroupIdVTotalMap.put(experimentOrgGroupId, bigDecimal);
        });

        Map<String, BigDecimal> kExperimentGroupIdVCostTotalMap = new HashMap<>();
        operateCostService.lambdaQuery()
            .eq(OperateCostEntity::getExperimentInstanceId, experimentId)
            .eq(OperateCostEntity::getPeriod, periods)
            .list()
            .forEach(operateCostEntity -> {
                String experimentGroupId = operateCostEntity.getExperimentGroupId();
                BigDecimal cost = operateCostEntity.getCost();
                BigDecimal bigDecimal = kExperimentGroupIdVCostTotalMap.get(experimentGroupId);
                if (Objects.isNull(bigDecimal)) {bigDecimal = BigDecimal.ZERO;}
                bigDecimal = bigDecimal.add(cost);
                kExperimentGroupIdVCostTotalMap.put(experimentGroupId, bigDecimal);
            });
        kExperimentOrgGroupIdVTotalMap.forEach((experimentOrgGroupId, initTotal) -> {
            BigDecimal costTotal = kExperimentGroupIdVCostTotalMap.get(experimentOrgGroupId);
            if (Objects.isNull(costTotal)) {costTotal = BigDecimal.ZERO;}
            BigDecimal groupMoneyScore = BigDecimal.valueOf(100).multiply(BigDecimal.ONE.subtract((costTotal.divide(initTotal, 2, RoundingMode.DOWN))));
            groupMoneyScoreRsResponseList.add(GroupMoneyScoreRsResponse
                .builder()
                    .experimentGroupId(experimentOrgGroupId)
                    .groupMoneyScore(groupMoneyScore)
                .build());
        });

        return RsCalculateMoneyScoreRsResponse
                .builder()
                .groupMoneyScoreRsResponseList(groupMoneyScoreRsResponseList)
                .build();
    }


    @Transactional(rollbackFor = Exception.class)
    @Trace(operationName = "存储期数翻转数据")
    @Tags({@Tag(key = "experimentId", value = "arg[0]"), @Tag(key = "periods", value = "arg[1]")})
    public void saveOrUpd(String experimentInstanceId, Integer periods) throws ExecutionException, InterruptedException {
        List<ExperimentGroupEntity> experimentGroupEntityList = new ArrayList<>();
        experimentGroupEntityList.addAll(experimentGroupService.lambdaQuery()
            .eq(ExperimentGroupEntity::getExperimentInstanceId, experimentInstanceId)
            .list());

        AtomicInteger scoringCountAtomicInteger = new AtomicInteger(1);
        experimentScoringService.lambdaQuery()
            .eq(ExperimentScoringEntity::getExperimentInstanceId, experimentInstanceId)
            .eq(ExperimentScoringEntity::getPeriods, periods)
            .list()
            .stream()
            .map(ExperimentScoringEntity::getScoringCount)
            .max(Integer::compareTo)
            .ifPresent(a -> scoringCountAtomicInteger.set(a + 1));



        AtomicReference<Float> knowledgeWeightAtomicReference = new AtomicReference<>(0F);
        AtomicReference<Float> healthIndexWeightAtomicReference = new AtomicReference<>(0F);
        AtomicReference<Float> medicalRatioWeightAtomicReference = new AtomicReference<>(0F);
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

        Map<String, BigDecimal> questionnaireScoreMap = new HashMap<>();
        questionnaireScoreMap.putAll(experimentQuestionnaireScoreBiz.listExptQuestionnaireScore(experimentInstanceId, periods));

        Map<String, BigDecimal> kExperimentGroupIdVGroupCompetitiveScoreMap = new HashMap<>();
        RsCalculateCompetitiveScoreRsResponse rsCalculateCompetitiveScoreRsResponse = this.rsCalculateCompetitiveScore(RsCalculateCompetitiveScoreRequestRs
            .builder()
            .experimentId(experimentInstanceId)
            .periods(periods)
            .build());
        List<GroupCompetitiveScoreRsResponse> groupCompetitiveScoreRsResponseList = rsCalculateCompetitiveScoreRsResponse.getGroupCompetitiveScoreRsResponseList();
        if (Objects.nonNull(groupCompetitiveScoreRsResponseList) && !groupCompetitiveScoreRsResponseList.isEmpty()) {
            groupCompetitiveScoreRsResponseList.forEach(groupCompetitiveScoreRsResponse -> {
                String experimentGroupId = groupCompetitiveScoreRsResponse.getExperimentGroupId();
                BigDecimal groupCompetitiveScore = groupCompetitiveScoreRsResponse.getGroupCompetitiveScore();
                kExperimentGroupIdVGroupCompetitiveScoreMap.put(experimentGroupId, groupCompetitiveScore);
            });
        }

        Map<String, BigDecimal> kExperimentGroupIdVGroupMoneyScoreMap = new HashMap<>();
        RsCalculateMoneyScoreRsResponse rsCalculateMoneyScoreRsResponse = this.rsCalculateMoneyScore(RsCalculateMoneyScoreRequestRs
            .builder()
            .experimentId(experimentInstanceId)
            .periods(periods)
            .build());
        List<GroupMoneyScoreRsResponse> groupMoneyScoreRsResponseList = rsCalculateMoneyScoreRsResponse.getGroupMoneyScoreRsResponseList();
        if (Objects.nonNull(groupMoneyScoreRsResponseList) && !groupMoneyScoreRsResponseList.isEmpty()) {
            groupMoneyScoreRsResponseList.forEach(groupMoneyScoreRsResponse -> {
                String experimentGroupId = groupMoneyScoreRsResponse.getExperimentGroupId();
                BigDecimal groupMoneyScore = groupMoneyScoreRsResponse.getGroupMoneyScore();
                kExperimentGroupIdVGroupMoneyScoreMap.put(experimentGroupId, groupMoneyScore);
            });
        }

        List<ExperimentScoringEntity> experimentScoringEntityList = new ArrayList<>();
        experimentGroupEntityList.forEach(experimentGroupEntity -> {
            String experimentGroupId = experimentGroupEntity.getExperimentGroupId();
            String groupName = experimentGroupEntity.getGroupName();
            String groupNo = experimentGroupEntity.getGroupNo();
            String groupAlias = experimentGroupEntity.getGroupAlias();
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
            BigDecimal totalScoreBigDecimal = getWeightTotalScore(
                BigDecimal.valueOf(knowledgeWeightAtomicReference.get()), questionnaireScoreBigDecimal,
                BigDecimal.valueOf(healthIndexWeightAtomicReference.get()), groupCompetitiveScoreBigDecimal,
                BigDecimal.valueOf(medicalRatioWeightAtomicReference.get()), groupIdVGroupMoneyScoreBigDecimal
            );
            experimentScoringEntityList.add(ExperimentScoringEntity
                .builder()
                .experimentScoringId(idGenerator.nextIdStr())
                .experimentInstanceId(experimentInstanceId)
                .experimentGroupId(experimentGroupId)
                .experimentGroupNo(groupNo)
                .experimentGroupName(groupName)
                .experimentGroupAlias(groupAlias)
                .knowledgeScore(questionnaireScoreBigDecimal.setScale(2, RoundingMode.DOWN).toString())
                .healthIndexScore(groupCompetitiveScoreBigDecimal.setScale(2, RoundingMode.DOWN).toString())
                .treatmentPercentScore(groupIdVGroupMoneyScoreBigDecimal.setScale(2, RoundingMode.DOWN).toString())
                .totalScore(totalScoreBigDecimal.setScale(2, RoundingMode.DOWN).toString())
                .scoringCount(scoringCountAtomicInteger.get())
                .periods(periods)
                .build());
        });

        experimentScoringService.saveOrUpdateBatch(experimentScoringEntityList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Trace(operationName = "存储期数翻转数据")
    @Tags({@Tag(key = "experimentId", value = "arg[0]"), @Tag(key = "periods", value = "arg[1]")})
    public void cfSaveOrUpd(String experimentInstanceId, Integer periods) throws ExecutionException, InterruptedException {
        List<ExperimentGroupEntity> experimentGroupEntityList = new ArrayList<>();
        CompletableFuture<Void> populateExperimentGroupEntityListCF = getPopulateExperimentGroupEntityListCF(experimentInstanceId, experimentGroupEntityList);

        AtomicInteger scoringCountAtomicInteger = new AtomicInteger(1);
        CompletableFuture<Void> populateScoringCountCF = getPopulateScoringCountCF(scoringCountAtomicInteger, experimentInstanceId, periods);

        AtomicReference<Float> knowledgeWeightAtomicReference = new AtomicReference<>(0F);
        AtomicReference<Float> healthIndexWeightAtomicReference = new AtomicReference<>(0F);
        AtomicReference<Float> medicalRatioWeightAtomicReference = new AtomicReference<>(0F);
        CompletableFuture<Void> populateWeightCF = getPopulateWeightCF(knowledgeWeightAtomicReference, healthIndexWeightAtomicReference, medicalRatioWeightAtomicReference, experimentInstanceId);

        Map<String, BigDecimal> questionnaireScoreMap = new HashMap<>();
        CompletableFuture<Void> populateQuestionnaireScoreMapCF = getPopulateQuestionnaireScoreMapCF(questionnaireScoreMap, experimentInstanceId, periods);

        Map<String, BigDecimal> kExperimentGroupIdVGroupCompetitiveScoreMap = new HashMap<>();
        CompletableFuture<Void> populateKExperimentGroupIdVGroupCompetitiveScoreMapCF = getPopulateKExperimentGroupIdVGroupCompetitiveScoreMapCF(kExperimentGroupIdVGroupCompetitiveScoreMap, experimentInstanceId, periods);

        Map<String, BigDecimal> kExperimentGroupIdVGroupMoneyScoreMap = new HashMap<>();
        CompletableFuture<Void> populateKExperimentGroupIdVGroupMoneyScoreMapCF = getPopulateKExperimentGroupIdVGroupMoneyScoreMapCF(kExperimentGroupIdVGroupMoneyScoreMap, experimentInstanceId, periods);

        CompletableFuture.allOf(populateExperimentGroupEntityListCF, populateScoringCountCF, populateWeightCF,
            populateQuestionnaireScoreMapCF, populateKExperimentGroupIdVGroupCompetitiveScoreMapCF, populateKExperimentGroupIdVGroupMoneyScoreMapCF).get();

        List<ExperimentScoringEntity> experimentScoringEntityList = new ArrayList<>();
        CompletableFuture<Void> populateExperimentScoringEntityListCF = getPopulateExperimentScoringEntityListCF(
            experimentScoringEntityList, experimentGroupEntityList, scoringCountAtomicInteger,
            knowledgeWeightAtomicReference, healthIndexWeightAtomicReference, medicalRatioWeightAtomicReference,
            questionnaireScoreMap, kExperimentGroupIdVGroupCompetitiveScoreMap, kExperimentGroupIdVGroupMoneyScoreMap,
            experimentInstanceId, periods
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
            Map<String, BigDecimal> kExperimentGroupIdVGroupCompetitiveScoreMap, String experimentInstanceId, Integer periods) {
        return CompletableFuture.runAsync(() -> {
            RsCalculateCompetitiveScoreRsResponse rsCalculateCompetitiveScoreRsResponse = this.rsCalculateCompetitiveScore(RsCalculateCompetitiveScoreRequestRs
                    .builder()
                    .experimentId(experimentInstanceId)
                    .periods(periods)
                    .build());
            List<GroupCompetitiveScoreRsResponse> groupCompetitiveScoreRsResponseList = rsCalculateCompetitiveScoreRsResponse.getGroupCompetitiveScoreRsResponseList();
            if (Objects.nonNull(groupCompetitiveScoreRsResponseList) && !groupCompetitiveScoreRsResponseList.isEmpty()) {
                groupCompetitiveScoreRsResponseList.forEach(groupCompetitiveScoreRsResponse -> {
                    String experimentGroupId = groupCompetitiveScoreRsResponse.getExperimentGroupId();
                    BigDecimal groupCompetitiveScore = groupCompetitiveScoreRsResponse.getGroupCompetitiveScore();
                    kExperimentGroupIdVGroupCompetitiveScoreMap.put(experimentGroupId, groupCompetitiveScore);
                });
            }
        });
    }

    private CompletableFuture<Void> getPopulateKExperimentGroupIdVGroupMoneyScoreMapCF(
            Map<String, BigDecimal> kExperimentGroupIdVGroupMoneyScoreMap, String experimentInstanceId, Integer periods
    ) {
        return CompletableFuture.runAsync(() -> {
            RsCalculateMoneyScoreRsResponse rsCalculateMoneyScoreRsResponse = this.rsCalculateMoneyScore(RsCalculateMoneyScoreRequestRs
                    .builder()
                    .experimentId(experimentInstanceId)
                    .periods(periods)
                    .build());
            List<GroupMoneyScoreRsResponse> groupMoneyScoreRsResponseList = rsCalculateMoneyScoreRsResponse.getGroupMoneyScoreRsResponseList();
            if (Objects.nonNull(groupMoneyScoreRsResponseList) && !groupMoneyScoreRsResponseList.isEmpty()) {
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
            Integer periods
    ) {
        return CompletableFuture.runAsync(() -> {
            experimentGroupEntityList.forEach(experimentGroupEntity -> {
                String experimentGroupId = experimentGroupEntity.getExperimentGroupId();
                String groupName = experimentGroupEntity.getGroupName();
                String groupNo = experimentGroupEntity.getGroupNo();
                String groupAlias = experimentGroupEntity.getGroupAlias();
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
                BigDecimal totalScoreBigDecimal = getWeightTotalScore(
                        BigDecimal.valueOf(knowledgeWeightAtomicReference.get()), questionnaireScoreBigDecimal,
                        BigDecimal.valueOf(healthIndexWeightAtomicReference.get()), groupCompetitiveScoreBigDecimal,
                        BigDecimal.valueOf(medicalRatioWeightAtomicReference.get()), groupIdVGroupMoneyScoreBigDecimal
                );
                experimentScoringEntityList.add(ExperimentScoringEntity
                        .builder()
                        .experimentScoringId(idGenerator.nextIdStr())
                        .experimentInstanceId(experimentInstanceId)
                        .experimentGroupId(experimentGroupId)
                        .experimentGroupNo(groupNo)
                        .experimentGroupName(groupName)
                        .experimentGroupAlias(groupAlias)
                        .knowledgeScore(questionnaireScoreBigDecimal.setScale(2, RoundingMode.DOWN).toString())
                        .healthIndexScore(groupCompetitiveScoreBigDecimal.setScale(2, RoundingMode.DOWN).toString())
                        .treatmentPercentScore(groupIdVGroupMoneyScoreBigDecimal.setScale(2, RoundingMode.DOWN).toString())
                        .totalScore(totalScoreBigDecimal.setScale(2, RoundingMode.DOWN).toString())
                        .scoringCount(scoringCountAtomicInteger.get())
                        .periods(periods)
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


//    public

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
        List<ExperimentScoringEntity> list = experimentScoringService.lambdaQuery()
                .eq(ExperimentScoringEntity::getExperimentInstanceId, experimentId)
                .list();
        list.forEach(experimentScoringEntity -> {
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

        /* runsix:算每期列表 */
        kPeriodVKExperimentGroupIdVExperimentScoringEntityMap.forEach((period, kExperimentGroupIdVExperimentScoringEntityMap) -> {
            /* runsix:12345期数据 */
            ExperimentRankItemResponse experimentRankItemResponse = kPeriodVExperimentRankItemResponseMap.get(period);
            List<ExperimentRankGroupItemResponse> experimentRankGroupItemResponseList = experimentRankItemResponse.getExperimentRankGroupItemResponseList();
            kExperimentGroupIdVExperimentScoringEntityMap.forEach((experimentGroupId, experimentScoringEntity) -> {
                experimentRankGroupItemResponseList.add(ExperimentRankGroupItemResponse
                        .builder()
                        .experimentGroupId(experimentScoringEntity.getExperimentGroupId())
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

        /**
         * 如果期数未满，那么不执行计算总分，直接返回对应的期数分值
         */
        int size = experimentGroupService.lambdaQuery()
            .eq(ExperimentGroupEntity::getExperimentInstanceId, experimentId)
            .list()
            .size();

        if (list.size() != totalPeriods*size) {
            return ExperimentRankResponse
                    .builder()
                    .totalPeriod(totalPeriods)
                    .experimentRankItemResponseList(experimentRankItemResponseList)
                    .experimentTotalRankItemResponseList(experimentTotalRankItemResponseList)
                    .build();
        }

        /**
         * todo ExperimentScoringBiz.getRank(String experimentId,String period,boolean recalc)该方法增加一个参数，可以用于判断是否要从新计算总分
         * if(recalc){
         *     todo
         * }
         * 先查数据库
         */
        List<ExperimentRankingEntity> list1 = experimentRankingService.lambdaQuery()
                .eq(ExperimentRankingEntity::getExperimentInstanceId, experimentId)
                .list();
        if (CollectionUtil.isNotEmpty(list1)) {
            for (ExperimentRankingEntity experimentRankingEntity : list1) {
                ExperimentTotalRankItemResponse experimentTotalRankItemResponse = ExperimentTotalRankItemResponse.builder()
                        .experimentGroupNo(experimentRankingEntity.getGroupAlias())
                        .experimentGroupId(experimentRankingEntity.getExperimentGroupId())
                        .experimentGroupName(experimentRankingEntity.getGroupName())
                        .allPeriodsTotalScore(experimentRankingEntity.getTotalScore())
                        .experimentTotalRankGroupItemResponseList(JSONUtil.toList(experimentRankingEntity.getPeriodScoreJson()
                                , ExperimentTotalRankGroupItemResponse.class))
                        .build();
                experimentTotalRankItemResponseList.add(experimentTotalRankItemResponse);
            }
            ExperimentRankResponse build = ExperimentRankResponse.builder()
                    .totalPeriod(totalPeriods)
                    .experimentRankItemResponseList(experimentRankItemResponseList)
                    .experimentTotalRankItemResponseList(experimentTotalRankItemResponseList)
                    .build();
            return build;
        }

        /* runsix:算所有期数得分 */
        kExperimentGroupIdVKPeriodVExperimentScoringEntityMap.forEach((experimentGroupId, kPeriodVExperimentScoringEntityMap) -> {
            List<ExperimentTotalRankGroupItemResponse> experimentTotalRankGroupItemResponseList = new ArrayList<>();
            AtomicReference<String> atomicReferenceExperimentGroupId = new AtomicReference<>();
            AtomicReference<String> atomicReferenceExperimentGroupNo = new AtomicReference<>();
            AtomicReference<String> atomicReferenceExperimentGroupName = new AtomicReference<>();
            AtomicReference<Double> atomicReferenceAllPeriodsTotalScore = new AtomicReference<>(0D);

            kPeriodVExperimentScoringEntityMap.forEach((period, experimentScoringEntity) -> {
                if (StringUtils.isBlank(atomicReferenceExperimentGroupId.get())) {
                    atomicReferenceExperimentGroupId.set(experimentScoringEntity.getExperimentGroupId());
                }
                if (StringUtils.isBlank(atomicReferenceExperimentGroupNo.get())) {
                    atomicReferenceExperimentGroupNo.set(experimentScoringEntity.getExperimentGroupNo());
                }
                if (StringUtils.isBlank(atomicReferenceExperimentGroupName.get())) {
                    atomicReferenceExperimentGroupName.set(experimentScoringEntity.getExperimentGroupName());
                }
                Double currentTotalScore = atomicReferenceAllPeriodsTotalScore.get();
                String totalScore = experimentScoringEntity.getTotalScore();
                Float weight = kPeriodVWeightMap.get(period.toString());
                currentTotalScore += Double.parseDouble(totalScore) * weight / 100;


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
                    .experimentGroupId(atomicReferenceExperimentGroupId.get())
                    .experimentGroupNo(atomicReferenceExperimentGroupNo.get())
                    .experimentGroupName(atomicReferenceExperimentGroupName.get())
                    .allPeriodsTotalScore(atomicReferenceAllPeriodsTotalScore.get().toString())
                    .experimentTotalRankGroupItemResponseList(experimentTotalRankGroupItemResponseList)
                    .build());
        });
        /* runsix:sort */
        experimentTotalRankItemResponseList.sort(Comparator.comparing(a -> Double.parseDouble(a.getAllPeriodsTotalScore())));
        Collections.reverse(experimentTotalRankItemResponseList);
        List<ExperimentRankingEntity> experimentRankingEntities = new ArrayList<>();
        for (int i = 0; i < experimentTotalRankItemResponseList.size(); i++) {
            ExperimentTotalRankItemResponse experimentTotalRankItemResponse = experimentTotalRankItemResponseList.get(i);
            experimentTotalRankItemResponse.getExperimentTotalRankGroupItemResponseList()
                    .sort(Comparator.comparingInt(ExperimentTotalRankGroupItemResponse::getPeriods));
            // 小组排行
            ExperimentRankingEntity experimentRankingEntity = ExperimentRankingEntity.builder()
                    .experimentRankingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentId)
                    .experimentGroupId(experimentTotalRankItemResponse.getExperimentGroupId())
                    .rankingIndex(i+1)// 排名
                    .totalScore(experimentTotalRankItemResponse.getAllPeriodsTotalScore())
                    .periodScoreJson(JSONUtil.toJsonStr(experimentTotalRankItemResponse.getExperimentTotalRankGroupItemResponseList()))
                    .groupName(experimentTotalRankItemResponse.getExperimentGroupName())
                    .groupAlias(experimentTotalRankItemResponse.getExperimentGroupNo())
                    .build();
            experimentRankingEntities.add(experimentRankingEntity);
        }
        // 保存计算后的结果
        experimentRankingService.saveBatch(experimentRankingEntities);

        return ExperimentRankResponse
                .builder()
                .totalPeriod(totalPeriods)
                .experimentRankItemResponseList(experimentRankItemResponseList)
                .experimentTotalRankItemResponseList(experimentTotalRankItemResponseList)
                .build();
    }

    public ExperimentGraphRankResponse getGraphRank(String appId, String experimentId, Integer period) throws ExecutionException, InterruptedException {
        if (Objects.isNull(period)) {
            ExperimentPeriodsResonse experimentPeriods = experimentTimerBiz.getExperimentCurrentPeriods(appId, experimentId);
            if (Objects.nonNull(experimentPeriods) && Objects.nonNull(experimentPeriods.getCurrentPeriod())) {
                period = experimentPeriods.getCurrentPeriod();
            } else {
                period = 1;
            }
        }
        Map<String, ExperimentGroupEntity> kExperimentGroupIdVExperimentGroupEntityMap = experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, experimentId)
                .list()
                .stream()
                .collect(Collectors.toMap(ExperimentGroupEntity::getExperimentGroupId, a -> a));

        AtomicReference<Float> knowledgeWeightAtomicReference = new AtomicReference<>(0F);
        AtomicReference<Float> healthIndexWeightAtomicReference = new AtomicReference<>(0F);
        AtomicReference<Float> medicalRatioWeightAtomicReference = new AtomicReference<>(0F);
        CompletableFuture<Void> populateWeightCF = getPopulateWeightCF(knowledgeWeightAtomicReference, healthIndexWeightAtomicReference, medicalRatioWeightAtomicReference, experimentId);
        populateWeightCF.get();

        Map<String, ExperimentScoringEntity> kExperimentGroupIdVExperimentScoringEntityMap = new HashMap<>();
        experimentScoringService.lambdaQuery()
                .eq(ExperimentScoringEntity::getExperimentInstanceId, experimentId)
                .eq(ExperimentScoringEntity::getPeriods, period)
                .list()
                .forEach(experimentScoringEntity -> {
                    String experimentGroupId = experimentScoringEntity.getExperimentGroupId();
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
                });

        Map<String, ExperimentGraphRankGroupResponse> kExperimentGroupIdVExperimentGraphRankGroupResponseMap = new HashMap<>();
        kExperimentGroupIdVExperimentGroupEntityMap.forEach((experimentGroupId, experimentGroupEntity) -> {
            String knowledgeScore = "0";
            String percentKnowledgeScore = "0";
            String healthIndexScore = "0";
            String percentHealthIndexScore = "0";
            String treatmentPercentScore = "0";
            String percentTreatmentPercentScore = "0";
            String totalScore = "0";
            ExperimentScoringEntity experimentScoringEntity = kExperimentGroupIdVExperimentScoringEntityMap.get(experimentGroupId);
            if (Objects.nonNull(experimentScoringEntity)) {
                totalScore = experimentScoringEntity.getTotalScore();
                knowledgeScore = experimentScoringEntity.getKnowledgeScore();
                percentKnowledgeScore = BigDecimal.valueOf(Double.parseDouble(knowledgeScore)).multiply(BigDecimal.valueOf(knowledgeWeightAtomicReference.get())).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN).divide(BigDecimal.valueOf(Double.parseDouble(totalScore)), 2, RoundingMode.DOWN).toString();
                healthIndexScore = experimentScoringEntity.getHealthIndexScore();
                percentHealthIndexScore = BigDecimal.valueOf(Double.parseDouble(healthIndexScore)).multiply(BigDecimal.valueOf(healthIndexWeightAtomicReference.get())).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN).divide(BigDecimal.valueOf(Double.parseDouble(totalScore)), 2, RoundingMode.DOWN).toString();
                treatmentPercentScore = experimentScoringEntity.getTreatmentPercentScore();
                percentTreatmentPercentScore = BigDecimal.valueOf(Double.parseDouble(treatmentPercentScore)).multiply(BigDecimal.valueOf(medicalRatioWeightAtomicReference.get())).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN).divide(BigDecimal.valueOf(Double.parseDouble(totalScore)), 2, RoundingMode.DOWN).toString();
            }
            ExperimentGraphRankGroupResponse experimentGraphRankGroupResponse = ExperimentGraphRankGroupResponse
                    .builder()
                    .experimentGroupId(experimentGroupEntity.getExperimentGroupId())
                    .experimentGroupNo(experimentGroupEntity.getGroupNo())
                    .experimentGroupAlias(experimentGroupEntity.getGroupAlias())
                    .experimentGroupName(experimentGroupEntity.getGroupName())
                    .percentKnowledgeScore(percentKnowledgeScore)
                    .percentHealthIndexScore(percentHealthIndexScore)
                    .percentTreatmentPercentScore(percentTreatmentPercentScore)
                    .totalScore(totalScore)
                    .build();
            kExperimentGroupIdVExperimentGraphRankGroupResponseMap.put(experimentGroupId, experimentGraphRankGroupResponse);
        });
        List<ExperimentGraphRankGroupResponse> experimentGraphRankGroupResponseList = new ArrayList<>();
        kExperimentGroupIdVExperimentGraphRankGroupResponseMap.forEach((experimentGroupId, experimentGraphRankGroupResponse) -> {
            experimentGraphRankGroupResponseList.add(experimentGraphRankGroupResponse);
        });
        experimentGraphRankGroupResponseList.sort(Comparator.comparing(ExperimentGraphRankGroupResponse::getTotalScore));
        Collections.reverse(experimentGraphRankGroupResponseList);
        return ExperimentGraphRankResponse
                .builder()
                .periods(period)
                .experimentGraphRankGroupResponseList(experimentGraphRankGroupResponseList)
                .build();
    }
}
