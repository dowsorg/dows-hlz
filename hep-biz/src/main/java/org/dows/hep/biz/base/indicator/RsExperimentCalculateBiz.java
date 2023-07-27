package org.dows.hep.biz.base.indicator;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.RsCalculateBizException;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.request.ExperimentPersonRequest;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.request.CaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.ExperimentCalIndicatorExpressionRequest;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.dows.hep.biz.user.person.PersonStatiscBiz;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsExperimentCalculateBiz {
  @Value("${redisson.lock.lease-time.experiment.recalculate-periods:0}")
  private Integer leaseTimeExperimentRecalculatePeriods;
  private final String EXPERIMENT_ID_PLUS_PERIODS = "experiment-id-periods";
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final ExperimentPersonService experimentPersonService;
  private final RsExperimentCrowdsBiz rsExperimentCrowdsBiz;
  private final RsExperimentIndicatorExpressionBiz rsExperimentIndicatorExpressionBiz;
  private final RsExperimentIndicatorInstanceBiz rsExperimentIndicatorInstanceBiz;
  private final RsExperimentPersonBiz rsExperimentPersonBiz;
  private final RsUtilBiz rsUtilBiz;
  private final ExperimentSettingService experimentSettingService;
  private final RsExperimentIndicatorValBiz rsExperimentIndicatorValBiz;
  private final ExperimentPersonCalculateTimeRsService experimentPersonCalculateTimeRsService;
  private final IdGenerator idGenerator;
  private final ExperimentScoringBiz experimentScoringBiz;
  private final ExperimentScoringService experimentScoringService;
  private final RedissonClient redissonClient;
  private final PersonStatiscBiz personStatiscBiz;

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

    experimentIndicatorInstanceRsService.saveOrUpdateBatch(resultExperimentIndicatorInstanceRsEntityList);
    experimentIndicatorValRsService.saveOrUpdateBatch(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.values());
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
    Set<String> durationExperimentIndicatorInstanceIdSet = new HashSet<>(kExperimentPersonIdVExperimentIndicatorInstanceIdMap.values());
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
      nextExperimentIndicatorValRsEntity.setExperimentIndicatorValId(curExperimentIndicatorValRsEntity.getCurrentVal());
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
  public void experimentReCalculateFunc(RsExperimentCalculateFuncRequest rsExperimentCalculateFuncRequest) throws ExecutionException, InterruptedException {
    /* runsix:param */
    String appId = rsExperimentCalculateFuncRequest.getAppId();
    String experimentId = rsExperimentCalculateFuncRequest.getExperimentId();
    Integer periods = rsExperimentCalculateFuncRequest.getPeriods();
    String experimentPersonId = rsExperimentCalculateFuncRequest.getExperimentPersonId();
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
    ExperimentScoringEntity experimentScoringEntity = experimentScoringService.lambdaQuery()
        .eq(ExperimentScoringEntity::getExperimentInstanceId, experimentId)
        .eq(ExperimentScoringEntity::getPeriods, periods)
        .one();
    /* runsix:说明已经这个实验这一期已经期数翻转计算过啦 */
    if (Objects.nonNull(experimentScoringEntity)) {return;}

    /* runsix:如果没有计算，那就谁先拿到谁算 */
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.EXPERIMENT_RECALCULATE_PERIODS, EXPERIMENT_ID_PLUS_PERIODS, experimentId+"-"+periods));
    boolean isLocked = lock.tryLock(leaseTimeExperimentRecalculatePeriods, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      return;
    }
    try {
      ExperimentScoringEntity insideExperimentScoringEntity = experimentScoringService.lambdaQuery()
          .eq(ExperimentScoringEntity::getExperimentInstanceId, experimentId)
          .eq(ExperimentScoringEntity::getPeriods, periods)
          .one();
      /* runsix:说明已经这个实验这一期已经期数翻转计算过啦 */
      if (Objects.nonNull(insideExperimentScoringEntity)) {return;}

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

      ExperimentPersonRequest experimentPersonRequest = ExperimentPersonRequest.builder()
          .experimentInstanceId(experimentId)
          .appId(appId)
          .periods(periods)// 计算上一期
          .build();
      // 一期结束保险返还
      personStatiscBiz.refundFunds(experimentPersonRequest);

      /* runsix:最后一步是更新所有人下一期的指标 */
      this.experimentSetVal(RsExperimentSetValRequest
          .builder()
          .appId(appId)
          .experimentId(experimentId)
          .periods(periods)
          .build());
    } finally {
      lock.unlock();
    }
  }
}
