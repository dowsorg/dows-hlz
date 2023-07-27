package org.dows.hep.biz.base.indicator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.ExperimentPhysicalExamCheckRequestRs;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.base.indicator.response.ExperimentPhysicalExamReportResponseRs;
import org.dows.hep.api.enums.*;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.biz.request.CaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.ExperimentCalIndicatorExpressionRequest;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorViewPhysicalExamReportRsBiz {
  private final ExperimentIndicatorViewPhysicalExamReportRsService experimentIndicatorViewPhysicalExamReportRsService;
  private final ExperimentIndicatorViewPhysicalExamRsService experimentIndicatorViewPhysicalExamRsService;
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final IdGenerator idGenerator;
  private final ExperimentIndicatorExpressionRefRsService experimentIndicatorExpressionRefRsService;
  private final ExperimentIndicatorExpressionRsService experimentIndicatorExpressionRsService;
  private final ExperimentIndicatorExpressionItemRsService experimentIndicatorExpressionItemRsService;
  private final ExperimentIndicatorExpressionInfluenceRsService experimentIndicatorExpressionInfluenceRsService;
  private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;
  private final RsExperimentIndicatorExpressionBiz rsExperimentIndicatorExpressionBiz;
  private final RsExperimentIndicatorInstanceBiz rsExperimentIndicatorInstanceBiz;
  private final RsExperimentIndicatorValBiz rsExperimentIndicatorValBiz;
  private final OperateCostBiz operateCostBiz;
  private final ExperimentPersonService experimentPersonService;

  public static ExperimentPhysicalExamReportResponseRs experimentPhysicalExamReport2ResponseRs(ExperimentIndicatorViewPhysicalExamReportRsEntity experimentIndicatorViewPhysicalExamReportRsEntity) {
    if (Objects.isNull(experimentIndicatorViewPhysicalExamReportRsEntity)) {
      return null;
    }
    return ExperimentPhysicalExamReportResponseRs
        .builder()
        .name(experimentIndicatorViewPhysicalExamReportRsEntity.getName())
        .fee(experimentIndicatorViewPhysicalExamReportRsEntity.getFee())
        .currentVal(experimentIndicatorViewPhysicalExamReportRsEntity.getCurrentVal())
        .unit(experimentIndicatorViewPhysicalExamReportRsEntity.getUnit())
        .resultExplain(experimentIndicatorViewPhysicalExamReportRsEntity.getResultExplain())
        .build();
  }

  /**
   * runsix method process
   * 1.change money
   * 2.save reportList
  */
  @Transactional(rollbackFor = Exception.class)
  public void physicalExamCheck(ExperimentPhysicalExamCheckRequestRs experimentPhysicalExamCheckRequestRs, HttpServletRequest request) throws ExecutionException, InterruptedException {
    Integer periods = experimentPhysicalExamCheckRequestRs.getPeriods();
    String appId = experimentPhysicalExamCheckRequestRs.getAppId();
    String experimentId = experimentPhysicalExamCheckRequestRs.getExperimentId();
    String experimentPersonId = experimentPhysicalExamCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentPhysicalExamCheckRequestRs.getIndicatorFuncId();
    String experimentOrgId = experimentPhysicalExamCheckRequestRs.getExperimentOrgId();
    List<String> experimentIndicatorViewPhysicalExamIdList = experimentPhysicalExamCheckRequestRs.getExperimentIndicatorViewPhysicalExamIdList();
    // 获取人物正在进行的流水号
    String operateFlowId = ShareBiz.checkRunningOperateFlowId(experimentPhysicalExamCheckRequestRs.getAppId(),
            experimentPhysicalExamCheckRequestRs.getExperimentId(),
            experimentPhysicalExamCheckRequestRs.getExperimentOrgId(),
            experimentPhysicalExamCheckRequestRs.getExperimentPersonId());

    Set<String> indicatorInstanceIdSet = new HashSet<>();
    List<ExperimentIndicatorViewPhysicalExamRsEntity> experimentIndicatorViewPhysicalExamRsEntityList = new ArrayList<>();
    if (Objects.nonNull(experimentIndicatorViewPhysicalExamIdList) && !experimentIndicatorViewPhysicalExamIdList.isEmpty()) {
      experimentIndicatorViewPhysicalExamRsService.lambdaQuery()
          .in(ExperimentIndicatorViewPhysicalExamRsEntity::getExperimentIndicatorViewPhysicalExamId, experimentIndicatorViewPhysicalExamIdList)
          .list()
          .forEach(experimentIndicatorViewPhysicalExamRsEntity -> {
            indicatorInstanceIdSet.add(experimentIndicatorViewPhysicalExamRsEntity.getIndicatorInstanceId());
            experimentIndicatorViewPhysicalExamRsEntityList.add(experimentIndicatorViewPhysicalExamRsEntity);
          });
    }
    AtomicReference<BigDecimal> totalFeeAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
    CompletableFuture<Void> cfPopulateTotalFee = CompletableFuture.runAsync(() -> {
      experimentIndicatorViewPhysicalExamRsEntityList.forEach(experimentIndicatorViewPhysicalExamRsEntity -> {
        totalFeeAtomicReference.set(totalFeeAtomicReference.get().subtract(experimentIndicatorViewPhysicalExamRsEntity.getFee()));
      });
    });
    cfPopulateTotalFee.get();

    Map<String, ExperimentIndicatorInstanceRsEntity> kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKIndicatorInstanceIdVExperimentIndicatorInstanceMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorInstanceBiz.populateKIndicatorInstanceIdVExperimentIndicatorInstanceMap(
          kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, experimentPersonId, indicatorInstanceIdSet
      );
    });
    cfPopulateKIndicatorInstanceIdVExperimentIndicatorInstanceMap.get();

    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
    if (!indicatorInstanceIdSet.isEmpty()) {
      indicatorInstanceIdSet.forEach(indicatorInstanceId -> {
        ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(indicatorInstanceId);
        if (Objects.nonNull(experimentIndicatorInstanceRsEntity) && StringUtils.isNotBlank(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId())) {
          experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
        }
      });
    }

    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateOnePersonKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorValBiz.populateOnePersonKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, experimentPersonId, periods
      );
    });
    cfPopulateOnePersonKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get();

    Map<String, ExperimentIndicatorExpressionRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap(
          kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap, experimentIndicatorInstanceIdSet
      );
    });
    cfPopulateKExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap.get();

    Set<String> experimentIndicatorExpressionIdSet = kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap.values()
        .stream().map(ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId).collect(Collectors.toSet());
    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
    CompletableFuture<Void> cfPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap(
          kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap, experimentIndicatorExpressionIdSet
      );
    });
    cfPopulateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap.get();

    Set<String> minAndMaxExperimentIndicatorExpressionItemIdSet = new HashSet<>();
    Map<String, ExperimentIndicatorExpressionItemRsEntity> kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = new HashMap<>();
    kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap.values().forEach(experimentIndicatorExpressionRsEntity -> {
      String minIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
      String maxIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
      if (StringUtils.isNotBlank(minIndicatorExpressionItemId)) {
        minAndMaxExperimentIndicatorExpressionItemIdSet.add(minIndicatorExpressionItemId);
      }
      if (StringUtils.isNotBlank(maxIndicatorExpressionItemId)) {
        minAndMaxExperimentIndicatorExpressionItemIdSet.add(maxIndicatorExpressionItemId);
      }
    });
    CompletableFuture<Void> cfPopulateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap(
          kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap, minAndMaxExperimentIndicatorExpressionItemIdSet
      );
    });
    cfPopulateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get();


    List<ExperimentIndicatorViewPhysicalExamReportRsEntity> experimentIndicatorViewPhysicalExamReportRsEntityList = new ArrayList<>();
    CompletableFuture<Void> cfPopulateExperimentIndicatorViewPhysicalExamReportRsEntityList = CompletableFuture.runAsync(() -> {
      experimentIndicatorViewPhysicalExamRsEntityList.forEach(experimentIndicatorViewPhysicalExamRsEntity -> {
        String currentVal = "";
        String unit = null;
        AtomicReference<String> resultExplainAtomicReference = new AtomicReference<>("");
        String indicatorInstanceId = experimentIndicatorViewPhysicalExamRsEntity.getIndicatorInstanceId();
        ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(indicatorInstanceId);
        if (Objects.nonNull(experimentIndicatorInstanceRsEntity)) {
          unit = experimentIndicatorInstanceRsEntity.getUnit();
          String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
          ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
          if (Objects.nonNull(experimentIndicatorValRsEntity)) {
            currentVal = experimentIndicatorValRsEntity.getCurrentVal();
            ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap.get(experimentIndicatorInstanceId);
            if (Objects.nonNull(experimentIndicatorExpressionRsEntity)) {
              String experimentIndicatorExpressionId = experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId();
              List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
              ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity = null;
              ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity = null;
              String minIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMinIndicatorExpressionItemId();
              String maxIndicatorExpressionItemId = experimentIndicatorExpressionRsEntity.getMaxIndicatorExpressionItemId();
              if (StringUtils.isNotBlank(minIndicatorExpressionItemId) && Objects.nonNull(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(minIndicatorExpressionItemId))) {
                minExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(minIndicatorExpressionItemId);
              }
              if (StringUtils.isNotBlank(maxIndicatorExpressionItemId) && Objects.nonNull(kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(maxIndicatorExpressionItemId))) {
                maxExperimentIndicatorExpressionItemRsEntity = kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap.get(maxIndicatorExpressionItemId);
              }

              if (Objects.nonNull(experimentIndicatorExpressionItemRsEntityList)) {
                rsExperimentIndicatorExpressionBiz.parseExperimentIndicatorExpression(
                    EnumIndicatorExpressionField.EXPERIMENT.getField(),
                    EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource(),
                    EnumIndicatorExpressionScene.PHYSICAL_EXAM.getScene(),
                    resultExplainAtomicReference,
                    new HashMap<>(),
                    DatabaseCalIndicatorExpressionRequest.builder().build(),
                    CaseCalIndicatorExpressionRequest.builder().build(),
                    ExperimentCalIndicatorExpressionRequest
                        .builder()
                        .kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
                        .experimentIndicatorExpressionRsEntity(experimentIndicatorExpressionRsEntity)
                        .experimentIndicatorExpressionItemRsEntityList(experimentIndicatorExpressionItemRsEntityList)
                        .minExperimentIndicatorExpressionItemRsEntity(minExperimentIndicatorExpressionItemRsEntity)
                        .maxExperimentIndicatorExpressionItemRsEntity(maxExperimentIndicatorExpressionItemRsEntity)
                        .build()
                );
              }
            }
          }
        }
        ExperimentIndicatorViewPhysicalExamReportRsEntity experimentIndicatorViewPhysicalExamReportRsEntity = ExperimentIndicatorViewPhysicalExamReportRsEntity
            .builder()
            .experimentIndicatorViewPhysicalExamReportId(idGenerator.nextIdStr())
            .experimentId(experimentId)
            .appId(appId)
            .period(periods)
            .indicatorFuncId(indicatorFuncId)
            .experimentPersonId(experimentPersonId)
            .operateFlowId(operateFlowId)
            .name(experimentIndicatorViewPhysicalExamRsEntity.getName())
            .fee(experimentIndicatorViewPhysicalExamRsEntity.getFee())
            .currentVal(currentVal)
            .unit(unit)
            .resultExplain(resultExplainAtomicReference.get())
            .build();
        experimentIndicatorViewPhysicalExamReportRsEntityList.add(experimentIndicatorViewPhysicalExamReportRsEntity);
      });
    });
    cfPopulateExperimentIndicatorViewPhysicalExamReportRsEntityList.get();

    experimentIndicatorInstanceRsBiz.changeMoney(RsChangeMoneyRequest
        .builder()
        .appId(appId)
        .experimentId(experimentId)
        .experimentPersonId(experimentPersonId)
        .periods(periods)
        .moneyChange(totalFeeAtomicReference.get())
        .build());

    // 保存消费记录
    LoginContextVO voLogin= ShareBiz.getLoginUser(request);
    // 获取小组信息
    ExperimentPersonEntity personEntity = experimentPersonService.lambdaQuery()
                           .eq(ExperimentPersonEntity::getExperimentPersonId,experimentPhysicalExamCheckRequestRs.getExperimentPersonId())
                           .eq(ExperimentPersonEntity::getDeleted,false)
                           .one();
    CostRequest costRequest = CostRequest.builder()
            .operateCostId(idGenerator.nextIdStr())
            .experimentInstanceId(experimentPhysicalExamCheckRequestRs.getExperimentId())
            .experimentGroupId(personEntity.getExperimentGroupId())
            .operatorId(voLogin.getAccountId())
            .experimentOrgId(experimentPhysicalExamCheckRequestRs.getExperimentOrgId())
            .operateFlowId(operateFlowId)
            .patientId(experimentPhysicalExamCheckRequestRs.getExperimentPersonId())
            .feeName(EnumOrgFeeType.TGJCF.getName())
            .feeCode(EnumOrgFeeType.TGJCF.getCode())
            .cost(totalFeeAtomicReference.get())
            .period(experimentPhysicalExamCheckRequestRs.getPeriods())
            .build();
    operateCostBiz.saveCost(costRequest);
    experimentIndicatorViewPhysicalExamReportRsService.saveOrUpdateBatch(experimentIndicatorViewPhysicalExamReportRsEntityList);
  }

  public List<ExperimentPhysicalExamReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId, String experimentOrgId, Integer periods) {
    String operateFlowId = ShareBiz.checkRunningOperateFlowId(appId, experimentId, experimentOrgId, experimentPersonId);
    return experimentIndicatorViewPhysicalExamReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getPeriod, periods)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getExperimentPersonId, experimentPersonId)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getOperateFlowId, operateFlowId)
        .orderByDesc(ExperimentIndicatorViewPhysicalExamReportRsEntity::getDt)
        .list()
        .stream()
        .map(ExperimentIndicatorViewPhysicalExamReportRsBiz::experimentPhysicalExamReport2ResponseRs)
        .collect(Collectors.toList());
  }
}
