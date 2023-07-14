package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dows.hep.api.base.indicator.request.RsCalculateCompetitiveScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsCalculateHealthScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsCalculateMoneyScoreRequestRs;
import org.dows.hep.api.base.indicator.response.GroupCompetitiveScoreRsResponse;
import org.dows.hep.api.base.indicator.response.GroupMoneyScoreRsResponse;
import org.dows.hep.api.base.indicator.response.RsCalculateCompetitiveScoreRsResponse;
import org.dows.hep.api.base.indicator.response.RsCalculateMoneyScoreRsResponse;
import org.dows.hep.api.enums.*;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
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
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsCalculateBiz {
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final ExperimentPersonService experimentPersonService;

  private final RsExperimentCrowdsBiz rsExperimentCrowdsBiz;
  private final RsIndicatorExpressionBiz rsIndicatorExpressionBiz;

  /* runsix:TODO 计算医疗占比 */
  @Transactional(rollbackFor = Exception.class)
  public RsCalculateMoneyScoreRsResponse rsCalculateMoneyScore(RsCalculateMoneyScoreRequestRs rsCalculateMoneyScoreRequestRs) {
    List<GroupMoneyScoreRsResponse> groupMoneyScoreRsResponseList = new ArrayList<>();
    return RsCalculateMoneyScoreRsResponse
        .builder()
        .groupMoneyScoreRsResponseList(groupMoneyScoreRsResponseList)
        .build();
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
      if (groupExperimentPersonSizeAtomicInteger.get() != 0) {
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

  private BigDecimal rsCalculateCompetitiveScore(BigDecimal currentHealthScore, BigDecimal defHealthScore, BigDecimal minHealthScore, BigDecimal maxHealthScore) {
    BigDecimal resultBigDecimal = null;
    if (currentHealthScore.compareTo(defHealthScore) >= 0) {
      resultBigDecimal = (currentHealthScore.subtract(defHealthScore).divide(maxHealthScore.subtract(defHealthScore), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100-60))).add(BigDecimal.valueOf(60));
    } else {
      resultBigDecimal = currentHealthScore.subtract(minHealthScore).divide(defHealthScore.subtract(minHealthScore), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(60));
    }
    return resultBigDecimal;
  }

  @Transactional(rollbackFor = Exception.class)
  public void rsCalculateHealthScore(RsCalculateHealthScoreRequestRs rsCalculateHealthScoreRequestRs) throws ExecutionException, InterruptedException {
    String appId = rsCalculateHealthScoreRequestRs.getAppId();
    Integer periods = rsCalculateHealthScoreRequestRs.getPeriods();
    String experimentId = rsCalculateHealthScoreRequestRs.getExperimentId();
    Set<String> experimentPersonIdSet = rsCalculateHealthScoreRequestRs.getExperimentPersonIdSet();

    Map<String, ExperimentCrowdsInstanceRsEntity> kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap = CompletableFuture.runAsync(() -> {
      rsExperimentCrowdsBiz.populateKExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap(
          kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap, experimentId
      );
    });
    cfPopulateKExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap.get();

    Set<String> experimentCrowdsIdSet = new HashSet<>();
    List<ExperimentCrowdsInstanceRsEntity> experimentCrowdsInstanceRsEntityList = new ArrayList<>();
    kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap.forEach((experimentCrowdsId, experimentCrowdsInstanceRsEntity) -> {
      experimentCrowdsIdSet.add(experimentCrowdsId);
      experimentCrowdsInstanceRsEntityList.add(experimentCrowdsInstanceRsEntity);
    });
    experimentCrowdsInstanceRsEntityList.sort(Comparator.comparing(ExperimentCrowdsInstanceRsEntity::getDt));

    Map<String, List<ExperimentRiskModelRsEntity>> kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentCrowdsIdVExperimentRiskModelRsEntityListMap = CompletableFuture.runAsync(() -> {
      rsExperimentCrowdsBiz.populateKExperimentCrowdsIdVExperimentRiskModelRsEntityListMap(
          kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap, experimentCrowdsIdSet);
    });
    cfPopulateKExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.get();

    Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap  = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap = CompletableFuture.runAsync(() -> {
      rsIndicatorExpressionBiz.populateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap(kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap, experimentCrowdsIdSet);
    });
    cfPopulateKExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap.get();

    Set<String> experimentRiskModelIdSet = new HashSet<>();
    kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.forEach((experimentCrowdsId, experimentRiskModelRsEntityList) -> {
      experimentRiskModelIdSet.addAll(experimentRiskModelRsEntityList.stream().map(ExperimentRiskModelRsEntity::getExperimentRiskModelId).collect(Collectors.toSet()));
    });
    Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap = CompletableFuture.runAsync(() -> {
      rsIndicatorExpressionBiz.populateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap(
          kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap, experimentRiskModelIdSet
      );
    });
    cfPopulateKExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap.get();

    Set<String> riskModelExperimentIndicatorExpressionIdSet = new HashSet<>();
    kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap.forEach((riskModelExperimentReasonId, experimentIndicatorExpressionRefList) -> {
      riskModelExperimentIndicatorExpressionIdSet.addAll(experimentIndicatorExpressionRefList.stream().map(ExperimentIndicatorExpressionRefRsEntity::getIndicatorExpressionId).collect(Collectors.toSet()));
    });
    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
    CompletableFuture<Void> cfRiskModelPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = CompletableFuture.runAsync(() -> {
      rsIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap(
          kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap, riskModelExperimentIndicatorExpressionIdSet
      );
    });
    cfRiskModelPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get();

    Map<String, ExperimentIndicatorExpressionRsEntity> kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = CompletableFuture.runAsync(() -> {
      rsIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap(
          kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap, riskModelExperimentIndicatorExpressionIdSet
      );
    });
    cfPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.get();

    Set<String> riskModelMinAndMaxExpressionIndicatorExpressionItemIdSet = new HashSet<>();
    kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.values().forEach(experimentIndicatorExpressionRsEntity -> {
      String minIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
      if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
        riskModelMinAndMaxExpressionIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
      }
      String maxIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
      if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
        riskModelMinAndMaxExpressionIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
      }
    });
    Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfRiskModelPopulateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = CompletableFuture.runAsync(() -> {
      rsIndicatorExpressionBiz.populateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap(
          kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap, riskModelMinAndMaxExpressionIndicatorExpressionItemIdSet
      );
    });
    cfRiskModelPopulateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get();

    Set<String> crowdsExperimentIndicatorExpressionIdSet = new HashSet<>();
    kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap.forEach((experimentReasonId, experimentIndicatorExpressionRefList) -> {
      crowdsExperimentIndicatorExpressionIdSet.addAll(experimentIndicatorExpressionRefList.stream().map(ExperimentIndicatorExpressionRefRsEntity::getIndicatorExpressionId).collect(Collectors.toSet()));
    });
    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
    CompletableFuture<Void> cfCrowdsPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = CompletableFuture.runAsync(() -> {
      rsIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap(
          kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap, crowdsExperimentIndicatorExpressionIdSet
      );
    });
    cfCrowdsPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get();

    Map<String, ExperimentIndicatorValRsEntity> kExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentPersonIdVHealthExperimentIndicatorInstanceRsEntityMap = CompletableFuture.runAsync(() -> {
      rsIndicatorExpressionBiz.populateKExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap(
          kExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap, experimentPersonIdSet, periods);
    });
    cfPopulateKExperimentPersonIdVHealthExperimentIndicatorInstanceRsEntityMap.get();

    Map<String, Map<String, ExperimentIndicatorValRsEntity>> kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValMap = CompletableFuture.runAsync(() -> {
      rsIndicatorExpressionBiz.populateKExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValMap(
          kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, experimentPersonIdSet, periods
      );
    });
    cfPopulateKExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValMap.get();

    List<ExperimentIndicatorValRsEntity> healthExperimentIndicatorValRsEntityList = new ArrayList<>();
    kExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap.forEach((experimentPersonId, healthExperimentIndicatorValRsEntity) -> {
      AtomicReference<String> experimentCrowdsIdAtomicReference = new AtomicReference<>("");
      /* runsix:这个人这期所有指标值 */
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentPersonId);
      if (Objects.isNull(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)) {
        return;
      }

      experimentCrowdsInstanceRsEntityList.forEach(experimentCrowdsInstanceRsEntity -> {
        if (StringUtils.isNotBlank(experimentCrowdsIdAtomicReference.get())) {
          return;
        }
        String experimentCrowdsId = experimentCrowdsInstanceRsEntity.getExperimentCrowdsId();
        List<ExperimentIndicatorExpressionRefRsEntity> experimentIndicatorExpressionRefRsEntityList = kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap.get(experimentCrowdsId);
        if (Objects.isNull(experimentIndicatorExpressionRefRsEntityList) || experimentIndicatorExpressionRefRsEntityList.isEmpty()) {
          return;
        }
        /* runsix:因为人群类型只能产生一个公式 */
        ExperimentIndicatorExpressionRefRsEntity experimentIndicatorExpressionRefRsEntity = experimentIndicatorExpressionRefRsEntityList.get(0);
        String indicatorExpressionId = experimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId();
        List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(indicatorExpressionId);
        if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList) || experimentIndicatorExpressionItemRsEntityList.isEmpty()) {
          return;
        }
        rsIndicatorExpressionBiz.parseIndicatorExpression(
            EnumIndicatorExpressionField.EXPERIMENT.getField(),
            EnumIndicatorExpressionSource.CROWDS.getSource(),
            null,
            experimentCrowdsIdAtomicReference,
            kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
            null,
            experimentIndicatorExpressionItemRsEntityList,
            null,
            null
            );
      });

      if (StringUtils.isBlank(experimentCrowdsIdAtomicReference.get())) {
        return;
      }
      String experimentCrowdsId = experimentCrowdsIdAtomicReference.get();
      List<ExperimentRiskModelRsEntity> experimentRiskModelRsEntityList = kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.get(experimentCrowdsId);
      if (Objects.isNull(experimentRiskModelRsEntityList)) {
        return;
      }
      Map<String, BigDecimal> kExperimentRiskModelIdVTotalScoreMap = new HashMap<>();
      Map<String, Integer> kExperimentRiskModelIdVRiskDeathProbabilityMap = new HashMap<>();
      AtomicInteger totalRiskDeathProbabilityAtomicInteger = new AtomicInteger(0);
      experimentRiskModelRsEntityList.forEach(experimentRiskModelRsEntity -> {
        kExperimentRiskModelIdVRiskDeathProbabilityMap.put(experimentRiskModelRsEntity.getRiskModelId(), experimentRiskModelRsEntity.getRiskDeathProbability());

        Map<String, BigDecimal> kPrincipalIdVScoreMap = new HashMap<>();
        AtomicReference<String> minScoreAtomicReference = new AtomicReference<>("0");
        AtomicReference<String> maxScoreAtomicReference = new AtomicReference<>("0");
        String experimentRiskModelId = experimentRiskModelRsEntity.getExperimentRiskModelId();
        List<ExperimentIndicatorExpressionRefRsEntity> experimentIndicatorExpressionRefRsEntityList = kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap.get(experimentRiskModelId);
        if (Objects.isNull(experimentIndicatorExpressionRefRsEntityList) || experimentIndicatorExpressionRefRsEntityList.isEmpty()) {
          return;
        }
        experimentIndicatorExpressionRefRsEntityList.forEach(experimentIndicatorExpressionRefRsEntity -> {
          AtomicReference<String> resultAtomicReference = new AtomicReference<>("0");
          String indicatorExpressionId = experimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId();
          ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.get(indicatorExpressionId);
          if (Objects.isNull(experimentIndicatorExpressionRsEntity)) {
            return;
          }
          List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(indicatorExpressionId);
          if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList) || experimentIndicatorExpressionItemRsEntityList.isEmpty()) {
            return;
          }
          ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity = null;
          ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity = null;
          String minIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
          if (StringUtils.isNotBlank(minIndicatorExpressionItemId)
              && Objects.nonNull(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(minIndicatorExpressionItemId))
          ) {
            minExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(minIndicatorExpressionItemId);
            String min = minScoreAtomicReference.get();
            String currentMin = minExperimentIndicatorExpressionItemRsEntity.getResultExpression();
            if (Double.parseDouble(currentMin) < Double.parseDouble(min)) {
              minScoreAtomicReference.set(String.valueOf(Double.parseDouble(currentMin)));
            }
          }
          String maxIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
          if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)
              && Objects.nonNull(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(maxIndicatorExpressionItemId))
          ) {
            maxExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(maxIndicatorExpressionItemId);
            String max = maxScoreAtomicReference.get();
            String currentMax = maxExperimentIndicatorExpressionItemRsEntity.getResultExpression();
            if (Double.parseDouble(currentMax) > Double.parseDouble(max)) {
              maxScoreAtomicReference.set(currentMax);
            }
          }
          rsIndicatorExpressionBiz.parseIndicatorExpression(
              EnumIndicatorExpressionField.EXPERIMENT.getField(), EnumIndicatorExpressionSource.RISK_MODEL.getSource(), EnumIndicatorExpressionScene.RISK_MODEL.getScene(),
              resultAtomicReference,
              kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
              experimentIndicatorExpressionRsEntity,
              experimentIndicatorExpressionItemRsEntityList,
              minExperimentIndicatorExpressionItemRsEntity,
              maxExperimentIndicatorExpressionItemRsEntity
              );
          /* runsix:只有不等于0的才计算 */
          if ("0".equals(resultAtomicReference.get())) {
            return;
          }
          try {
            int totalRiskDeathProbability = totalRiskDeathProbabilityAtomicInteger.get();
            totalRiskDeathProbability += experimentRiskModelRsEntity.getRiskDeathProbability();
            totalRiskDeathProbabilityAtomicInteger.set(totalRiskDeathProbability);
            kPrincipalIdVScoreMap.put(experimentIndicatorExpressionRsEntity.getPrincipalId(), BigDecimal.valueOf(Double.parseDouble(resultAtomicReference.get())));
          } catch (Exception exception) {
            /* runsix:TODO  */
          }
        });
        BigDecimal finalTotalScore = BigDecimal.ZERO;
        if (kPrincipalIdVScoreMap.size() == 1) {
          finalTotalScore = calculateRiskModelScore(
              BigDecimal.valueOf(Double.parseDouble(minScoreAtomicReference.get())),
              BigDecimal.valueOf(Double.parseDouble(maxScoreAtomicReference.get())),
              kPrincipalIdVScoreMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add)
          );
        } else if (kPrincipalIdVScoreMap.size() >= 2){
          List<BigDecimal> leOneList = new ArrayList<>();
          List<BigDecimal> gtOneList = new ArrayList<>();
          kPrincipalIdVScoreMap.values().forEach(score -> {
            if (score.compareTo(BigDecimal.ONE) <= 0) {
              leOneList.add(score);
            } else {
              gtOneList.add(score);
            }
          });
          BigDecimal multiplyResult = BigDecimal.ZERO;
          if (!leOneList.isEmpty()) {
            multiplyResult =  leOneList.stream().reduce(BigDecimal.ONE, BigDecimal::multiply);
          }
          BigDecimal addResult = gtOneList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
          finalTotalScore = calculateRiskModelScore(
              BigDecimal.valueOf(Double.parseDouble(minScoreAtomicReference.get())),
              BigDecimal.valueOf(Double.parseDouble(maxScoreAtomicReference.get())),
              addResult.add(multiplyResult).setScale(2, RoundingMode.DOWN)
          );
        }
        kExperimentRiskModelIdVTotalScoreMap.put(experimentRiskModelRsEntity.getExperimentRiskModelId(), finalTotalScore);
      });
      if (!kExperimentRiskModelIdVTotalScoreMap.isEmpty()
          && !kExperimentRiskModelIdVRiskDeathProbabilityMap.isEmpty()
          && totalRiskDeathProbabilityAtomicInteger.get() != 0
      ) {
        BigDecimal newHealthScoreBigDecimal = calculateFinalHealthScore(kExperimentRiskModelIdVTotalScoreMap, kExperimentRiskModelIdVRiskDeathProbabilityMap, totalRiskDeathProbabilityAtomicInteger);

        healthExperimentIndicatorValRsEntity.setCurrentVal(newHealthScoreBigDecimal.toString());
        healthExperimentIndicatorValRsEntityList.add(healthExperimentIndicatorValRsEntity);
      }
    });
    experimentIndicatorValRsService.saveOrUpdateBatch(healthExperimentIndicatorValRsEntityList);
  }

  private BigDecimal calculateRiskModelScore(
      BigDecimal min,
      BigDecimal max,
      BigDecimal current
  ) {
    if (BigDecimal.ZERO.compareTo(min.subtract(max)) == 0) {
      return BigDecimal.ZERO;
    }
    return BigDecimal.valueOf(100).multiply(current.subtract(max)).divide(min.subtract(max), 2, RoundingMode.DOWN);
  }

  private BigDecimal calculateFinalHealthScore(
      Map<String, BigDecimal> kExperimentRiskModelIdVTotalScoreMap,
      Map<String, Integer> kExperimentRiskModelIdVRiskDeathProbabilityMap,
      AtomicInteger totalRiskDeathProbabilityAtomicInteger
  ) {
    BigDecimal finalScoreBigDecimal = BigDecimal.ZERO;
    int totalRiskDeathProbability = totalRiskDeathProbabilityAtomicInteger.get();
    kExperimentRiskModelIdVTotalScoreMap.forEach((experimentRiskModelId, totalScore) -> {
      Integer riskDeathProbability = kExperimentRiskModelIdVRiskDeathProbabilityMap.get(experimentRiskModelId);
      finalScoreBigDecimal.add(
          BigDecimal.valueOf(riskDeathProbability)
              .divide(BigDecimal.valueOf(totalRiskDeathProbability), 2, RoundingMode.DOWN)
              .multiply(totalScore)
      );
    });
    return finalScoreBigDecimal;
  }
}
