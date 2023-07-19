package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.GroupCompetitiveScoreRsResponse;
import org.dows.hep.api.base.indicator.response.GroupMoneyScoreRsResponse;
import org.dows.hep.api.base.indicator.response.RsCalculateCompetitiveScoreRsResponse;
import org.dows.hep.api.base.indicator.response.RsCalculateMoneyScoreRsResponse;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.RsCalculateBizException;
import org.dows.hep.biz.request.CaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
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
  private final CaseIndicatorInstanceService caseIndicatorInstanceService;
  private final CaseIndicatorRuleService caseIndicatorRuleService;
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final ExperimentPersonService experimentPersonService;

  private final RsExperimentCrowdsBiz rsExperimentCrowdsBiz;
  private final RsExperimentIndicatorExpressionBiz rsExperimentIndicatorExpressionBiz;
  private final RsExperimentIndicatorInstanceBiz rsExperimentIndicatorInstanceBiz;
  private final RsExperimentPersonBiz rsExperimentPersonBiz;
  private final RsCaseIndicatorInstanceBiz rsCaseIndicatorInstanceBiz;
  private final RsCaseIndicatorExpressionBiz rsCaseIndicatorExpressionBiz;
  private final RsCrowdsBiz rsCrowdsBiz;
  private final RsUtilBiz rsUtilBiz;
  private final RsIndicatorExpressionBiz rsIndicatorExpressionBiz;
  private final IndicatorRuleService indicatorRuleService;
  private final RsIndicatorInstanceBiz rsIndicatorInstanceBiz;

  @Transactional(rollbackFor = Exception.class)
  public void caseReCalculateOnePerson(ReCalculateOnePersonRequestRs reCalculateOnePersonRequestRs) throws ExecutionException, InterruptedException {
    String appId = reCalculateOnePersonRequestRs.getAppId();
    String accountId = reCalculateOnePersonRequestRs.getAccountId();

    Map<String, CaseIndicatorInstanceEntity> kCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorInstanceBiz.populateKCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap(kCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap, accountId);
    });
    cfPopulateKCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap.get();
    if (kCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap.isEmpty()) {return;}
    Set<String> caseIndicatorInstanceIdSet = new HashSet<>();
    kCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap.forEach((caseIndicatorInstanceId, caseIndicatorInstanceEntity) -> {
      caseIndicatorInstanceIdSet.add(caseIndicatorInstanceId);
    });

    Map<String, Set<String>> kCaseIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKCaseIndicatorInstanceIdInfluencedIndicatorInstanceIdSetMap = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorExpressionBiz.populateKCaseIndicatorInstanceIdInfluencedIndicatorInstanceIdSetMap(kCaseIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap, caseIndicatorInstanceIdSet);
    });
    cfPopulateKCaseIndicatorInstanceIdInfluencedIndicatorInstanceIdSetMap.get();

    Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorExpressionBiz.populateKCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap(kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap, caseIndicatorInstanceIdSet);
    });
    cfPopulateKCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.get();

    Map<String, CaseIndicatorExpressionEntity> kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap = new HashMap<>();
    CompletableFuture<Void> cfKCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorExpressionBiz.populateKCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap(kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap, caseIndicatorInstanceIdSet);
    });
    cfKCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap.get();
    Set<String> minAndMaxCaseIndicatorExpressionItemIdSet = new HashSet<>();
    kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap.forEach((caseIndicatorInstanceId, caseIndicatorExpressionEntity) -> {
      String minIndicatorExpressionItemId = caseIndicatorExpressionEntity.getMinIndicatorExpressionItemId();
      if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {minAndMaxCaseIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);}
      String maxIndicatorExpressionItemId = caseIndicatorExpressionEntity.getMaxIndicatorExpressionItemId();
      if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {minAndMaxCaseIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);}
    });

    Map<String, List<CaseIndicatorExpressionItemEntity>> kCaseIndicatorInstanceIdVCaseIndicatorExpressionItemEntityListMap = new HashMap<>();
    CompletableFuture<Void> cfKCaseIndicatorInstanceIdVCaseIndicatorExpressionItemEntityListMap = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorExpressionBiz.populateKCaseIndicatorInstanceIdVCaseIndicatorExpressionItemEntityListMap(kCaseIndicatorInstanceIdVCaseIndicatorExpressionItemEntityListMap, caseIndicatorInstanceIdSet);
    });
    cfKCaseIndicatorInstanceIdVCaseIndicatorExpressionItemEntityListMap.get();

    Map<String, CaseIndicatorExpressionItemEntity> kMinAndMaxCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap = new HashMap<>();
    CompletableFuture<Void> cfKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorExpressionBiz.populateKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap(kMinAndMaxCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap, minAndMaxCaseIndicatorExpressionItemIdSet);
    });
    cfKCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.get();

    Set<String> hasCalculatedCaseIndicatorInstanceIdSet = new HashSet<>();
    Set<String> needCalculateCaseIndicatorInstanceIdSet = new HashSet<>(caseIndicatorInstanceIdSet);
    while (!needCalculateCaseIndicatorInstanceIdSet.isEmpty()) {
      AtomicBoolean hasFindOne = new AtomicBoolean(Boolean.FALSE);
      needCalculateCaseIndicatorInstanceIdSet.forEach(needCalculateCaseIndicatorInstanceId -> {
        if (hasFindOne.get()) {return;}
        Set<String> influencedIndicatorInstanceIdSet = kCaseIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.get(needCalculateCaseIndicatorInstanceId);
        if (Objects.isNull(influencedIndicatorInstanceIdSet) || influencedIndicatorInstanceIdSet.isEmpty()
            || hasCalculatedCaseIndicatorInstanceIdSet.containsAll(influencedIndicatorInstanceIdSet)
        ) {
          /* runsix:TODO 这里应该是默认值，算错了就用当前值计算 */
          CaseIndicatorRuleEntity caseIndicatorRuleEntity = kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.get(needCalculateCaseIndicatorInstanceId);
          String def = caseIndicatorRuleEntity.getDef();
          AtomicReference<String> resultAtomicReference = new AtomicReference<>(def);
          CaseIndicatorExpressionEntity caseIndicatorExpressionEntity = kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap.get(needCalculateCaseIndicatorInstanceId);
          List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = kCaseIndicatorInstanceIdVCaseIndicatorExpressionItemEntityListMap.get(needCalculateCaseIndicatorInstanceId);
          CaseIndicatorExpressionItemEntity minCaseIndicatorExpressionItemEntity = null;
          if (Objects.nonNull(caseIndicatorExpressionEntity)
              && StringUtils.isNotBlank(caseIndicatorExpressionEntity.getMinIndicatorExpressionItemId())
              && Objects.nonNull(kMinAndMaxCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.get(caseIndicatorExpressionEntity.getMinIndicatorExpressionItemId()))
          ) {
            minCaseIndicatorExpressionItemEntity = kMinAndMaxCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.get(caseIndicatorExpressionEntity.getMinIndicatorExpressionItemId());
          }
          CaseIndicatorExpressionItemEntity maxCaseIndicatorExpressionItemEntity = null;
          if (Objects.nonNull(caseIndicatorExpressionEntity)
              && StringUtils.isNotBlank(caseIndicatorExpressionEntity.getMaxIndicatorExpressionItemId())
              && Objects.nonNull(kMinAndMaxCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.get(caseIndicatorExpressionEntity.getMaxIndicatorExpressionItemId()))
          ) {
            maxCaseIndicatorExpressionItemEntity = kMinAndMaxCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemMap.get(caseIndicatorExpressionEntity.getMaxIndicatorExpressionItemId());
          }

          rsCaseIndicatorExpressionBiz.parseCaseIndicatorExpression(
              EnumIndicatorExpressionField.CASE.getField(), EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource(), EnumIndicatorExpressionScene.CASE_RE_CALCULATE.getScene(),
              resultAtomicReference,
              new HashMap<>(),
              DatabaseCalIndicatorExpressionRequest.builder().build(),
              CaseCalIndicatorExpressionRequest
                  .builder()
                  .kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap(kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap)
                  .caseIndicatorExpressionEntity(caseIndicatorExpressionEntity)
                  .caseIndicatorExpressionItemEntityList(caseIndicatorExpressionItemEntityList)
                  .minCaseIndicatorExpressionItemEntity(minCaseIndicatorExpressionItemEntity)
                  .maxCaseIndicatorExpressionItemEntity(maxCaseIndicatorExpressionItemEntity)
                  .build()
          );
          caseIndicatorRuleEntity.setDef(resultAtomicReference.get());
          kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.put(needCalculateCaseIndicatorInstanceId, caseIndicatorRuleEntity);
          hasCalculatedCaseIndicatorInstanceIdSet.add(needCalculateCaseIndicatorInstanceId);
          needCalculateCaseIndicatorInstanceIdSet.remove(needCalculateCaseIndicatorInstanceId);
        }
      });
    }


    caseIndicatorRuleService.saveOrUpdateBatch(kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.values());
  }

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
  public void experimentRsCalculateHealthScore(ExperimentRsCalculateHealthScoreRequestRs experimentRsCalculateHealthScoreRequestRs) throws ExecutionException, InterruptedException {
    String appId = experimentRsCalculateHealthScoreRequestRs.getAppId();
    Integer periods = experimentRsCalculateHealthScoreRequestRs.getPeriods();
    String experimentId = experimentRsCalculateHealthScoreRequestRs.getExperimentId();
    Set<String> experimentPersonIdSet = experimentRsCalculateHealthScoreRequestRs.getExperimentPersonIdSet();

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
      rsExperimentIndicatorExpressionBiz.populateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap(kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap, experimentCrowdsIdSet);
    });
    cfPopulateKExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap.get();

    Set<String> experimentRiskModelIdSet = new HashSet<>();
    kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.forEach((experimentCrowdsId, experimentRiskModelRsEntityList) -> {
      experimentRiskModelIdSet.addAll(experimentRiskModelRsEntityList.stream().map(ExperimentRiskModelRsEntity::getExperimentRiskModelId).collect(Collectors.toSet()));
    });
    Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorExpressionBiz.populateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap(
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
      rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap(
          kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap, riskModelExperimentIndicatorExpressionIdSet
      );
    });
    cfRiskModelPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get();

    Map<String, ExperimentIndicatorExpressionRsEntity> kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap(
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
      rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap(
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
      rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap(
          kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap, crowdsExperimentIndicatorExpressionIdSet
      );
    });
    cfCrowdsPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get();

    Map<String, ExperimentIndicatorValRsEntity> kExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentPersonIdVHealthExperimentIndicatorInstanceRsEntityMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorExpressionBiz.populateKExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap(
          kExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap, experimentPersonIdSet, periods);
    });
    cfPopulateKExperimentPersonIdVHealthExperimentIndicatorInstanceRsEntityMap.get();

    Map<String, Map<String, ExperimentIndicatorValRsEntity>> kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorExpressionBiz.populateKExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValMap(
          kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, experimentPersonIdSet, periods
      );
    });
    cfPopulateKExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValMap.get();

    List<ExperimentIndicatorValRsEntity> healthExperimentIndicatorValRsEntityList = new ArrayList<>();
    kExperimentPersonIdVHealthExperimentIndicatorValRsEntityMap.forEach((experimentPersonId, healthExperimentIndicatorValRsEntity) -> {
      AtomicReference<String> experimentCrowdsIdAtomicReference = new AtomicReference<>(RsUtilBiz.RESULT_DROP);
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
        rsExperimentIndicatorExpressionBiz.parseExperimentIndicatorExpression(
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
          rsExperimentIndicatorExpressionBiz.parseExperimentIndicatorExpression(
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
    AtomicReference<BigDecimal> finalScoreBigDecimal = new AtomicReference<>(BigDecimal.ZERO);
    int totalRiskDeathProbability = totalRiskDeathProbabilityAtomicInteger.get();
    kExperimentRiskModelIdVTotalScoreMap.forEach((experimentRiskModelId, totalScore) -> {
      Integer riskDeathProbability = kExperimentRiskModelIdVRiskDeathProbabilityMap.get(experimentRiskModelId);
      finalScoreBigDecimal.set(
          finalScoreBigDecimal.get().add(
              BigDecimal.valueOf(riskDeathProbability)
                  .divide(BigDecimal.valueOf(totalRiskDeathProbability), 2, RoundingMode.DOWN)
                  .multiply(totalScore)
          )
      );
    });
    return finalScoreBigDecimal.get();
  }

  @Transactional(rollbackFor = Exception.class)
  public void experimentReCalculateAllPerson(RsCalculateAllPersonRequestRs rsCalculateAllPersonRequestRs) throws ExecutionException, InterruptedException {
    String appId = rsCalculateAllPersonRequestRs.getAppId();
    String experimentId = rsCalculateAllPersonRequestRs.getExperimentId();
    Integer periods = rsCalculateAllPersonRequestRs.getPeriods();

    Set<String> reasonIdSet = new HashSet<>();
    Map<String, List<ExperimentIndicatorExpressionRsEntity>> kReasonIdVExperimentIndicatorExpressionRsEntityListMap = new HashMap<>();
    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
    Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = new HashMap<>();

    Set<String> experimentPersonIdSet = new HashSet<>();
    CompletableFuture<Void> cfPopulateExperimentPersonIdSet = CompletableFuture.runAsync(() -> {
      rsExperimentPersonBiz.populateExperimentPersonIdSet(experimentPersonIdSet, experimentId);
    });
    cfPopulateExperimentPersonIdSet.get();

    Map<String, List<ExperimentIndicatorInstanceRsEntity>> kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap = CompletableFuture.runAsync(() -> {
       rsExperimentIndicatorInstanceBiz.populateKExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap(
          kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap, experimentPersonIdSet, experimentId
       );
    });
    cfPopulateKExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap.get();

    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
    kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap.forEach((experimentPersonId, experimentIndicatorInstanceRsEntityList) -> {
      experimentIndicatorInstanceRsEntityList.forEach(experimentIndicatorInstanceRsEntity -> {
        experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
      });
    });
    if (experimentIndicatorInstanceIdSet.isEmpty()) {return;}
    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorInstanceBiz.populateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, periods, experimentIndicatorInstanceIdSet
      );
    });
    cfPopulateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get();

    reasonIdSet.addAll(experimentIndicatorInstanceIdSet);
    CompletableFuture<Void> cfPopulateParseParam = CompletableFuture.runAsync(() -> {
      try {
        rsExperimentIndicatorExpressionBiz.populateParseParam(
            reasonIdSet,
            kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
            kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
            kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap
        );
      } catch (Exception e) {
        log.error("RsCalculateBiz.reCalculateAllPerson.cfPopulateParseParam rsCalculateAllPersonRequestRs:{}" , rsCalculateAllPersonRequestRs, e);
        throw new RsCalculateBizException(EnumESC.RS_CALCULATE_ERROR);
      }
    });
    cfPopulateParseParam.get();

    CompletableFuture<Void> cfReCalculateAllExperimentIndicatorInstance = CompletableFuture.runAsync(() -> {
      try {
        rsExperimentIndicatorExpressionBiz.reCalculateAllExperimentIndicatorInstance(
            kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap,
            kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
            kReasonIdVExperimentIndicatorExpressionRsEntityListMap,
            kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap,
            kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap
        );
      } catch (Exception e) {
        log.error("RsCalculateBiz.reCalculateAllPerson.cfReCalculateAllExperimentIndicatorInstance rsCalculateAllPersonRequestRs:{}" , rsCalculateAllPersonRequestRs, e);
        throw new RsCalculateBizException(EnumESC.RS_CALCULATE_ERROR);
      }
    });
    cfReCalculateAllExperimentIndicatorInstance.get();

    CompletableFuture<Void> cfFinalOperation = CompletableFuture.runAsync(() -> {
      experimentIndicatorValRsService.saveOrUpdateBatch(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.values());
    });
    cfFinalOperation.get();
  }

  @Transactional(rollbackFor = Exception.class)
  public void caseRsCalculateHealthScore(CaseRsCalculateHealthScoreRequestRs caseRsCalculateHealthScoreRequestRs) throws ExecutionException, InterruptedException {
    /* runsix:param */
    String appId = caseRsCalculateHealthScoreRequestRs.getAppId();
    String accountId = caseRsCalculateHealthScoreRequestRs.getAccountId();
    /* runsix:populate */
    Set<String> allReasonIdSet = new HashSet<>();
    Set<String> allCaseReasonIdSet = new HashSet<>();
    Set<String> crowdsIdSet = new HashSet<>();
    Set<String> riskModelIdSet = new HashSet<>();
    Set<String> caseIndicatorInstanceIdSet = new HashSet<>();
    Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap = new HashMap<>();

    /* runsix:人群类别和死亡原因在数据库 */
    Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap = new HashMap<>();
    Map<String, List<IndicatorExpressionEntity>> kReasonIdVIndicatorExpressionEntityListMap = new HashMap<>();
    Map<String, List<IndicatorExpressionItemEntity>> kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap = new HashMap<>();
    Map<String, IndicatorExpressionItemEntity> kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap = new HashMap<>();

    Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap = new HashMap<>();
    Map<String, List<CaseIndicatorExpressionEntity>> kCaseReasonIdVCaseIndicatorExpressionEntityListMap = new HashMap<>();
    Map<String, List<CaseIndicatorExpressionItemEntity>> kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap = new HashMap<>();
    Map<String, CaseIndicatorExpressionItemEntity> kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemEntityMap = new HashMap<>();

    CompletableFuture<Void> cfPopulateKIndicatorInstanceIdVCaseIndicatorInstanceIdMap = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorInstanceBiz.populateKIndicatorInstanceIdVCaseIndicatorInstanceIdMap(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, accountId);
    });
    cfPopulateKIndicatorInstanceIdVCaseIndicatorInstanceIdMap.get();
    if (kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.isEmpty()) {return;}

    AtomicReference<CaseIndicatorRuleEntity> caseIndicatorRuleEntityAR = new AtomicReference<>();
    CompletableFuture<Void> cfPopulateHealthPointCaseIndicatorRuleEntity = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorInstanceBiz.populateHealthPointCaseIndicatorRuleEntity(caseIndicatorRuleEntityAR, accountId);
    });
    cfPopulateHealthPointCaseIndicatorRuleEntity.get();

    /* runsix:crowds */
    Map<String, CrowdsInstanceEntity> kCrowdsIdVCrowdsInstanceEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKCrowdsIdVCrowdsInstanceEntityMap = CompletableFuture.runAsync(() -> {
      rsCrowdsBiz.populateEnableKCrowdsIdVCrowdsInstanceEntityMap(kCrowdsIdVCrowdsInstanceEntityMap, appId);
    });
    cfPopulateKCrowdsIdVCrowdsInstanceEntityMap.get();
    if (kCrowdsIdVCrowdsInstanceEntityMap.isEmpty()) {return;}

    crowdsIdSet.addAll(kCrowdsIdVCrowdsInstanceEntityMap.keySet());

    /* runsix:riskModel */
    Map<String, List<RiskModelEntity>> kCrowdsIdVRiskModelEntityListMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKCrowdsIdVRiskModelEntityListMap = CompletableFuture.runAsync(() -> {
      rsCrowdsBiz.populateEnableKCrowdsIdVRiskModelEntityListMap(kCrowdsIdVRiskModelEntityListMap, crowdsIdSet);
    });
    cfPopulateKCrowdsIdVRiskModelEntityListMap.get();
    if (kCrowdsIdVRiskModelEntityListMap.isEmpty()) {return;}
    kCrowdsIdVRiskModelEntityListMap.forEach((crowdsId, riskModelEntityList) -> {
      riskModelIdSet.addAll(riskModelEntityList.stream().map(RiskModelEntity::getRiskModelId).collect(Collectors.toSet()));
    });

    /* runsix:caseIndicatorInstanceIdSet */
    Map<String, CaseIndicatorInstanceEntity> kCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorInstanceBiz.populateKCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap(kCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap, accountId);
    });
    cfPopulateKCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap.get();
    if (kCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap.isEmpty()) {return;}
    caseIndicatorInstanceIdSet.addAll(kCaseIndicatorInstanceIdVCaseIndicatorInstanceEntityMap.keySet());

    CompletableFuture<Void> cfPopulateKCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorInstanceBiz.populateKCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap(kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap, caseIndicatorInstanceIdSet);
    });
    cfPopulateKCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.get();
    if (kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.isEmpty()) {return;}

    /* runsix:allReasonIdSet && allCaseReasonIdSet */
    allReasonIdSet.addAll(crowdsIdSet);
    allReasonIdSet.addAll(riskModelIdSet);
    allCaseReasonIdSet.addAll(caseIndicatorInstanceIdSet);

    /* runsix:database param */
    CompletableFuture<Void> cfDatabasePopulateParseParam = CompletableFuture.runAsync(() -> {
      try {
        rsIndicatorExpressionBiz.populateParseParam(
            allReasonIdSet,
            kReasonIdVIndicatorExpressionEntityListMap,
            kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap,
            kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap
        );
      } catch (Exception e) {
        log.error("RsCalculateBiz.caseRsCalculateHealthScore caseRsCalculateHealthScoreRequestRs:{}" , caseRsCalculateHealthScoreRequestRs, e);
        throw new RsCalculateBizException(EnumESC.DATABASE_RS_CALCULATE_HEALTH_SCORE);
      }
    });
    cfDatabasePopulateParseParam.get();

    /* runsix:case param */
    CompletableFuture<Void> cfCasePopulateParseParam = CompletableFuture.runAsync(() -> {
      try {
        rsCaseIndicatorExpressionBiz.populateCaseParseParam(
            allCaseReasonIdSet,
            kCaseReasonIdVCaseIndicatorExpressionEntityListMap,
            kCaseIndicatorExpressionIdVCaseIndicatorExpressionItemEntityListMap,
            kCaseIndicatorExpressionItemIdVCaseIndicatorExpressionItemEntityMap
        );
      } catch (Exception e) {
        log.error("RsCalculateBiz.caseRsCalculateHealthScore caseRsCalculateHealthScoreRequestRs:{}" , caseRsCalculateHealthScoreRequestRs, e);
        throw new RsCalculateBizException(EnumESC.CASE_RS_CALCULATE_HEALTH_SCORE);
      }
    });
    cfCasePopulateParseParam.get();

    /* runsix:calculate */
    kCrowdsIdVCrowdsInstanceEntityMap.forEach((crowdsId, crowdsInstanceEntity) -> {
      /* runsix:一个人只能适用于一个人群类型 */
      AtomicBoolean hasFindCrowdsAR = new AtomicBoolean(Boolean.FALSE);
      if (hasFindCrowdsAR.get()) {return;}

      AtomicReference<String> crowdsAR = new AtomicReference<>(RsUtilBiz.RESULT_DROP);
      IndicatorExpressionEntity indicatorExpressionEntity = null;
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = new ArrayList<>();
      List<IndicatorExpressionEntity> indicatorExpressionEntityList = kReasonIdVIndicatorExpressionEntityListMap.get(crowdsId);
      /* runsix:人群类型只能产生一个公式 */
      if (Objects.nonNull(indicatorExpressionEntityList) && !indicatorExpressionEntityList.isEmpty()) {
        indicatorExpressionEntity = indicatorExpressionEntityList.get(0);
      }
      if (Objects.nonNull(indicatorExpressionEntity)) {
        String indicatorExpressionId = indicatorExpressionEntity.getIndicatorExpressionId();
        List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList1 = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
        if (Objects.nonNull(indicatorExpressionItemEntityList1) && !indicatorExpressionItemEntityList1.isEmpty()) {
          /* runsix:人群类型公式只能有一个条件 */
          indicatorExpressionItemEntityList.add(indicatorExpressionItemEntityList1.get(0));
        }
      }
      rsCaseIndicatorExpressionBiz.parseCaseIndicatorExpression(
          EnumIndicatorExpressionField.CASE.getField(), EnumIndicatorExpressionSource.CROWDS.getSource(), EnumIndicatorExpressionScene.CASE_CALCULATE_HEALTH_POINT.getScene(),
          crowdsAR,
          kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
          DatabaseCalIndicatorExpressionRequest
              .builder()
              .indicatorExpressionEntity(indicatorExpressionEntity)
              .indicatorExpressionItemEntityList(indicatorExpressionItemEntityList)
              .build(),
          CaseCalIndicatorExpressionRequest
              .builder()
              .kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap(kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap)
              .build()
      );
      if (StringUtils.equals(RsUtilBiz.RESULT_DROP, crowdsAR.get())) {return;}
      if (!StringUtils.equalsIgnoreCase(Boolean.TRUE.toString(), crowdsAR.get())) {return;}
      hasFindCrowdsAR.set(Boolean.TRUE);
      List<RiskModelEntity> riskModelEntityList = kCrowdsIdVRiskModelEntityListMap.get(crowdsId);
      if (Objects.isNull(riskModelEntityList) || riskModelEntityList.isEmpty()) {return;}
      Map<String, BigDecimal> kRiskModelIdVTotalScoreMap = new HashMap<>();
      Map<String, Integer> kRiskModelIdVRiskDeathProbabilityMap = new HashMap<>();
      riskModelEntityList.forEach(riskModelEntity -> {
        Map<String, BigDecimal> kPrincipalIdVScoreMap = new HashMap<>();
        AtomicReference<BigDecimal> minScoreAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> maxScoreAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
        String riskModelId = riskModelEntity.getRiskModelId();
        AtomicReference<BigDecimal> singleRiskModelAR = new AtomicReference<>(BigDecimal.ZERO);
        List<IndicatorExpressionEntity> indicatorExpressionEntityList0 = kReasonIdVIndicatorExpressionEntityListMap.get(riskModelId);
        /* runsix:危险原因会产生多个公式 */
        if (Objects.isNull(indicatorExpressionEntityList0) || indicatorExpressionEntityList0.isEmpty()) {return;}
        indicatorExpressionEntityList0.forEach(indicatorExpressionEntity2 -> {
          AtomicReference<String> singleExpressionResultRiskModelAR = new AtomicReference<>(RsUtilBiz.RESULT_DROP);
          String indicatorExpressionId = indicatorExpressionEntity2.getIndicatorExpressionId();
          List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList2 = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
          if (Objects.isNull(indicatorExpressionItemEntityList2) || indicatorExpressionItemEntityList2.isEmpty()) {return;}
          String minIndicatorExpressionItemId = indicatorExpressionEntity2.getMinIndicatorExpressionItemId();
          IndicatorExpressionItemEntity minIndicatorExpressionItemEntity = kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap.get(minIndicatorExpressionItemId);
          String maxIndicatorExpressionItemId = indicatorExpressionEntity2.getMinIndicatorExpressionItemId();
          IndicatorExpressionItemEntity maxIndicatorExpressionItemEntity = kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap.get(maxIndicatorExpressionItemId);
          rsCaseIndicatorExpressionBiz.parseCaseIndicatorExpression(
              EnumIndicatorExpressionField.CASE.getField(), EnumIndicatorExpressionSource.RISK_MODEL.getSource(), EnumIndicatorExpressionScene.CASE_CALCULATE_HEALTH_POINT.getScene(),
              singleExpressionResultRiskModelAR,
              kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
              DatabaseCalIndicatorExpressionRequest
                  .builder()
                  .indicatorExpressionEntity(indicatorExpressionEntity2)
                  .indicatorExpressionItemEntityList(indicatorExpressionItemEntityList2)
                  .minIndicatorExpressionItemEntity(minIndicatorExpressionItemEntity)
                  .maxIndicatorExpressionItemEntity(maxIndicatorExpressionItemEntity)
                  .build(),
              CaseCalIndicatorExpressionRequest
                  .builder()
                  .kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap(kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap)
                  .build()
          );
          if (StringUtils.equals(RsUtilBiz.RESULT_DROP, singleExpressionResultRiskModelAR.get())) {return;}

          BigDecimal curVal = BigDecimal.valueOf(Double.parseDouble(singleExpressionResultRiskModelAR.get()));
          if (curVal.compareTo(minScoreAtomicReference.get()) < 0) {minScoreAtomicReference.set(curVal);}
          if (curVal.compareTo(maxScoreAtomicReference.get()) > 0) {maxScoreAtomicReference.set(curVal);}

          kRiskModelIdVRiskDeathProbabilityMap.put(riskModelId, riskModelEntity.getRiskDeathProbability());
          kPrincipalIdVScoreMap.put(indicatorExpressionEntity2.getPrincipalId(), BigDecimal.valueOf(Double.parseDouble(singleExpressionResultRiskModelAR.get())));
        });
        rsUtilBiz.calculateRiskModelScore(singleRiskModelAR, kPrincipalIdVScoreMap, minScoreAtomicReference, maxScoreAtomicReference);
        kRiskModelIdVTotalScoreMap.put(riskModelId, singleRiskModelAR.get());
      });
      if (kRiskModelIdVRiskDeathProbabilityMap.isEmpty()) {return;}
      Integer totalRiskDeathProbability = kRiskModelIdVRiskDeathProbabilityMap.values().stream().reduce(0, Integer::sum);
      CaseIndicatorRuleEntity caseIndicatorRuleEntity = caseIndicatorRuleEntityAR.get();
      BigDecimal newHealthPoint = rsUtilBiz.newCalculateFinalHealthScore(kRiskModelIdVTotalScoreMap, kRiskModelIdVRiskDeathProbabilityMap, totalRiskDeathProbability);
      caseIndicatorRuleEntity.setDef(newHealthPoint.setScale(2, RoundingMode.DOWN).toString());
    });
    if (Objects.nonNull(caseIndicatorRuleEntityAR.get())) {caseIndicatorRuleService.saveOrUpdate(caseIndicatorRuleEntityAR.get());}
  }

  @Transactional(rollbackFor = Exception.class)
  public void databaseRsCalculateHealthScore(DatabaseRsCalculateHealthScoreRequestRs databaseRsCalculateHealthScoreRequestRs) throws ExecutionException, InterruptedException {
    /* runsix:param */
    String appId = databaseRsCalculateHealthScoreRequestRs.getAppId();
    /* runsix:result */
    AtomicReference<IndicatorRuleEntity> indicatorRuleEntityAR = new AtomicReference<>();
    /* runsix:populate */

    Map<String, CrowdsInstanceEntity> kCrowdsIdVCrowdsInstanceEntityMap = new HashMap<>();
    Set<String> crowdsIdSet = new HashSet<>();

    Map<String, List<RiskModelEntity>> kCrowdsIdVRiskModelEntityListMap = new HashMap<>();
    Set<String> riskModelIdSet = new HashSet<>();

    Map<String, IndicatorInstanceEntity> kIndicatorInstanceIdVIndicatorInstanceEntityMap = new HashMap<>();
    Set<String> indicatorInstanceIdSet = new HashSet<>();

    Set<String> allReasonIdSet = new HashSet<>();
    Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleEntityMap = new HashMap<>();
    Map<String, List<IndicatorExpressionEntity>> kReasonIdVIndicatorExpressionEntityListMap = new HashMap<>();
    Map<String, List<IndicatorExpressionItemEntity>> kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap = new HashMap<>();
    Map<String, IndicatorExpressionItemEntity> kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap = new HashMap<>();

    CompletableFuture<Void> cfPopulateKCrowdsIdVCrowdsInstanceEntityMap = CompletableFuture.runAsync(() -> {
      rsCrowdsBiz.populateEnableKCrowdsIdVCrowdsInstanceEntityMap(kCrowdsIdVCrowdsInstanceEntityMap, appId);
    });
    cfPopulateKCrowdsIdVCrowdsInstanceEntityMap.get();
    if (kCrowdsIdVCrowdsInstanceEntityMap.isEmpty()) {return;}
    crowdsIdSet.addAll(kCrowdsIdVCrowdsInstanceEntityMap.keySet());


    CompletableFuture<Void> cfPopulateKCrowdsIdVRiskModelEntityListMap = CompletableFuture.runAsync(() -> {
      rsCrowdsBiz.populateEnableKCrowdsIdVRiskModelEntityListMap(kCrowdsIdVRiskModelEntityListMap, crowdsIdSet);
    });
    cfPopulateKCrowdsIdVRiskModelEntityListMap.get();
    if (kCrowdsIdVRiskModelEntityListMap.isEmpty()) {return;}
    kCrowdsIdVRiskModelEntityListMap.forEach((crowdsId, riskModelEntityList) -> {
      riskModelIdSet.addAll(riskModelEntityList.stream().map(RiskModelEntity::getRiskModelId).collect(Collectors.toSet()));
    });


    CompletableFuture<Void> cfPopulateKIndicatorInstanceIdVIndicatorInstanceEntityMap = CompletableFuture.runAsync(() -> {
      rsIndicatorInstanceBiz.populateKIndicatorInstanceIdVIndicatorInstanceEntityMap(kIndicatorInstanceIdVIndicatorInstanceEntityMap, appId);
    });
    cfPopulateKIndicatorInstanceIdVIndicatorInstanceEntityMap.get();
    if (kIndicatorInstanceIdVIndicatorInstanceEntityMap.isEmpty()) {return;}
    indicatorInstanceIdSet.addAll(kIndicatorInstanceIdVIndicatorInstanceEntityMap.keySet());


    allReasonIdSet.addAll(crowdsIdSet);
    allReasonIdSet.addAll(riskModelIdSet);
    allReasonIdSet.addAll(indicatorInstanceIdSet);


    CompletableFuture<Void> cfPopulateKIndicatorInstanceIdVIndicatorRuleEntityMap = CompletableFuture.runAsync(() -> {
      rsIndicatorInstanceBiz.populateKIndicatorInstanceIdVIndicatorRuleEntityMap(kIndicatorInstanceIdVIndicatorRuleEntityMap, indicatorInstanceIdSet);
    });
    cfPopulateKIndicatorInstanceIdVIndicatorRuleEntityMap.get();
    if (kIndicatorInstanceIdVIndicatorRuleEntityMap.isEmpty()) {return;}


    CompletableFuture<Void> cfPopulateParseParam = CompletableFuture.runAsync(() -> {
      try {
        rsIndicatorExpressionBiz.populateParseParam(
            allReasonIdSet,
            kReasonIdVIndicatorExpressionEntityListMap,
            kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap,
            kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap
        );
      } catch (Exception e) {
        log.error("RsCalculateBiz.databaseRsCalculateHealthScore databaseRsCalculateHealthScoreRequestRs:{}" , databaseRsCalculateHealthScoreRequestRs, e);
        throw new RsCalculateBizException(EnumESC.DATABASE_RS_CALCULATE_HEALTH_SCORE);
      }
    });
    cfPopulateParseParam.get();

    CompletableFuture<Void> cfPopulateHealthPointIndicatorRuleEntity = CompletableFuture.runAsync(() -> {
      rsIndicatorInstanceBiz.populateHealthPointIndicatorRuleEntity(indicatorRuleEntityAR, appId);
    });
    cfPopulateHealthPointIndicatorRuleEntity.get();


    kCrowdsIdVCrowdsInstanceEntityMap.forEach((crowdsId, crowdsInstanceEntity) -> {
      /* runsix:一个人只能适用于一个人群类型 */
      AtomicBoolean hasFindCrowdsAR = new AtomicBoolean(Boolean.FALSE);
      if (hasFindCrowdsAR.get()) {return;}

      AtomicReference<String> crowdsAR = new AtomicReference<>(RsUtilBiz.RESULT_DROP);
      IndicatorExpressionEntity indicatorExpressionEntity = null;
      List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList = new ArrayList<>();
      List<IndicatorExpressionEntity> indicatorExpressionEntityList = kReasonIdVIndicatorExpressionEntityListMap.get(crowdsId);
      /* runsix:人群类型只能产生一个公式 */
      if (Objects.nonNull(indicatorExpressionEntityList) && !indicatorExpressionEntityList.isEmpty()) {
        indicatorExpressionEntity = indicatorExpressionEntityList.get(0);
      }
      if (Objects.nonNull(indicatorExpressionEntity)) {
        String indicatorExpressionId = indicatorExpressionEntity.getIndicatorExpressionId();
        List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList1 = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
        if (Objects.nonNull(indicatorExpressionItemEntityList1) && !indicatorExpressionItemEntityList1.isEmpty()) {
          /* runsix:人群类型公式只能有一个条件 */
          indicatorExpressionItemEntityList.add(indicatorExpressionItemEntityList1.get(0));
        }
      }
      rsIndicatorExpressionBiz.parseIndicatorExpression(
          EnumIndicatorExpressionField.DATABASE.getField(), EnumIndicatorExpressionSource.CROWDS.getSource(), EnumIndicatorExpressionScene.DATABASE_CALCULATE_HEALTH_POINT.getScene(),
          crowdsAR,
          DatabaseCalIndicatorExpressionRequest
              .builder()
              .kIndicatorInstanceIdVIndicatorRuleEntityMap(kIndicatorInstanceIdVIndicatorRuleEntityMap)
              .indicatorExpressionEntity(indicatorExpressionEntity)
              .indicatorExpressionItemEntityList(indicatorExpressionItemEntityList)
              .minIndicatorExpressionItemEntity(null)
              .maxIndicatorExpressionItemEntity(null)
              .build()
      );
      if (StringUtils.equals(RsUtilBiz.RESULT_DROP, crowdsAR.get())) {return;}
      if (!StringUtils.equalsIgnoreCase(Boolean.TRUE.toString(), crowdsAR.get())) {return;}
      hasFindCrowdsAR.set(Boolean.TRUE);
      List<RiskModelEntity> riskModelEntityList = kCrowdsIdVRiskModelEntityListMap.get(crowdsId);
      if (Objects.isNull(riskModelEntityList) || riskModelEntityList.isEmpty()) {return;}
      Map<String, BigDecimal> kRiskModelIdVTotalScoreMap = new HashMap<>();
      Map<String, Integer> kRiskModelIdVRiskDeathProbabilityMap = new HashMap<>();
      riskModelEntityList.forEach(riskModelEntity -> {
        Map<String, BigDecimal> kPrincipalIdVScoreMap = new HashMap<>();
        AtomicReference<BigDecimal> minScoreAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> maxScoreAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
        String riskModelId = riskModelEntity.getRiskModelId();
        AtomicReference<BigDecimal> singleRiskModelAR = new AtomicReference<>(BigDecimal.ZERO);
        List<IndicatorExpressionEntity> indicatorExpressionEntityList0 = kReasonIdVIndicatorExpressionEntityListMap.get(riskModelId);
        /* runsix:危险原因会产生多个公式 */
        if (Objects.isNull(indicatorExpressionEntityList0) || indicatorExpressionEntityList0.isEmpty()) {return;}
        indicatorExpressionEntityList0.forEach(indicatorExpressionEntity2 -> {
          AtomicReference<String> singleExpressionResultRiskModelAR = new AtomicReference<>(RsUtilBiz.RESULT_DROP);
          String indicatorExpressionId = indicatorExpressionEntity2.getIndicatorExpressionId();
          List<IndicatorExpressionItemEntity> indicatorExpressionItemEntityList2 = kIndicatorExpressionIdVIndicatorExpressionItemEntityListMap.get(indicatorExpressionId);
          if (Objects.isNull(indicatorExpressionItemEntityList2) || indicatorExpressionItemEntityList2.isEmpty()) {return;}
          String minIndicatorExpressionItemId = indicatorExpressionEntity2.getMinIndicatorExpressionItemId();
          IndicatorExpressionItemEntity minIndicatorExpressionItemEntity = kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap.get(minIndicatorExpressionItemId);
          String maxIndicatorExpressionItemId = indicatorExpressionEntity2.getMinIndicatorExpressionItemId();
          IndicatorExpressionItemEntity maxIndicatorExpressionItemEntity = kIndicatorExpressionItemIdVIndicatorExpressionItemEntityMap.get(maxIndicatorExpressionItemId);
          rsIndicatorExpressionBiz.parseIndicatorExpression(
              EnumIndicatorExpressionField.DATABASE.getField(), EnumIndicatorExpressionSource.RISK_MODEL.getSource(), EnumIndicatorExpressionScene.DATABASE_CALCULATE_HEALTH_POINT.getScene(),
              singleExpressionResultRiskModelAR,
              DatabaseCalIndicatorExpressionRequest
                  .builder()
                  .kIndicatorInstanceIdVIndicatorRuleEntityMap(kIndicatorInstanceIdVIndicatorRuleEntityMap)
                  .indicatorExpressionEntity(indicatorExpressionEntity2)
                  .indicatorExpressionItemEntityList(indicatorExpressionItemEntityList2)
                  .minIndicatorExpressionItemEntity(minIndicatorExpressionItemEntity)
                  .maxIndicatorExpressionItemEntity(maxIndicatorExpressionItemEntity)
                  .build()
          );
          if (StringUtils.equals(RsUtilBiz.RESULT_DROP, singleExpressionResultRiskModelAR.get())) {return;}

          BigDecimal curVal = BigDecimal.valueOf(Double.parseDouble(singleExpressionResultRiskModelAR.get()));
          if (curVal.compareTo(minScoreAtomicReference.get()) < 0) {minScoreAtomicReference.set(curVal);}
          if (curVal.compareTo(maxScoreAtomicReference.get()) > 0) {maxScoreAtomicReference.set(curVal);}

          kRiskModelIdVRiskDeathProbabilityMap.put(riskModelId, riskModelEntity.getRiskDeathProbability());
          kPrincipalIdVScoreMap.put(indicatorExpressionEntity2.getPrincipalId(), BigDecimal.valueOf(Double.parseDouble(singleExpressionResultRiskModelAR.get())));
        });
        rsUtilBiz.calculateRiskModelScore(singleRiskModelAR, kPrincipalIdVScoreMap, minScoreAtomicReference, maxScoreAtomicReference);
        kRiskModelIdVTotalScoreMap.put(riskModelId, singleRiskModelAR.get());
      });
      if (kRiskModelIdVRiskDeathProbabilityMap.isEmpty()) {return;}
      Integer totalRiskDeathProbability = kRiskModelIdVRiskDeathProbabilityMap.values().stream().reduce(0, Integer::sum);
      IndicatorRuleEntity indicatorRuleEntity = indicatorRuleEntityAR.get();
      BigDecimal newHealthPoint = rsUtilBiz.newCalculateFinalHealthScore(kRiskModelIdVTotalScoreMap, kRiskModelIdVRiskDeathProbabilityMap, totalRiskDeathProbability);
      indicatorRuleEntity.setDef(newHealthPoint.setScale(2, RoundingMode.DOWN).toString());
    });

    /* runsix:final operation */
    if (Objects.nonNull(indicatorRuleEntityAR.get())) {indicatorRuleService.saveOrUpdate(indicatorRuleEntityAR.get());}
  }
}
