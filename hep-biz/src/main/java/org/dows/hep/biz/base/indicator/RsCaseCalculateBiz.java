package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.CaseRsCalculateHealthScoreRequestRs;
import org.dows.hep.api.base.indicator.request.ReCalculateOnePersonRequestRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionField;
import org.dows.hep.api.enums.EnumIndicatorExpressionScene;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.exception.RsCalculateBizException;
import org.dows.hep.biz.request.CaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.entity.*;
import org.dows.hep.service.CaseIndicatorRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsCaseCalculateBiz {
  private final RsCaseIndicatorInstanceBiz rsCaseIndicatorInstanceBiz;
  private final RsCaseIndicatorExpressionBiz rsCaseIndicatorExpressionBiz;
  private final RsCrowdsBiz rsCrowdsBiz;
  private final RsUtilBiz rsUtilBiz;
  private final RsIndicatorExpressionBiz rsIndicatorExpressionBiz;
  private final CaseIndicatorRuleService caseIndicatorRuleService;
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
          String maxIndicatorExpressionItemId = indicatorExpressionEntity2.getMaxIndicatorExpressionItemId();
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
      AtomicReference<BigDecimal> newHealthPointAR = new AtomicReference<>(newHealthPoint);
      rsUtilBiz.healthPointMinAndMax(newHealthPointAR);
      caseIndicatorRuleEntity.setDef(newHealthPointAR.get().setScale(2, RoundingMode.DOWN).toString());
      caseIndicatorRuleEntityAR.set(caseIndicatorRuleEntity);
    });
    if (Objects.nonNull(caseIndicatorRuleEntityAR.get())) {caseIndicatorRuleService.saveOrUpdate(caseIndicatorRuleEntityAR.get());}
  }
}
