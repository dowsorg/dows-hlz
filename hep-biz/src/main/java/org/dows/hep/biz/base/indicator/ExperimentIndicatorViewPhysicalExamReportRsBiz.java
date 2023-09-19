package org.dows.hep.biz.base.indicator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.edw.HepOperateTypeEnum;
import org.dows.edw.domain.HepHealthExamination;
import org.dows.edw.domain.HepHealthTherapy;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.base.indicator.request.ExperimentPhysicalExamCheckRequestRs;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.base.indicator.response.ExperimentPhysicalExamReportResponseRs;
import org.dows.hep.api.base.indicator.response.ExperimentSupportExamReportResponseRs;
import org.dows.hep.api.config.ConfigExperimentFlow;
import org.dows.hep.api.edw.request.HepOperateGetRequest;
import org.dows.hep.api.edw.request.HepOperateSetRequest;
import org.dows.hep.api.enums.EnumIndicatorExpressionField;
import org.dows.hep.api.enums.EnumIndicatorExpressionScene;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumOrgFeeType;
import org.dows.hep.biz.edw.InterveneHandler;
import org.dows.hep.biz.eval.EvalPersonBiz;
import org.dows.hep.biz.eval.QueryPersonBiz;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.dows.hep.biz.request.CaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.DatabaseCalIndicatorExpressionRequest;
import org.dows.hep.biz.request.ExperimentCalIndicatorExpressionRequest;
import org.dows.hep.biz.util.BigDecimalOptional;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.*;
import org.dows.hep.properties.MongoProperties;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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
  private final IdGenerator idGenerator;
  private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;
  private final RsExperimentIndicatorExpressionBiz rsExperimentIndicatorExpressionBiz;
  private final RsExperimentIndicatorInstanceBiz rsExperimentIndicatorInstanceBiz;
  private final RsExperimentIndicatorValBiz rsExperimentIndicatorValBiz;
  private final OperateCostBiz operateCostBiz;
  private final ExperimentPersonService experimentPersonService;
  private final OperateInsuranceService operateInsuranceService;
  private final ExperimentOrgService experimentOrgService;
  private final CaseOrgFeeService caseOrgFeeService;

  private final QueryPersonBiz queryPersonBiz;

  private final EvalPersonBiz evalPersonBiz;
  private final InterveneHandler interveneHandler;
  private final MongoProperties mongoProperties;

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

    boolean useMongo = mongoProperties != null && mongoProperties.getEnable() != null && mongoProperties.getEnable();
    // 保存数据到mongodb
    if(useMongo){
      LoginContextVO voLogin= ShareBiz.getLoginUser(request);
      HepOperateSetRequest hepOperateSetRequest = HepOperateSetRequest.builder()
              .type(HepOperateTypeEnum.getNameByCode(HepHealthExamination.class))
              .experimentInstanceId(Long.valueOf(experimentPhysicalExamCheckRequestRs.getExperimentId()))
              .experimentGroupId(Long.valueOf(experimentPhysicalExamCheckRequestRs.getExperimentGroupId()))
              .operatorId(Long.valueOf(voLogin.getAccountId()))
              .orgTreeId(Long.valueOf(experimentPhysicalExamCheckRequestRs.getExperimentOrgId()))
              .flowId(experimentPhysicalExamCheckRequestRs.getOperateFlowId())
              .personId(Long.valueOf(experimentPhysicalExamCheckRequestRs.getExperimentPersonId()))
              .orgName(experimentPhysicalExamCheckRequestRs.getOrgName())
              .functionName(experimentPhysicalExamCheckRequestRs.getFunctionName())
              .functionCode(experimentPhysicalExamCheckRequestRs.getIndicatorFuncId())
              .data(experimentPhysicalExamCheckRequestRs.getData())
              .period(experimentPhysicalExamCheckRequestRs.getPeriods())
              .onDate(null)
              .onDay(null)
              .build();
      interveneHandler.write(hepOperateSetRequest,HepHealthExamination.class);
      return;
    }
    if(ConfigExperimentFlow.SWITCH2SpelCache){
      evalPersonBiz.physicalExamCheck(experimentPhysicalExamCheckRequestRs,request);
      return;
    }

    Integer periods = experimentPhysicalExamCheckRequestRs.getPeriods();
    String appId = experimentPhysicalExamCheckRequestRs.getAppId();
    String experimentId = experimentPhysicalExamCheckRequestRs.getExperimentId();
    String experimentPersonId = experimentPhysicalExamCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentPhysicalExamCheckRequestRs.getIndicatorFuncId();
    String experimentOrgId = experimentPhysicalExamCheckRequestRs.getExperimentOrgId();
    List<String> experimentIndicatorViewPhysicalExamIdList = experimentPhysicalExamCheckRequestRs.getExperimentIndicatorViewPhysicalExamIdList();
    // 获取人物正在进行的流水号
    String operateFlowId = ShareBiz.assertRunningOperateFlowId(experimentPhysicalExamCheckRequestRs.getAppId(),
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
    experimentIndicatorViewPhysicalExamRsEntityList.forEach(experimentIndicatorViewPhysicalExamRsEntity -> {
      totalFeeAtomicReference.set(totalFeeAtomicReference.get().subtract(experimentIndicatorViewPhysicalExamRsEntity.getFee()));
    });

    Map<String, ExperimentIndicatorInstanceRsEntity> kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
    rsExperimentIndicatorInstanceBiz.populateKIndicatorInstanceIdVExperimentIndicatorInstanceMap(
            kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap, experimentPersonId, indicatorInstanceIdSet
    );

    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
    if (!indicatorInstanceIdSet.isEmpty()) {
      indicatorInstanceIdSet.forEach(indicatorInstanceId -> {
        ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(indicatorInstanceId);
        if (Objects.nonNull(experimentIndicatorInstanceRsEntity) && StringUtils.isNotBlank(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId())) {
          experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
        }
      });
    }

    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap =queryPersonBiz.populateOnePersonKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(experimentPersonId,periods);


    Map<String, ExperimentIndicatorExpressionRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
    rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap(
            kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap, experimentIndicatorInstanceIdSet
    );

    Set<String> experimentIndicatorExpressionIdSet = kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionRsEntityMap.values()
        .stream().map(ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId).collect(Collectors.toSet());
    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
    rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap(
            kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap, experimentIndicatorExpressionIdSet
    );

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
    rsExperimentIndicatorExpressionBiz.populateKExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap(
            kExperimentIndicatorExpressionItemIdVExperimentIndicatorExpressionItemRsEntityMap, minAndMaxExperimentIndicatorExpressionItemIdSet
    );


    List<ExperimentIndicatorViewPhysicalExamReportRsEntity> experimentIndicatorViewPhysicalExamReportRsEntityList = new ArrayList<>();
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
              .currentVal(Optional.ofNullable(BigDecimalOptional.valueOf(resultExplainAtomicReference.get()).getString(2, RoundingMode.HALF_UP))
                      .orElse(currentVal))
              .unit(unit)
              .resultExplain(experimentIndicatorViewPhysicalExamRsEntity.getResultAnalysis())
              .build();
      experimentIndicatorViewPhysicalExamReportRsEntityList.add(experimentIndicatorViewPhysicalExamReportRsEntity);
    });

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
                           .eq(ExperimentPersonEntity::getExperimentPersonId,experimentPhysicalExamCheckRequestRs.getExperimentPersonId())
                           .eq(ExperimentPersonEntity::getDeleted,false)
                           .one();
    //计算每次操作应该返回的报销金额
    BigDecimal reimburse = getExperimentPersonRestitution(totalFeeAtomicReference.get().negate(),experimentPhysicalExamCheckRequestRs.getExperimentPersonId());
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
            .cost(totalFeeAtomicReference.get().negate())
            .restitution(reimburse)
            .period(experimentPhysicalExamCheckRequestRs.getPeriods())
            .build();
    operateCostBiz.saveCost(costRequest);
    experimentIndicatorViewPhysicalExamReportRsService.saveOrUpdateBatch(experimentIndicatorViewPhysicalExamReportRsEntityList);
  }

  public List<ExperimentPhysicalExamReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId, String experimentOrgId, Integer periods) {
    String operateFlowId = ShareBiz.checkRunningOperateFlowId(appId, experimentId, experimentOrgId, experimentPersonId);
    if(ShareUtil.XObject.isEmpty(operateFlowId)){
      return Collections.emptyList();
    }
    boolean useMongo = mongoProperties != null && mongoProperties.getEnable() != null && mongoProperties.getEnable();
    if(useMongo){
      HepOperateGetRequest hepOperateGetRequest = HepOperateGetRequest.builder()
              .type(HepOperateTypeEnum.getNameByCode(HepHealthExamination.class))
              .experimentInstanceId(Long.valueOf(experimentId))
              //             .experimentGroupId(Long.valueOf(experimentGroupId))
              //            .operatorId(Long.valueOf(reqOperateFunc.getOperateAccountId()))
              .orgTreeId(Long.valueOf(experimentOrgId))
              .flowId(operateFlowId)
              .personId(Long.valueOf(experimentPersonId))
              .period(periods)
              .build();
      return interveneHandler.read(hepOperateGetRequest, ExperimentPhysicalExamReportResponseRs.class, HepHealthExamination.class);
    }else {
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
            reimburse = reimburse.add(fee.multiply(BigDecimal.valueOf(feeEntity.getReimburseRatio())).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
          }
        }
      }
    }
    return reimburse;
  }
}
