package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.DatabaseRsCalculateHealthScoreRequestRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorExpressionField;
import org.dows.hep.api.enums.EnumIndicatorExpressionScene;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.exception.RsCalculateBizException;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.entity.*;
import org.dows.hep.service.IndicatorRuleService;
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
public class RsDatabaseCalculateBiz {
  private final RsUtilBiz rsUtilBiz;
  private final RsCrowdsBiz rsCrowdsBiz;
  private final IndicatorRuleService indicatorRuleService;
  private final RsIndicatorInstanceBiz rsIndicatorInstanceBiz;
  private final RsIndicatorExpressionBiz rsIndicatorExpressionBiz;
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
}
