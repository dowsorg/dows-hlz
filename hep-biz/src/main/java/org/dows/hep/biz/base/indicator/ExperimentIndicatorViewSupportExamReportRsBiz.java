package org.dows.hep.biz.base.indicator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.base.indicator.request.ExperimentSupportExamCheckRequestRs;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.base.indicator.response.ExperimentSupportExamReportResponseRs;
import org.dows.hep.api.enums.*;
import org.dows.hep.biz.request.CaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.ExperimentCalIndicatorExpressionRequest;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class ExperimentIndicatorViewSupportExamReportRsBiz {
  private final ExperimentIndicatorViewSupportExamReportRsService experimentIndicatorViewSupportExamReportRsService;
  private final ExperimentIndicatorViewSupportExamRsService experimentIndicatorViewSupportExamRsService;
  private final IdGenerator idGenerator;
  private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;
  private final RsExperimentIndicatorValBiz rsExperimentIndicatorValBiz;
  private final RsExperimentIndicatorInstanceBiz rsExperimentIndicatorInstanceBiz;
  private final RsExperimentIndicatorExpressionBiz rsExperimentIndicatorExpressionBiz;
  private final ExperimentPersonService experimentPersonService;
  private final OperateCostBiz operateCostBiz;
  private final OperateInsuranceService operateInsuranceService;
  private final ExperimentOrgService experimentOrgService;
  private final CaseOrgFeeService caseOrgFeeService;

  public static ExperimentSupportExamReportResponseRs experimentSupportExamReport2ResponseRs(ExperimentIndicatorViewSupportExamReportRsEntity experimentIndicatorViewSupportExamReportRsEntity) {
    if (Objects.isNull(experimentIndicatorViewSupportExamReportRsEntity)) {
      return null;
    }
    return ExperimentSupportExamReportResponseRs
        .builder()
        .name(experimentIndicatorViewSupportExamReportRsEntity.getName())
        .fee(experimentIndicatorViewSupportExamReportRsEntity.getFee())
        .currentVal(experimentIndicatorViewSupportExamReportRsEntity.getCurrentVal())
        .unit(experimentIndicatorViewSupportExamReportRsEntity.getUnit())
        .resultExplain(experimentIndicatorViewSupportExamReportRsEntity.getResultExplain())
        .build();
  }

  public List<ExperimentSupportExamReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId, String experimentOrgId, Integer periods) {
    String operateFlowId = ShareBiz.checkRunningOperateFlowId(appId, experimentId, experimentOrgId, experimentPersonId);
    if(ShareUtil.XObject.isEmpty(operateFlowId)){
      return Collections.emptyList();
    }
    return experimentIndicatorViewSupportExamReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getPeriod, periods)
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getExperimentPersonId, experimentPersonId)
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getOperateFlowId, operateFlowId)
        .orderByDesc(ExperimentIndicatorViewSupportExamReportRsEntity::getDt)
        .list()
        .stream()
        .map(ExperimentIndicatorViewSupportExamReportRsBiz::experimentSupportExamReport2ResponseRs)
        .collect(Collectors.toList());
  }

  @Transactional(rollbackFor = Exception.class)
  public void supportExamCheck(ExperimentSupportExamCheckRequestRs experimentSupportExamCheckRequestRs, HttpServletRequest request) throws ExecutionException, InterruptedException {
    String appId = experimentSupportExamCheckRequestRs.getAppId();
    Integer periods = experimentSupportExamCheckRequestRs.getPeriods();
    String experimentId = experimentSupportExamCheckRequestRs.getExperimentId();
    String experimentPersonId = experimentSupportExamCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentSupportExamCheckRequestRs.getIndicatorFuncId();
    String experimentOrgId = experimentSupportExamCheckRequestRs.getExperimentOrgId();
    List<String> experimentIndicatorViewSupportExamIdList = experimentSupportExamCheckRequestRs.getExperimentIndicatorViewSupportExamIdList();
    // 获取人物正在进行的流水号
    String operateFlowId = ShareBiz.assertRunningOperateFlowId(experimentSupportExamCheckRequestRs.getAppId(),
            experimentSupportExamCheckRequestRs.getExperimentId(),
            experimentSupportExamCheckRequestRs.getExperimentOrgId(),
            experimentSupportExamCheckRequestRs.getExperimentPersonId());

    Set<String> indicatorInstanceIdSet = new HashSet<>();
    List<ExperimentIndicatorViewSupportExamRsEntity> experimentIndicatorViewSupportExamRsEntityList = new ArrayList<>();
    if (Objects.nonNull(experimentIndicatorViewSupportExamIdList) && !experimentIndicatorViewSupportExamIdList.isEmpty()) {
      experimentIndicatorViewSupportExamRsService.lambdaQuery()
          .in(ExperimentIndicatorViewSupportExamRsEntity::getExperimentIndicatorViewSupportExamId, experimentIndicatorViewSupportExamIdList)
          .list()
          .forEach(experimentIndicatorViewSupportExamRsEntity -> {
            indicatorInstanceIdSet.add(experimentIndicatorViewSupportExamRsEntity.getIndicatorInstanceId());
            experimentIndicatorViewSupportExamRsEntityList.add(experimentIndicatorViewSupportExamRsEntity);
          });
    }
    AtomicReference<BigDecimal> totalFeeAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
    CompletableFuture<Void> cfPopulateTotalFee = CompletableFuture.runAsync(() -> {
      experimentIndicatorViewSupportExamRsEntityList.forEach(experimentIndicatorViewSupportExamRsEntity -> {
        totalFeeAtomicReference.set(totalFeeAtomicReference.get().subtract(experimentIndicatorViewSupportExamRsEntity.getFee()));
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


    List<ExperimentIndicatorViewSupportExamReportRsEntity> experimentIndicatorViewSupportExamReportRsEntityList = new ArrayList<>();
    CompletableFuture<Void> cfPopulateExperimentIndicatorViewSupportExamReportRsEntityList = CompletableFuture.runAsync(() -> {
      experimentIndicatorViewSupportExamRsEntityList.forEach(experimentIndicatorViewSupportExamRsEntity -> {
        String currentVal = "";
        String unit = null;
        AtomicReference<String> resultExplainAtomicReference = new AtomicReference<>("");
        String indicatorInstanceId = experimentIndicatorViewSupportExamRsEntity.getIndicatorInstanceId();
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
        ExperimentIndicatorViewSupportExamReportRsEntity experimentIndicatorViewSupportExamReportRsEntity = ExperimentIndicatorViewSupportExamReportRsEntity
            .builder()
            .experimentIndicatorViewSupportExamReportId(idGenerator.nextIdStr())
            .experimentId(experimentId)
            .appId(appId)
            .period(periods)
            .indicatorFuncId(indicatorFuncId)
            .experimentPersonId(experimentPersonId)
            .operateFlowId(operateFlowId)
            .name(experimentIndicatorViewSupportExamRsEntity.getName())
            .fee(experimentIndicatorViewSupportExamRsEntity.getFee())
            .currentVal(currentVal)
            .unit(unit)
            .resultExplain(resultExplainAtomicReference.get())
            .build();
        experimentIndicatorViewSupportExamReportRsEntityList.add(experimentIndicatorViewSupportExamReportRsEntity);
      });
    });
    cfPopulateExperimentIndicatorViewSupportExamReportRsEntityList.get();

    experimentIndicatorInstanceRsBiz.changeMoney(RsChangeMoneyRequest
        .builder()
        .appId(appId)
        .experimentId(experimentId)
        .experimentPersonId(experimentPersonId)
        .periods(periods)
        .moneyChange(totalFeeAtomicReference.get())
        .assertEnough(true)
        .build());
    // 保存消费记录
    LoginContextVO voLogin= ShareBiz.getLoginUser(request);
    // 获取小组信息
    ExperimentPersonEntity personEntity = experimentPersonService.lambdaQuery()
            .eq(ExperimentPersonEntity::getExperimentPersonId,experimentSupportExamCheckRequestRs.getExperimentPersonId())
            .eq(ExperimentPersonEntity::getDeleted,false)
            .one();
    //计算每次操作应该返回的报销金额
    BigDecimal reimburse = getExperimentPersonRestitution(totalFeeAtomicReference.get().negate(),experimentSupportExamCheckRequestRs.getExperimentPersonId());
    CostRequest costRequest = CostRequest.builder()
            .operateCostId(idGenerator.nextIdStr())
            .experimentInstanceId(experimentSupportExamCheckRequestRs.getExperimentId())
            .experimentGroupId(personEntity.getExperimentGroupId())
            .operatorId(voLogin.getAccountId())
            .experimentOrgId(experimentSupportExamCheckRequestRs.getExperimentOrgId())
            .operateFlowId(operateFlowId)
            .patientId(experimentSupportExamCheckRequestRs.getExperimentPersonId())
            .feeName(EnumOrgFeeType.FZJCF.getName())
            .feeCode(EnumOrgFeeType.FZJCF.getCode())
            .cost(totalFeeAtomicReference.get().negate())
            .restitution(reimburse)
            .period(experimentSupportExamCheckRequestRs.getPeriods())
            .build();
    operateCostBiz.saveCost(costRequest);
    experimentIndicatorViewSupportExamReportRsService.saveOrUpdateBatch(experimentIndicatorViewSupportExamReportRsEntityList);
  }

  private BigDecimal getExperimentPersonRestitution(BigDecimal fee,String experimentPersonId){
    //获取在该消费之前的保险购买记录并计算报销比例
    List<OperateInsuranceEntity> insuranceEntityList = operateInsuranceService.lambdaQuery()
            .eq(OperateInsuranceEntity::getExperimentPersonId, experimentPersonId)
            .le(OperateInsuranceEntity::getIndate, new Date())
            .ge(OperateInsuranceEntity::getExpdate, new Date())
            .list();
    //可能会存在多个机构购买情况，金钱要叠加
    BigDecimal reimburse = new BigDecimal(0);
    if (insuranceEntityList != null && insuranceEntityList.size() > 0) {
      for (int j = 0; j < insuranceEntityList.size(); j++) {
        //3.4、通过机构获取报销比例
        ExperimentOrgEntity orgEntity = experimentOrgService.lambdaQuery()
                .eq(ExperimentOrgEntity::getExperimentOrgId, insuranceEntityList.get(j).getExperimentOrgId())
                .eq(ExperimentOrgEntity::getDeleted, false)
                .one();
        if (orgEntity != null && !ReflectUtil.isObjectNull(orgEntity)) {
          CaseOrgFeeEntity feeEntity = caseOrgFeeService.lambdaQuery()
                  .eq(CaseOrgFeeEntity::getCaseOrgId, orgEntity.getCaseOrgId())
                  .eq(CaseOrgFeeEntity::getFeeCode, "BXF")
                  .one();
          if (feeEntity != null && !ReflectUtil.isObjectNull(feeEntity)) {
            reimburse = reimburse.add(fee.multiply(BigDecimal.valueOf(feeEntity.getReimburseRatio())).divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN));
          }
        }
      }
    }
    return reimburse;
  }
}
