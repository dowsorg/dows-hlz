package org.dows.hep.biz.eval;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.RsCalculateCompetitiveScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsCalculateMoneyScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsInitMoneyRequest;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.user.experiment.ExperimentQuestionnaireScoreBiz;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.biz.util.*;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author : wuzl
 * @date : 2023/9/13 19:20
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EvalScoreRankBiz {

    private final ExperimentQuestionnaireScoreBiz experimentQuestionnaireScoreBiz;
    private final IdGenerator idGenerator;
    private final ExperimentSettingService experimentSettingService;

    private final ExperimentRankingService experimentRankingService;

    private final ExperimentScoringService experimentScoringService;
    private final ExperimentTimerBiz experimentTimerBiz;
    private final ExperimentGroupService experimentGroupService;

    private final EvalCompetitiveScoreBiz evalCompetitiveScoreBiz;

    private final ExperimentPersonCache experimentPersonCache;

    private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;

    private final OperateCostService operateCostService;

    private final EvalJudgeScoreBiz evalJudgeScoreBiz;

    private final EvalPersonMoneyBiz evalPersonMoneyBiz;

    private final int SCALEScore=2;


    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpd(String experimentInstanceId, Integer periods) {
        Collection<ExperimentGroupEntity> experimentGroupEntityList = experimentPersonCache.getGroups(experimentInstanceId);

        AtomicInteger scoringCountAtomicInteger = new AtomicInteger(1);
        experimentScoringService.lambdaQuery()
                .eq(ExperimentScoringEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentScoringEntity::getPeriods, periods)
                .orderByDesc(ExperimentScoringEntity::getScoringCount)
                .select(ExperimentScoringEntity::getScoringCount)
                .last("limit 1")
                .oneOpt()
                .ifPresent(i -> scoringCountAtomicInteger.set(i.getScoringCount() + 1));

        AtomicReference<BigDecimal> knowledgeWeightAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> healthIndexWeightAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> medicalRatioWeightAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> operateRightAtomicWeightReference = new AtomicReference<>(BigDecimal.ZERO);
        ExperimentSettingCollection exptColl = ExperimentSettingCache.Instance().getSet(ExperimentCacheKey.create("3", experimentInstanceId), false);
        Optional.ofNullable(exptColl).ifPresent(i -> {
            knowledgeWeightAtomicReference.set(i.getKnowledgeWeight());
            healthIndexWeightAtomicReference.set(i.getHealthIndexWeight());
            medicalRatioWeightAtomicReference.set(i.getMedicalRatioWeight());
            operateRightAtomicWeightReference.set(i.getOperateRightWeight());
        });

        //A、知识答题得分
        Map<String, BigDecimal> questionnaireScoreMap = new HashMap<>();
        questionnaireScoreMap.putAll(experimentQuestionnaireScoreBiz.listExptQuestionnaireScore(experimentInstanceId, periods));

        //B、健康指数得分
        RsCalculateCompetitiveScoreRsResponse rsCalculateCompetitiveScoreRsResponse = evalCompetitiveScoreBiz.evalCompetitiveScore(RsCalculateCompetitiveScoreRequestRs
                .builder()
                .experimentId(experimentInstanceId)
                .periods(periods)
                .build());
        Map<String, GroupCompetitiveScoreRsResponse> mapGroupScores = rsCalculateCompetitiveScoreRsResponse.getMapGroupScores();


        //C、医疗占比得分
        Map<String, BigDecimalOptional> mapMoneyScore =evalPersonMoneyBiz.evalMoneyScore4Period(experimentInstanceId,periods);

        //D.操作准确度得分
        Map<String, BigDecimalOptional> mapJudgeSocre = evalJudgeScoreBiz.evalJudgeScore4Period(experimentInstanceId, periods);

        List<ExperimentScoringEntity> experimentScoringEntityList = new ArrayList<>();
        experimentGroupEntityList.forEach(experimentGroupEntity -> {
            String experimentGroupId = experimentGroupEntity.getExperimentGroupId();
            String groupName = experimentGroupEntity.getGroupName();
            String groupNo = experimentGroupEntity.getGroupNo();
            String groupAlias = experimentGroupEntity.getGroupAlias();
            //知识答题分
            BigDecimal questionnaireScoreBigDecimal = BigDecimalOptional.valueOf(questionnaireScoreMap.get(experimentGroupId))
                    .min(BigDecimal.ZERO)
                    .getValue(SCALEScore);

            //健康竞赛分
            GroupCompetitiveScoreRsResponse groupScore = mapGroupScores.get(experimentGroupId);
            BigDecimal groupCompetitiveScoreBigDecimal = Optional.ofNullable(groupScore)
                    .map(GroupCompetitiveScoreRsResponse::getGroupCompetitiveScore)
                    .map(BigDecimalOptional::valueOf)
                    .orElse(BigDecimalOptional.zero())
                    .min(BigDecimal.ZERO)
                    .getValue(SCALEScore);

            //医疗得分
            BigDecimal groupIdVGroupMoneyScoreBigDecimal =  mapMoneyScore.getOrDefault(experimentGroupId,BigDecimalOptional.zero())
                    .min(BigDecimal.ZERO)
                    .getValue(SCALEScore);

            //操作准确度得分
            BigDecimal groupOperateRightScore = mapJudgeSocre.getOrDefault(experimentGroupId, BigDecimalOptional.zero())
                    .min(BigDecimal.ZERO)
                    .getValue(SCALEScore);

            //权重
            BigDecimal knowledgeWeight = knowledgeWeightAtomicReference.get();
            BigDecimal healthIndexWeight = healthIndexWeightAtomicReference.get();
            BigDecimal medicalRatioWeight = medicalRatioWeightAtomicReference.get();
            BigDecimal operateRightWeight = operateRightAtomicWeightReference.get();
            //总分
            BigDecimal totalScoreBigDecimal = getWeightTotalScore(
                    knowledgeWeight, questionnaireScoreBigDecimal,
                    healthIndexWeight, groupCompetitiveScoreBigDecimal,
                    medicalRatioWeight, groupIdVGroupMoneyScoreBigDecimal,
                    operateRightWeight, groupOperateRightScore
            );

            //权重后得分
            BigDecimal finalKnowledgeScore = questionnaireScoreBigDecimal.multiply(knowledgeWeight);
            BigDecimal finalHealthIndexScore = groupCompetitiveScoreBigDecimal.multiply(healthIndexWeight);
            BigDecimal finalMedicalRatioScoreScore = groupIdVGroupMoneyScoreBigDecimal.multiply(medicalRatioWeight);
            BigDecimal finalOperateRightScore = groupOperateRightScore.multiply(operateRightWeight);

            String hpScoreJson = JacksonUtil.toJsonSilence(Optional.ofNullable(groupScore)
                    .map(GroupCompetitiveScoreRsResponse::getPersonScores)
                    .orElse(null), true);
            experimentScoringEntityList.add(ExperimentScoringEntity
                    .builder()
                    .experimentScoringId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstanceId)
                    .experimentGroupId(experimentGroupId)
                    .experimentGroupNo(groupNo)
                    .experimentGroupName(groupName)
                    .experimentGroupAlias(groupAlias)
                    .knowledgeScore(questionnaireScoreBigDecimal.setScale(2, RoundingMode.HALF_UP).toString())
                    .healthIndexScore(groupCompetitiveScoreBigDecimal.setScale(2, RoundingMode.HALF_UP).toString())
                    .treatmentPercentScore(groupIdVGroupMoneyScoreBigDecimal.setScale(2, RoundingMode.HALF_UP).toString())
                    .operateRightScore(groupOperateRightScore.setScale(2, RoundingMode.HALF_UP).toString())
                    .totalScore(totalScoreBigDecimal.setScale(2, RoundingMode.HALF_UP).toString())
                    .percentKnowledgeScore(finalKnowledgeScore.divide(totalScoreBigDecimal, 2, RoundingMode.HALF_UP).toString())
                    .percentHealthIndexScore(finalHealthIndexScore.divide(totalScoreBigDecimal, 2, RoundingMode.HALF_UP).toString())
                    .percentTreatmentPercentScore(finalMedicalRatioScoreScore.divide(totalScoreBigDecimal, 2, RoundingMode.HALF_UP).toString())
                    .percentOperateRightScore(finalOperateRightScore.divide(totalScoreBigDecimal, 2, RoundingMode.HALF_UP).toString())
                    .scoringCount(scoringCountAtomicInteger.get())
                    .periods(periods)
                    .hpScoreJson(hpScoreJson)
                    .build());
        });
        //计算排名
        experimentScoringEntityList.sort(Comparator.comparing(ExperimentScoringEntity::getTotalScore)
                .thenComparing(ExperimentScoringEntity::getHealthIndexScore).reversed());
        AtomicInteger curRank = new AtomicInteger(1);
        experimentScoringEntityList.forEach(experimentScoringEntity -> experimentScoringEntity.setRankNo(curRank.getAndIncrement()));
        experimentScoringService.saveOrUpdateBatch(experimentScoringEntityList);
    }

    public ExperimentGraphRankResponse getGraphRank(String appId, String experimentId, Integer period) {
        if (Objects.isNull(period)) {
            ExperimentPeriodsResonse experimentPeriods = experimentTimerBiz.getExperimentCurrentPeriods(appId, experimentId);
            if (Objects.nonNull(experimentPeriods) && Objects.nonNull(experimentPeriods.getCurrentPeriod())) {
                period = experimentPeriods.getCurrentPeriod();
            } else {
                period = 1;
            }
        }
        Map<String, ExperimentGroupEntity> kExperimentGroupIdVExperimentGroupEntityMap = experimentPersonCache.getMapGroups(experimentId);


        //分组得分
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

            ExperimentScoringEntity rowScore = Optional.ofNullable(kExperimentGroupIdVExperimentScoringEntityMap.get(experimentGroupId))
                    .orElse(new ExperimentScoringEntity()
                            .setRankNo(1)
                            .setHealthIndexScore("0")
                            .setKnowledgeScore("0")
                            .setTreatmentPercentScore("0")
                            .setOperateRightScore("0")
                            .setPercentHealthIndexScore("0")
                            .setPercentKnowledgeScore("0")
                            .setPercentTreatmentPercentScore("0")
                            .setPercentOperateRightScore("0")
                            .setTotalScore("0")
                    );

            ExperimentGraphRankGroupResponse experimentGraphRankGroupResponse = ExperimentGraphRankGroupResponse
                    .builder()
                    .experimentGroupId(experimentGroupEntity.getExperimentGroupId())
                    .experimentGroupNo(experimentGroupEntity.getGroupNo())
                    .experimentGroupAlias(experimentGroupEntity.getGroupAlias())
                    .experimentGroupName(experimentGroupEntity.getGroupName())
                    .rankNo(String.valueOf(rowScore.getRankNo()))
                    .totalScore(rowScore.getTotalScore())
                    .knowledgeScore(rowScore.getKnowledgeScore())
                    .healthIndexScore(rowScore.getHealthIndexScore())
                    .treatmentPercentScore(rowScore.getTreatmentPercentScore())
                    .operateRightScore(rowScore.getOperateRightScore())
                    .percentKnowledgeScore(rowScore.getPercentKnowledgeScore())
                    .percentHealthIndexScore(rowScore.getPercentHealthIndexScore())
                    .percentTreatmentPercentScore(rowScore.getPercentTreatmentPercentScore())
                    .percentOperateRightScore(rowScore.getPercentOperateRightScore())
                    .build();

            kExperimentGroupIdVExperimentGraphRankGroupResponseMap.put(experimentGroupId, experimentGraphRankGroupResponse);
        });
        List<ExperimentGraphRankGroupResponse> experimentGraphRankGroupResponseList = new ArrayList<>();
        kExperimentGroupIdVExperimentGraphRankGroupResponseMap.forEach((experimentGroupId, experimentGraphRankGroupResponse) -> {
            experimentGraphRankGroupResponseList.add(experimentGraphRankGroupResponse);
        });

        experimentGraphRankGroupResponseList.sort(Comparator.comparing(ExperimentGraphRankGroupResponse::getRankNo)
                .thenComparing(ExperimentGraphRankGroupResponse::getTotalScore).reversed());

        return ExperimentGraphRankResponse
                .builder()
                .periods(period)
                .experimentGraphRankGroupResponseList(experimentGraphRankGroupResponseList)
                .build();
    }

    public ExperimentRankResponse getRank(String experimentId) {

        List<ExperimentTotalRankItemResponse> experimentTotalRankItemResponseList = new ArrayList<>();
        Map<Integer, ExperimentRankItemResponse> kPeriodVExperimentRankItemResponseMap = new HashMap<>();

        ExperimentSettingCollection exptColl = ExperimentSettingCache.Instance().getSet(ExperimentCacheKey.create("3", experimentId), false);
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(exptColl))
                .throwMessage("未找到实验设置[实验id:%s]", experimentId);
        AssertUtil.falseThenThrow(exptColl.hasSandMode())
                .throwMessage("未找到沙盘设置[实验id:%s]", experimentId);


        final Integer totalPeriods = exptColl.getPeriods();
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
                .ge(ExperimentScoringEntity::getPeriods, 1)
                .list();
        list.forEach(experimentScoringEntity -> {
            String experimentGroupId = experimentScoringEntity.getExperimentGroupId();
            Integer periods1 = experimentScoringEntity.getPeriods();

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
                        .experimentGroupNo(experimentScoringEntity.getExperimentGroupNo())
                        .experimentGroupName(experimentScoringEntity.getExperimentGroupName())
                        .healthIndexScore(experimentScoringEntity.getHealthIndexScore())
                        .knowledgeScore(experimentScoringEntity.getKnowledgeScore())
                        .treatmentPercentScore(experimentScoringEntity.getTreatmentPercentScore())
                        .operateRightScore(experimentScoringEntity.getOperateRightScore())
                        .totalScore(experimentScoringEntity.getTotalScore())
                        .periods(experimentScoringEntity.getPeriods())
                        .rankNo(experimentScoringEntity.getRankNo())
                        .build());

            });
            experimentRankGroupItemResponseList.sort(Comparator.comparing(ExperimentRankGroupItemResponse::getRankNo));
            experimentRankItemResponse.setExperimentRankGroupItemResponseList(experimentRankGroupItemResponseList);
            kPeriodVExperimentRankItemResponseMap.put(period, experimentRankItemResponse);
        });

        List<ExperimentRankItemResponse> experimentRankItemResponseList = new ArrayList<>(kPeriodVExperimentRankItemResponseMap.values());
        experimentRankItemResponseList.sort(Comparator.comparingInt(ExperimentRankItemResponse::getPeriods));

        /**
         * 如果期数未满，那么不执行计算总分，直接返回对应的期数分值
         */
        if (kPeriodVKExperimentGroupIdVExperimentScoringEntityMap.size() < totalPeriods) {
            return ExperimentRankResponse
                    .builder()
                    .totalPeriod(totalPeriods)
                    .experimentRankItemResponseList(experimentRankItemResponseList)
                    .experimentTotalRankItemResponseList(experimentTotalRankItemResponseList)
                    .build();
        }


        List<ExperimentRankingEntity> list1 = experimentRankingService.lambdaQuery()
                .eq(ExperimentRankingEntity::getExperimentInstanceId, experimentId)
                .orderByAsc(ExperimentRankingEntity::getRankingIndex)
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
                        .rankingIndex(experimentRankingEntity.getRankingIndex())
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
            BigDecimalOptional total = BigDecimalOptional.zero();
            BigDecimalOptional cur = BigDecimalOptional.zero();

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
                String totalScore = experimentScoringEntity.getTotalScore();
                Float weight = exptColl.getPeriodWigtht(period);
                total.add(cur.setValue(BigDecimalUtil.tryParseDecimalElseZero(totalScore))
                        .mul(BigDecimalUtil.valueOf(weight))
                        .div(BigDecimalUtil.valueOf(100))
                        .getValue());


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
                    .allPeriodsTotalScore(BigDecimalUtil.formatRoundDecimal(total.getValue(), 2))
                    .experimentTotalRankGroupItemResponseList(experimentTotalRankGroupItemResponseList)
                    .build());
        });
        /* runsix:sort */
        experimentTotalRankItemResponseList.sort(Comparator.comparing(a -> Double.parseDouble(a.getAllPeriodsTotalScore()), Comparator.reverseOrder()));

        List<ExperimentRankingEntity> experimentRankingEntities = new ArrayList<>();
        for (int i = 0; i < experimentTotalRankItemResponseList.size(); i++) {
            ExperimentTotalRankItemResponse experimentTotalRankItemResponse = experimentTotalRankItemResponseList.get(i);

            // 小组排行
            ExperimentRankingEntity experimentRankingEntity = ExperimentRankingEntity.builder()
                    .experimentRankingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentId)
                    .experimentGroupId(experimentTotalRankItemResponse.getExperimentGroupId())
                    .rankingIndex(i + 1)// 排名
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



    private BigDecimal getWeightTotalScore(
            BigDecimal knowledgeWeight, BigDecimal knowledgeScore,
            BigDecimal healthIndexWeight, BigDecimal healthIndexScore,
            BigDecimal medicalRatioWeight, BigDecimal medicalRatioScore,
            BigDecimal operateRightWeight, BigDecimal operateRightScore
    ) {
        BigDecimal finalKnowledgeScore = knowledgeScore.multiply(knowledgeWeight);
        BigDecimal finalHealthIndexScore = healthIndexScore.multiply(healthIndexWeight);
        BigDecimal finalMedicalRatioScoreScore = medicalRatioScore.multiply(medicalRatioWeight);
        BigDecimal finalOperateRightScore = operateRightScore.multiply(operateRightWeight);
        return finalKnowledgeScore.add(finalHealthIndexScore).add(finalMedicalRatioScoreScore).add(finalOperateRightScore)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public RsCalculateMoneyScoreRsResponse rsCalculateMoneyScore(RsCalculateMoneyScoreRequestRs rsCalculateMoneyScoreRequestRs) {
        List<GroupMoneyScoreRsResponse> groupMoneyScoreRsResponseList = new ArrayList<>();
        Integer periods = rsCalculateMoneyScoreRequestRs.getPeriods();
        String experimentId = rsCalculateMoneyScoreRequestRs.getExperimentId();

        Map<String, ExperimentPersonEntity> mapPersons = experimentPersonCache.getMapPersons(experimentId);


        if (mapPersons.isEmpty()) {
            return RsCalculateMoneyScoreRsResponse.builder().build();
        }

        Map<String, String> initMoneyByPeriods = experimentIndicatorInstanceRsBiz.getInitMoneyByPeriods(
                RsInitMoneyRequest
                        .builder()
                        .periods(periods)
                        .experimentPersonIdSet(mapPersons.keySet())
                        .build()
        );
        if (initMoneyByPeriods.isEmpty()) {
            return RsCalculateMoneyScoreRsResponse.builder().build();
        }

        Map<String, BigDecimal> kExperimentOrgGroupIdVTotalMap = new HashMap<>();
        initMoneyByPeriods.forEach((experimentPersonId, money) -> {
            String experimentOrgGroupId = Optional.ofNullable(mapPersons.get(experimentPersonId).getExperimentGroupId()).orElse("");
            if (StringUtils.isBlank(experimentOrgGroupId)) {
                return;
            }
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
                    if (Objects.isNull(bigDecimal)) {
                        bigDecimal = BigDecimal.ZERO;
                    }
                    bigDecimal = bigDecimal.add(cost);
                    kExperimentGroupIdVCostTotalMap.put(experimentGroupId, bigDecimal);
                });
        kExperimentOrgGroupIdVTotalMap.forEach((experimentOrgGroupId, initTotal) -> {
            BigDecimal costTotal = kExperimentGroupIdVCostTotalMap.get(experimentOrgGroupId);
            if (Objects.isNull(costTotal)) {
                costTotal = BigDecimal.ZERO;
            }
            BigDecimal groupMoneyScore;
            if (initTotal.compareTo(BigDecimal.ZERO) <= 0) {
                groupMoneyScore = BigDecimal.ZERO;
            } else {
                groupMoneyScore = BigDecimal.valueOf(100).multiply(BigDecimal.ONE.subtract((costTotal.divide(initTotal, 2, RoundingMode.DOWN))));
            }

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
}
