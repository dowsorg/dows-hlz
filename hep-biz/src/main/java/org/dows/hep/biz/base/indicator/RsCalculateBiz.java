package org.dows.hep.biz.base.indicator;

import cn.hutool.json.JSONUtil;
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
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.request.CaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.ExperimentCalIndicatorExpressionRequest;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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
  private final ExperimentSettingService experimentSettingService;
  private final RsExperimentIndicatorValBiz rsExperimentIndicatorValBiz;
  private final ExperimentPersonCalculateTimeRsService experimentPersonCalculateTimeRsService;
  private final IdGenerator idGenerator;
  private final ExperimentScoringBiz experimentScoringBiz;

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

    Map<String, Set<String>> kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKCaseIndicatorInstanceIdInfluencedIndicatorInstanceIdSetMap = CompletableFuture.runAsync(() -> {
      rsCaseIndicatorExpressionBiz.populateKCaseIndicatorInstanceIdInfluencedIndicatorInstanceIdSetMap(kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap, caseIndicatorInstanceIdSet);
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

    List<String> seqCaseIndicatorInstanceIdList = new ArrayList<>();
    rsUtilBiz.algorithmKahn(seqCaseIndicatorInstanceIdList, kCaseIndicatorInstanceIdVCaseInfluencedIndicatorInstanceIdSetMap);
    seqCaseIndicatorInstanceIdList.forEach(caseIndicatorInstanceId -> {
      CaseIndicatorRuleEntity caseIndicatorRuleEntity = kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.get(caseIndicatorInstanceId);
      String def = caseIndicatorRuleEntity.getDef();
      AtomicReference<String> resultAtomicReference = new AtomicReference<>(def);
      CaseIndicatorExpressionEntity caseIndicatorExpressionEntity = kCaseIndicatorInstanceIdVCaseIndicatorExpressionEntityMap.get(caseIndicatorInstanceId);
      List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = kCaseIndicatorInstanceIdVCaseIndicatorExpressionItemEntityListMap.get(caseIndicatorInstanceId);
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
      kCaseIndicatorInstanceIdVCaseIndicatorRuleEntityMap.put(caseIndicatorInstanceId, caseIndicatorRuleEntity);
    });

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

  private BigDecimal rsCalculateCompetitiveScore(BigDecimal currentHealthScore, BigDecimal defHealthScore, BigDecimal minHealthScore, BigDecimal maxHealthScore) {
    BigDecimal resultBigDecimal = null;
    if (currentHealthScore.compareTo(defHealthScore) >= 0) {
      if (maxHealthScore.compareTo(defHealthScore) == 0) {
        resultBigDecimal = currentHealthScore;
      } else {
        resultBigDecimal = (currentHealthScore.subtract(defHealthScore).divide(maxHealthScore.subtract(defHealthScore), 2, RoundingMode.DOWN).multiply(BigDecimal.valueOf(100-60))).add(BigDecimal.valueOf(60));
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

  @Transactional(rollbackFor = Exception.class)
  public void experimentRsCalculateHealthScore(ExperimentRsCalculateHealthScoreRequestRs experimentRsCalculateHealthScoreRequestRs) throws ExecutionException, InterruptedException {
    String appId = experimentRsCalculateHealthScoreRequestRs.getAppId();
    Integer periods = experimentRsCalculateHealthScoreRequestRs.getPeriods();
    String experimentId = experimentRsCalculateHealthScoreRequestRs.getExperimentId();
    Set<String> experimentPersonIdSet = experimentRsCalculateHealthScoreRequestRs.getExperimentPersonIdSet();

    Map<String, Map<String, String>> kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
    if (Objects.isNull(experimentPersonIdSet) || experimentPersonIdSet.isEmpty()) {return;}
    CompletableFuture<Void> cfPopulateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorInstanceBiz.populateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap(kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap, experimentPersonIdSet);
    });
    cfPopulateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get();

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

    Map<String, List<ExperimentIndicatorExpressionRefRsEntity>> kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap  = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorExpressionBiz.populateKExperimentReasonIdVExperimentIndicatorExpressionRefListMap(kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap, experimentCrowdsIdSet);
    });
    cfPopulateKExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap.get();

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

    Map<String, List<ExperimentRiskModelRsEntity>> kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentCrowdsIdVExperimentRiskModelRsEntityListMap = CompletableFuture.runAsync(() -> {
      rsExperimentCrowdsBiz.populateKExperimentCrowdsIdVExperimentRiskModelRsEntityListMap(
          kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap, experimentCrowdsIdSet);
    });
    cfPopulateKExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.get();

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
      AtomicReference<String> crowdsAR = new AtomicReference<>(RsUtilBiz.RESULT_DROP);
      Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(experimentPersonId);
      if (Objects.isNull(kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap) || kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.isEmpty()) {return;}
      /* runsix:这个人这期所有指标值 */
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = kExperimentPersonIdVKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentPersonId);
      if (Objects.isNull(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)) {return;}

      experimentCrowdsInstanceRsEntityList.forEach(experimentCrowdsInstanceRsEntity -> {
        AtomicBoolean hasFindCrowdsAR = new AtomicBoolean(Boolean.FALSE);
        if (hasFindCrowdsAR.get()) {return;}

        String experimentCrowdsId = experimentCrowdsInstanceRsEntity.getExperimentCrowdsId();
        List<ExperimentIndicatorExpressionRefRsEntity> crowdsExperimentIndicatorExpressionRefRsEntityList = kExperimentCrowdsIdVExperimentIndicatorExpressionRefListMap.get(experimentCrowdsId);
        if (Objects.isNull(crowdsExperimentIndicatorExpressionRefRsEntityList) || crowdsExperimentIndicatorExpressionRefRsEntityList.isEmpty()) {return;}

        /* runsix:因为人群类型只能产生一个公式 */
        ExperimentIndicatorExpressionRefRsEntity crowdsExperimentIndicatorExpressionRefRsEntity = crowdsExperimentIndicatorExpressionRefRsEntityList.get(0);
        String crowdsIndicatorExpressionId = crowdsExperimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId();
        List<ExperimentIndicatorExpressionItemRsEntity> crowdsExperimentIndicatorExpressionItemRsEntityList = kCrowdsExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(crowdsIndicatorExpressionId);
        if (Objects.isNull(crowdsExperimentIndicatorExpressionItemRsEntityList) || crowdsExperimentIndicatorExpressionItemRsEntityList.isEmpty()) {return;}
        rsExperimentIndicatorExpressionBiz.parseExperimentIndicatorExpression(
            EnumIndicatorExpressionField.EXPERIMENT.getField(),
            EnumIndicatorExpressionSource.CROWDS.getSource(),
            EnumIndicatorExpressionScene.EXPERIMENT_CALCULATE_HEALTH_POINT.getScene(),
            crowdsAR,
            kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
            DatabaseCalIndicatorExpressionRequest.builder().build(),
            CaseCalIndicatorExpressionRequest.builder().build(),
            ExperimentCalIndicatorExpressionRequest
                .builder()
                .kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
                .experimentIndicatorExpressionItemRsEntityList(crowdsExperimentIndicatorExpressionItemRsEntityList)
                .build()
        );
        if (StringUtils.equals(RsUtilBiz.RESULT_DROP, crowdsAR.get())) {return;}
        if (!StringUtils.equalsIgnoreCase(Boolean.TRUE.toString(), crowdsAR.get())) {return;}
        hasFindCrowdsAR.set(Boolean.TRUE);
        List<ExperimentRiskModelRsEntity> experimentRiskModelRsEntityList = kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.get(experimentCrowdsId);
        if (Objects.isNull(experimentRiskModelRsEntityList) || experimentRiskModelRsEntityList.isEmpty()) {return;}
        Map<String, BigDecimal> kRiskModelIdVTotalScoreMap = new HashMap<>();
        Map<String, Integer> kRiskModelIdVRiskDeathProbabilityMap = new HashMap<>();
        experimentRiskModelRsEntityList.forEach(experimentRiskModelRsEntity -> {
          Map<String, BigDecimal> kPrincipalIdVScoreMap = new HashMap<>();
          AtomicReference<BigDecimal> minScoreAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
          AtomicReference<BigDecimal> maxScoreAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
          String experimentRiskModelId = experimentRiskModelRsEntity.getExperimentRiskModelId();
          AtomicReference<BigDecimal> singleRiskModelAR = new AtomicReference<>(BigDecimal.ZERO);
          List<ExperimentIndicatorExpressionRefRsEntity> riskModelExperimentIndicatorExpressionRefRsEntityList = kExperimentRiskModelIdVExperimentIndicatorExpressionRefListMap.get(experimentRiskModelId);
          if (Objects.isNull(riskModelExperimentIndicatorExpressionRefRsEntityList) || riskModelExperimentIndicatorExpressionRefRsEntityList.isEmpty()) {return;}
          riskModelExperimentIndicatorExpressionRefRsEntityList.forEach(riskModelExperimentIndicatorExpressionRefRsEntity -> {
            String riskModelIndicatorExpressionId = riskModelExperimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId();
            ExperimentIndicatorExpressionRsEntity riskModelExperimentIndicatorExpressionRsEntity = kRiskModelExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.get(riskModelIndicatorExpressionId);
            if (Objects.isNull(riskModelExperimentIndicatorExpressionRsEntity)) {return;}
            AtomicReference<String> singleExpressionResultRiskModelAR = new AtomicReference<>(RsUtilBiz.RESULT_DROP);
            String experimentIndicatorExpressionId = riskModelExperimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId();
            List<ExperimentIndicatorExpressionItemRsEntity> riskModelExperimentIndicatorExpressionItemRsEntityList = kRiskModelExpIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
            if (Objects.isNull(riskModelExperimentIndicatorExpressionItemRsEntityList) || riskModelExperimentIndicatorExpressionItemRsEntityList.isEmpty()) {return;}
            String minIndicatorExpressionItemId = riskModelExperimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
            ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(minIndicatorExpressionItemId);
            String maxIndicatorExpressionItemId = riskModelExperimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
            ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(maxIndicatorExpressionItemId);
            rsExperimentIndicatorExpressionBiz.parseExperimentIndicatorExpression(
                EnumIndicatorExpressionField.EXPERIMENT.getField(), EnumIndicatorExpressionSource.RISK_MODEL.getSource(), EnumIndicatorExpressionScene.EXPERIMENT_CALCULATE_HEALTH_POINT.getScene(),
                singleExpressionResultRiskModelAR,
                kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
                DatabaseCalIndicatorExpressionRequest.builder().build(),
                CaseCalIndicatorExpressionRequest.builder().build(),
                ExperimentCalIndicatorExpressionRequest
                    .builder()
                    .kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
                    .experimentIndicatorExpressionRsEntity(riskModelExperimentIndicatorExpressionRsEntity)
                    .experimentIndicatorExpressionItemRsEntityList(riskModelExperimentIndicatorExpressionItemRsEntityList)
                    .minExperimentIndicatorExpressionItemRsEntity(minExperimentIndicatorExpressionItemRsEntity)
                    .maxExperimentIndicatorExpressionItemRsEntity(maxExperimentIndicatorExpressionItemRsEntity)
                    .build()
            );
            if (StringUtils.equals(RsUtilBiz.RESULT_DROP, singleExpressionResultRiskModelAR.get())) {return;}

            BigDecimal curVal = BigDecimal.valueOf(Double.parseDouble(singleExpressionResultRiskModelAR.get()));
            if (curVal.compareTo(minScoreAtomicReference.get()) < 0) {minScoreAtomicReference.set(curVal);}
            if (curVal.compareTo(maxScoreAtomicReference.get()) > 0) {maxScoreAtomicReference.set(curVal);}

            kRiskModelIdVRiskDeathProbabilityMap.put(experimentRiskModelId, experimentRiskModelRsEntity.getRiskDeathProbability());
            kPrincipalIdVScoreMap.put(riskModelExperimentIndicatorExpressionRsEntity.getPrincipalId(), BigDecimal.valueOf(Double.parseDouble(singleExpressionResultRiskModelAR.get())));
          });
          rsUtilBiz.calculateRiskModelScore(singleRiskModelAR, kPrincipalIdVScoreMap, minScoreAtomicReference, maxScoreAtomicReference);
          kRiskModelIdVTotalScoreMap.put(experimentRiskModelId, singleRiskModelAR.get());
        });
        if (kRiskModelIdVRiskDeathProbabilityMap.isEmpty()) {return;}
        Integer totalRiskDeathProbability = kRiskModelIdVRiskDeathProbabilityMap.values().stream().reduce(0, Integer::sum);
        BigDecimal newHealthPoint = rsUtilBiz.newCalculateFinalHealthScore(kRiskModelIdVTotalScoreMap, kRiskModelIdVRiskDeathProbabilityMap, totalRiskDeathProbability);
        healthExperimentIndicatorValRsEntity.setCurrentVal(newHealthPoint.toString());
        healthExperimentIndicatorValRsEntityList.add(healthExperimentIndicatorValRsEntity);
      });
    });
    if (!healthExperimentIndicatorValRsEntityList.isEmpty()) {experimentIndicatorValRsService.saveOrUpdateBatch(healthExperimentIndicatorValRsEntityList);}
  }
  @Transactional(rollbackFor = Exception.class)
  public void experimentReCalculatePerson(RsCalculatePersonRequestRs rsCalculatePersonRequestRs) throws ExecutionException, InterruptedException {
    String appId = rsCalculatePersonRequestRs.getAppId();
    String experimentId = rsCalculatePersonRequestRs.getExperimentId();
    Integer periods = rsCalculatePersonRequestRs.getPeriods();
    Set<String> personIdSet = rsCalculatePersonRequestRs.getPersonIdSet();

    Set<String> reasonIdSet = new HashSet<>();
    Map<String, List<ExperimentIndicatorExpressionRsEntity>> kReasonIdVExperimentIndicatorExpressionRsEntityListMap = new HashMap<>();
    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
    Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = new HashMap<>();

    Set<String> experimentPersonIdSet = new HashSet<>();
    CompletableFuture<Void> cfPopulateExperimentPersonIdSet = CompletableFuture.runAsync(() -> {
      rsExperimentPersonBiz.populateExperimentPersonIdSet(experimentPersonIdSet, experimentId, personIdSet);
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
    List<ExperimentIndicatorInstanceRsEntity> resultExperimentIndicatorInstanceRsEntityList = new ArrayList<>();
    kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap.forEach((experimentPersonId, experimentIndicatorInstanceRsEntityList) -> {
      resultExperimentIndicatorInstanceRsEntityList.addAll(experimentIndicatorInstanceRsEntityList);
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
        log.error("RsCalculateBiz.reCalculateAllPerson.cfPopulateParseParam rsCalculateAllPersonRequestRs:{}" , rsCalculatePersonRequestRs, e);
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
        log.error("RsCalculateBiz.reCalculateAllPerson.cfReCalculateAllExperimentIndicatorInstance rsCalculateAllPersonRequestRs:{}" , rsCalculatePersonRequestRs, e);
        throw new RsCalculateBizException(EnumESC.RS_CALCULATE_ERROR);
      }
    });
    cfReCalculateAllExperimentIndicatorInstance.get();

    /* runsix:需要把指标变化的那个字段置为0 */
    resultExperimentIndicatorInstanceRsEntityList.forEach(experimentIndicatorInstanceRsEntity -> {
      experimentIndicatorInstanceRsEntity.setChangeVal(0D);
    });

    CompletableFuture<Void> cfFinalOperation = CompletableFuture.runAsync(() -> {
      experimentIndicatorInstanceRsService.saveOrUpdateBatch(resultExperimentIndicatorInstanceRsEntityList);
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
      caseIndicatorRuleEntityAR.set(caseIndicatorRuleEntity);
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
      indicatorRuleEntityAR.set(indicatorRuleEntity);
    });

    /* runsix:final operation */
    if (Objects.nonNull(indicatorRuleEntityAR.get())) {indicatorRuleService.saveOrUpdate(indicatorRuleEntityAR.get());}
  }

  @Transactional(rollbackFor = Exception.class)
  public void experimentSetDuration(RsExperimentSetDurationRequest rsExperimentSetDurationRequest) throws ExecutionException, InterruptedException {
    /* runsix:param */
    String appId = rsExperimentSetDurationRequest.getAppId();
    String experimentId = rsExperimentSetDurationRequest.getExperimentId();
    Integer periods = rsExperimentSetDurationRequest.getPeriods();
    Map<String, Integer> kExperimentPersonIdVDurationMap = rsExperimentSetDurationRequest.getKExperimentPersonIdVDurationMap();
    if (Objects.isNull(kExperimentPersonIdVDurationMap) || kExperimentPersonIdVDurationMap.isEmpty()) {return;}
    Set<String> experimentPersonIdSet = kExperimentPersonIdVDurationMap.keySet();
    /* runsix:result */
    List<ExperimentIndicatorValRsEntity> experimentIndicatorValRsEntityList = new ArrayList<>();
    Map<String, String> kExperimentPersonIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentPersonIdVDurationExperimentIndicatorInstanceIdMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorInstanceBiz.populateKExperimentPersonIdVDurationExperimentIndicatorInstanceIdMap(kExperimentPersonIdVExperimentIndicatorInstanceIdMap, experimentPersonIdSet);
    });
    cfPopulateKExperimentPersonIdVDurationExperimentIndicatorInstanceIdMap.get();

    if (kExperimentPersonIdVExperimentIndicatorInstanceIdMap.isEmpty()) {return;}
    Set<String> durationExperimentIndicatorInstanceIdSet = (Set<String>) kExperimentPersonIdVExperimentIndicatorInstanceIdMap.values();
    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorValBiz.populateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, durationExperimentIndicatorInstanceIdSet, periods);
    });
    cfPopulateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get();

    kExperimentPersonIdVDurationMap.forEach((experimentPersonId, duration) -> {
      String experimentIndicatorInstanceId = kExperimentPersonIdVExperimentIndicatorInstanceIdMap.get(experimentPersonId);
      if (StringUtils.isBlank(experimentIndicatorInstanceId)) {return;}
      ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
      if (Objects.isNull(experimentIndicatorValRsEntity)) {return;}
      experimentIndicatorValRsEntity.setCurrentVal(String.valueOf(Math.max(duration, 1)));
      experimentIndicatorValRsEntityList.add(experimentIndicatorValRsEntity);
    });

    /* runsix:operation */
    experimentIndicatorValRsService.saveOrUpdateBatch(experimentIndicatorValRsEntityList);
  }

  @Transactional(rollbackFor = Exception.class)
  public void experimentSetVal(RsExperimentSetValRequest rsExperimentSetValRequest) throws ExecutionException, InterruptedException {
    /* runsix:param */
    String appId = rsExperimentSetValRequest.getAppId();
    String experimentId = rsExperimentSetValRequest.getExperimentId();
    Integer curPeriods = rsExperimentSetValRequest.getPeriods();
    /* runsix:result */
    List<ExperimentIndicatorValRsEntity> experimentIndicatorValRsEntityList = new ArrayList<>();
    /* runsix:check if last periods */
    ExperimentSettingEntity experimentSettingEntity = experimentSettingService.lambdaQuery()
        .eq(ExperimentSettingEntity::getExperimentInstanceId, experimentId)
        .eq(ExperimentSettingEntity::getConfigKey, ExperimentSetting.SandSetting.class.getName())
        .one();
    /* runsix:如果实验不存在，不做任何操作 */
    if (Objects.isNull(experimentSettingEntity)) {return;}
    ExperimentSetting.SandSetting sandSetting = JSONUtil.toBean(experimentSettingEntity.getConfigJsonVals(), ExperimentSetting.SandSetting.class);
    Integer maxPeriods = sandSetting.getPeriods();
    /* runsix:如果当前期数是最后一期，不需要更新下一期，因为没有下一期 */
    if (curPeriods >= maxPeriods) {return;}
    Integer nextPeriods = curPeriods+1;

    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
    Set<String> experimentPersonIdSet = experimentPersonService.lambdaQuery()
        .eq(ExperimentPersonEntity::getExperimentInstanceId, experimentId)
        .list()
        .stream().map(ExperimentPersonEntity::getExperimentPersonId)
        .collect(Collectors.toSet());
    if (experimentPersonIdSet.isEmpty()) {return;}

    experimentIndicatorInstanceRsService.lambdaQuery()
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentId, experimentId)
        .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonIdSet)
        .list()
        .forEach(experimentIndicatorInstanceRsEntity -> {
          experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
        });

    Map<String, ExperimentIndicatorValRsEntity> curKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    CompletableFuture<Void> curCfPopulateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorValBiz.populateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(
          curKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,experimentIndicatorInstanceIdSet, curPeriods);
    });
    curCfPopulateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get();

    Map<String, ExperimentIndicatorValRsEntity> nextKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    CompletableFuture<Void> nextCfPopulateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorValBiz.populateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(
          nextKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, experimentIndicatorInstanceIdSet, nextPeriods);
    });
    nextCfPopulateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get();

    nextKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.forEach((experimentIndicatorInstanceId, nextExperimentIndicatorValRsEntity) -> {
      ExperimentIndicatorValRsEntity curExperimentIndicatorValRsEntity = curKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
      if (Objects.isNull(curExperimentIndicatorValRsEntity)) {return;}
      nextExperimentIndicatorValRsEntity.setCurrentVal(curExperimentIndicatorValRsEntity.getCurrentVal());
      experimentIndicatorValRsEntityList.add(nextExperimentIndicatorValRsEntity);
    });

    /* runsix:final operation */
    experimentIndicatorValRsService.saveOrUpdateBatch(experimentIndicatorValRsEntityList);
  }

  @Transactional(rollbackFor = Exception.class)
  public void experimentUpdateCalculatorTime(RsCalculateTimeRequest rsCalculateTimeRequest, Map<String, Integer> kExperimentPersonIdVDurationMap) {
    /* runsix:param */
    String appId = rsCalculateTimeRequest.getAppId();
    String experimentId = rsCalculateTimeRequest.getExperimentId();
    Set<String> experimentPersonIdSet = rsCalculateTimeRequest.getExperimentPersonIdSet();
    ExperimentTimePoint timePoint=ExperimentSettingCache.Instance().getTimePointByRealTimeSilence(ExperimentCacheKey.create(appId,experimentId), LocalDateTime.now(), true);
    Integer gameDay = timePoint.getGameDay();
    /* runsix:result */
    List<ExperimentPersonCalculateTimeRsEntity> experimentPersonCalculateTimeRsEntityList = new ArrayList<>();

    Map<String, ExperimentPersonCalculateTimeRsEntity> kExperimentPersonIdVExperimentPersonCalculateTimeRsEntityMap = new HashMap<>();
    if (Objects.isNull(experimentPersonIdSet) || experimentPersonIdSet.isEmpty()) {return;}
    experimentPersonCalculateTimeRsService.lambdaQuery()
        .eq(ExperimentPersonCalculateTimeRsEntity::getExperimentId, experimentId)
        .in(ExperimentPersonCalculateTimeRsEntity::getExperimentPersonId, experimentPersonIdSet)
        .list()
        .forEach(experimentPersonCalculateTimeRsEntity -> {
          kExperimentPersonIdVExperimentPersonCalculateTimeRsEntityMap.put(experimentPersonCalculateTimeRsEntity.getExperimentPersonId(), experimentPersonCalculateTimeRsEntity);
        });
    experimentPersonIdSet.forEach(experimentPersonId -> {
      ExperimentPersonCalculateTimeRsEntity experimentPersonCalculateTimeRsEntity = kExperimentPersonIdVExperimentPersonCalculateTimeRsEntityMap.get(experimentPersonId);
      Integer duration = null;
      /* runsix:如果没有，说明是第一次结算，第一次结算要插入 */
      if (Objects.isNull(experimentPersonCalculateTimeRsEntity)) {
        duration = Math.max(gameDay, 1);
        experimentPersonCalculateTimeRsEntity = ExperimentPersonCalculateTimeRsEntity
            .builder()
            .experimentPersonCalculateTimeId(idGenerator.nextIdStr())
            .experimentId(experimentId)
            .experimentPersonId(experimentPersonId)
            .lastCalDay(gameDay)
            .build();
      } else {
        duration = Math.max(gameDay - experimentPersonCalculateTimeRsEntity.getLastCalDay(), 1);
        experimentPersonCalculateTimeRsEntity.setLastCalDay(gameDay);
      }
      experimentPersonCalculateTimeRsEntityList.add(experimentPersonCalculateTimeRsEntity);
      if (Objects.isNull(kExperimentPersonIdVDurationMap)) {return;}
      kExperimentPersonIdVDurationMap.put(experimentPersonId, duration);
    });

    /* runsix:final operation */
    experimentPersonCalculateTimeRsService.saveOrUpdateBatch(experimentPersonCalculateTimeRsEntityList);
  }

  /**
   * runsix method process
   * 功能结算点需要调用这个，单个人结算
   * 注意参数personIdSet，如果为空表示是此次实验所有人
   * 1.计算此次结算持续天数
   * 2.设置这个人的持续天数
   * 3.重新计算人的指标
   * 4.重新计算健康指数
  */
  @Transactional(rollbackFor = Exception.class)
  public void experimentReCalculateFunc(RsCalculateFuncRequest rsCalculateFuncRequest) throws ExecutionException, InterruptedException {
    /* runsix:param */
    String appId = rsCalculateFuncRequest.getAppId();
    String experimentId = rsCalculateFuncRequest.getExperimentId();
    Integer periods = rsCalculateFuncRequest.getPeriods();
    String experimentPersonId = rsCalculateFuncRequest.getExperimentPersonId();
    Set<String> experimentPersonIdSet = new HashSet<>();
    experimentPersonIdSet.add(experimentPersonId);
    /* runsix:cal param */
    Map<String, Integer> kExperimentPersonIdVDurationMap = new HashMap<>();

    /* runsix:1.计算此次结算持续天数 */
    this.experimentUpdateCalculatorTime(RsCalculateTimeRequest
        .builder()
        .appId(appId)
        .experimentId(experimentId)
        .experimentPersonIdSet(experimentPersonIdSet)
        .build(),
        kExperimentPersonIdVDurationMap);

    /* runsix:2.设置此次结算持续天数 */
    this.experimentSetDuration(RsExperimentSetDurationRequest
        .builder()
        .appId(appId)
        .experimentId(experimentId)
        .periods(periods)
        .kExperimentPersonIdVDurationMap(kExperimentPersonIdVDurationMap)
        .build());

    /* runsix:3.重新计算人的指标 */
    this.experimentReCalculatePerson(RsCalculatePersonRequestRs
        .builder()
        .appId(appId)
        .experimentId(experimentId)
        .periods(periods)
        .personIdSet(experimentPersonIdSet)
        .build());

    /* runsix:4.重新计算健康指数 */
    this.experimentRsCalculateHealthScore(ExperimentRsCalculateHealthScoreRequestRs
        .builder()
        .appId(appId)
        .experimentId(experimentId)
        .periods(periods)
        .experimentPersonIdSet(experimentPersonIdSet)
        .build());
  }

  /**
   * runsix method process
   * 期数翻转，计算所有人
   * 1.算出每个人的持续天数
   * 2.设置每个人的持续天数
   * 3.重新计算所有人的指标
   * 4.重新计算所有人的健康指数
   * 5.存储期数翻转数据
   * 最后一步是更新所有人下一期的指标
  */
  @Transactional(rollbackFor = Exception.class)
  public void experimentReCalculatePeriods(RsCalculatePeriodsRequest rsCalculatePeriodsRequest) throws ExecutionException, InterruptedException {
    /* runsix:param */
    String appId = rsCalculatePeriodsRequest.getAppId();
    String experimentId = rsCalculatePeriodsRequest.getExperimentId();
    Integer periods = rsCalculatePeriodsRequest.getPeriods();

    /* runsix:cal param */
    Map<String, Integer> kExperimentPersonIdVDurationMap = new HashMap<>();

    /* runsix:get all experiment person */
    Set<String> experimentPersonIdSet = new HashSet<>();
    CompletableFuture<Void> cfPopulateExperimentPersonIdSet = CompletableFuture.runAsync(() -> {
      rsExperimentPersonBiz.populateExperimentPersonIdSet(experimentPersonIdSet, experimentId, null);
    });
    cfPopulateExperimentPersonIdSet.get();

    /* runsix:1.算出每个人的持续天数 */
    this.experimentUpdateCalculatorTime(RsCalculateTimeRequest
            .builder()
            .appId(appId)
            .experimentId(experimentId)
            .experimentPersonIdSet(experimentPersonIdSet)
            .build(),
        kExperimentPersonIdVDurationMap);

    /* runsix:2.设置每个人的持续天数 */
    this.experimentSetDuration(RsExperimentSetDurationRequest
        .builder()
        .appId(appId)
        .experimentId(experimentId)
        .periods(periods)
        .kExperimentPersonIdVDurationMap(kExperimentPersonIdVDurationMap)
        .build());

    /* runsix:3.重新计算所有人的指标 */
    this.experimentReCalculatePerson(RsCalculatePersonRequestRs
        .builder()
        .appId(appId)
        .experimentId(experimentId)
        .periods(periods)
        .personIdSet(experimentPersonIdSet)
        .build());

    /* runsix:4.重新计算所有人的健康指数 */
    this.experimentRsCalculateHealthScore(ExperimentRsCalculateHealthScoreRequestRs
        .builder()
        .appId(appId)
        .experimentId(experimentId)
        .periods(periods)
        .experimentPersonIdSet(experimentPersonIdSet)
        .build());

    /* runsix:5.存储期数翻转数据 */
    experimentScoringBiz.saveOrUpd(experimentId, periods);

    /* runsix:最后一步是更新所有人下一期的指标 */
    this.experimentSetVal(RsExperimentSetValRequest
        .builder()
        .appId(appId)
        .experimentId(experimentId)
        .periods(periods)
        .build());
  }
}
