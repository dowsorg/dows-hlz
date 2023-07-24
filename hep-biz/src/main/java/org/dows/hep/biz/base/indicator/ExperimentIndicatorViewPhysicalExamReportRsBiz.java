package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dows.hep.api.base.indicator.request.ExperimentPhysicalExamCheckRequestRs;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.base.indicator.response.ExperimentPhysicalExamReportResponseRs;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.ExperimentIndicatorViewPhysicalExamReportRsException;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
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
//  @Transactional(rollbackFor = Exception.class)
//  public void physicalExamCheck(ExperimentPhysicalExamCheckRequestRs experimentPhysicalExamCheckRequestRs) throws ExecutionException, InterruptedException {
//    List<ExperimentIndicatorViewPhysicalExamReportRsEntity> experimentIndicatorViewPhysicalExamReportRsEntityList = new ArrayList<>();
//    /* runsix:TODO 这个期数后期根据张亮接口拿 */
//    Integer period = 1;
//    String experimentPersonId = experimentPhysicalExamCheckRequestRs.getExperimentPersonId();
//    String indicatorFuncId = experimentPhysicalExamCheckRequestRs.getIndicatorFuncId();
//    List<String> experimentIndicatorViewPhysicalExamIdList = experimentPhysicalExamCheckRequestRs.getExperimentIndicatorViewPhysicalExamIdList();
//    String appId = experimentPhysicalExamCheckRequestRs.getAppId();
//    String experimentId = experimentPhysicalExamCheckRequestRs.getExperimentId();
//    String experimentOrgId = experimentPhysicalExamCheckRequestRs.getExperimentOrgId();
//    /* runsix:TODO 等吴治霖弄好 */
//    String operateFlowId = "1";
////    ExptOrgFlowValidator exptOrgFlowValidator = ExptOrgFlowValidator.create(appId, experimentId, experimentOrgId, experimentPersonId);
////    exptOrgFlowValidator.checkOrgFlow(true);
////    String operateFlowId = exptOrgFlowValidator.getOperateFlowId();
//    AtomicReference<BigDecimal> moneyChangeAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
//    Map<String, ExperimentIndicatorViewPhysicalExamRsEntity> kExperimentIndicatorViewPhysicalExamIdVExperimentIndicatorViewPhysicalExamRsEntityMap = new HashMap<>();
//    Set<String> indicatorInstanceIdSet = new HashSet<>();
//    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
//    if (!experimentIndicatorViewPhysicalExamIdList.isEmpty()) {
//      experimentIndicatorViewPhysicalExamRsService.lambdaQuery()
//          .eq(ExperimentIndicatorViewPhysicalExamRsEntity::getAppId, appId)
//          .in(ExperimentIndicatorViewPhysicalExamRsEntity::getExperimentIndicatorViewPhysicalExamId, experimentIndicatorViewPhysicalExamIdList)
//          .list()
//          .forEach(experimentIndicatorViewPhysicalExamRsEntity -> {
//            BigDecimal fee = experimentIndicatorViewPhysicalExamRsEntity.getFee();
//            BigDecimal currentFee = moneyChangeAtomicReference.get();
//            moneyChangeAtomicReference.set(currentFee.subtract(fee));
//            indicatorInstanceIdSet.add(experimentIndicatorViewPhysicalExamRsEntity.getIndicatorInstanceId());
//            kExperimentIndicatorViewPhysicalExamIdVExperimentIndicatorViewPhysicalExamRsEntityMap.put(experimentIndicatorViewPhysicalExamRsEntity.getExperimentIndicatorViewPhysicalExamId(), experimentIndicatorViewPhysicalExamRsEntity);
//          });
//    }
//    Map<String, ExperimentIndicatorInstanceRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap = new HashMap<>();
//    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
//    Map<String, List<String>> kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionIdListMap = new HashMap<>();
//    Map<String, ExperimentIndicatorExpressionRsEntity> kExperimentIndicatorInstanceIdVSourceExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
//    Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
//    Set<String> experimentIndicatorExpressionIdSet = new HashSet<>();
//    if (!indicatorInstanceIdSet.isEmpty()) {
//      experimentIndicatorInstanceRsService.lambdaQuery()
//          .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
//          .in(ExperimentIndicatorInstanceRsEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
//          .list()
//          .forEach(experimentIndicatorInstanceRsEntity -> {
//            experimentIndicatorInstanceIdSet.add(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
//            kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.put(
//                experimentIndicatorInstanceRsEntity.getIndicatorInstanceId(), experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId());
//          });
//      experimentIndicatorInstanceRsService.lambdaQuery()
//          .in(ExperimentIndicatorInstanceRsEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
//          .list()
//          .forEach(experimentIndicatorInstanceRsEntity -> {
//            kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.put(experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId(), experimentIndicatorInstanceRsEntity);
//          });
//      CompletableFuture<Void> cfPopulateOnePersonKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = CompletableFuture.runAsync(() -> {
//        rsExperimentIndicatorValBiz.populateOnePersonKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(
//            kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, experimentPersonId, period
//        );
//      });
//      cfPopulateOnePersonKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get();
//    }
//    if (!experimentIndicatorInstanceIdSet.isEmpty()) {
//      experimentIndicatorExpressionRefRsService.lambdaQuery()
//          .in(ExperimentIndicatorExpressionRefRsEntity::getReasonId, experimentIndicatorInstanceIdSet)
//          .list()
//          .forEach(experimentIndicatorExpressionRefRsEntity -> {
//            String reasonId = experimentIndicatorExpressionRefRsEntity.getReasonId();
//            String indicatorExpressionId = experimentIndicatorExpressionRefRsEntity.getIndicatorExpressionId();
//            List<String> experimentIndicatorExpressionIdList = kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionIdListMap.get(reasonId);
//            if (Objects.isNull(experimentIndicatorExpressionIdList)) {
//              experimentIndicatorExpressionIdList = new ArrayList<>();
//            }
//            experimentIndicatorExpressionIdList.add(indicatorExpressionId);
//            kExperimentIndicatorInstanceIdVExperimentIndicatorExpressionIdListMap.put(reasonId, experimentIndicatorExpressionIdList);
//            experimentIndicatorExpressionIdSet.add(indicatorExpressionId);
//          });
//    }
//    Map<String, ExperimentIndicatorExpressionRsEntity> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap = new HashMap<>();
//    Set<String> sourceExperimentIndicatorExpressionIdSet = new HashSet<>();
//    if (!experimentIndicatorExpressionIdSet.isEmpty()) {
//      experimentIndicatorExpressionRsService.lambdaQuery()
//          .eq(ExperimentIndicatorExpressionRsEntity::getExperimentId, experimentId)
//          .eq(ExperimentIndicatorExpressionRsEntity::getSource, EnumIndicatorExpressionSource.INDICATOR_MANAGEMENT.getSource())
//          .in(ExperimentIndicatorExpressionRsEntity::getExperimentIndicatorExpressionId, experimentIndicatorExpressionIdSet)
//          .list()
//          .forEach(experimentIndicatorExpressionRsEntity -> {
//            sourceExperimentIndicatorExpressionIdSet.add(experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId());
//            kExperimentIndicatorInstanceIdVSourceExperimentIndicatorExpressionRsEntityMap.put(experimentIndicatorExpressionRsEntity.getPrincipalId(), experimentIndicatorExpressionRsEntity);
//            kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionRsEntityMap.put(experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId(), experimentIndicatorExpressionRsEntity);
//          });
//    }
//    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap = new HashMap<>();
//    if (!sourceExperimentIndicatorExpressionIdSet.isEmpty()) {
//      experimentIndicatorExpressionItemRsService.lambdaQuery()
//          .eq(ExperimentIndicatorExpressionItemRsEntity::getAppId, appId)
//          .eq(ExperimentIndicatorExpressionItemRsEntity::getExperimentId, experimentId)
//          .in(ExperimentIndicatorExpressionItemRsEntity::getIndicatorExpressionId, sourceExperimentIndicatorExpressionIdSet)
//          .list()
//          .forEach(experimentIndicatorExpressionItemRsEntity -> {
//            String indicatorExpressionId = experimentIndicatorExpressionItemRsEntity.getIndicatorExpressionId();
//            List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(indicatorExpressionId);
//            if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList)) {
//              experimentIndicatorExpressionItemRsEntityList = new ArrayList<>();
//            }
//            experimentIndicatorExpressionItemRsEntityList.add(experimentIndicatorExpressionItemRsEntity);
//            kIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.put(indicatorExpressionId, experimentIndicatorExpressionItemRsEntityList);
//          });
//    }
//    /* runsix:sort */
//    kIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.forEach((indicatorExpressionId, experimentIndicatorExpressionItemRsEntityList) -> {
//      experimentIndicatorExpressionItemRsEntityList.sort(Comparator.comparingInt(ExperimentIndicatorExpressionItemRsEntity::getSeq));
//    });
//    kExperimentIndicatorViewPhysicalExamIdVExperimentIndicatorViewPhysicalExamRsEntityMap.forEach((experimentIndicatorViewPhysicalExamId, experimentIndicatorViewPhysicalExamRsEntity) -> {
//      String indicatorInstanceId = experimentIndicatorViewPhysicalExamRsEntity.getIndicatorInstanceId();
//      String experimentIndicatorInstanceId = kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
//      if (Objects.isNull(experimentIndicatorInstanceId)) {
//        log.error("ExperimentIndicatorViewPhysicalExamReportRsBiz.physicalExamCheck indicatorInstanceId:{} mapped no experimentIndicatorInstanceId", indicatorInstanceId);
//        throw new ExperimentIndicatorViewPhysicalExamReportRsException(EnumESC.VALIDATE_EXCEPTION);
//      }
//      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = kExperimentIndicatorInstanceIdVSourceExperimentIndicatorExpressionRsEntityMap.get(experimentIndicatorInstanceId);
//      if (Objects.isNull(experimentIndicatorExpressionRsEntity)) {
//        log.error("ExperimentIndicatorViewPhysicalExamReportRsBiz.physicalExamCheck experimentIndicatorInstanceId:{} has no ExperimentIndicatorExpressionRsEntity", experimentIndicatorInstanceId);
//        throw new ExperimentIndicatorViewPhysicalExamReportRsException(EnumESC.VALIDATE_EXCEPTION);
//      }
//      String experimentIndicatorExpressionId = experimentIndicatorExpressionRsEntity.getExperimentIndicatorExpressionId();
//      List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kIndicatorExpressionIdVExperimentIndicatorExpressionItemRsEntityListMap.get(experimentIndicatorExpressionId);
//      AtomicReference<String> atomicReferenceResultExplain = new AtomicReference<>();
//      if (Objects.nonNull(experimentIndicatorExpressionItemRsEntityList)) {
//        for (ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity : experimentIndicatorExpressionItemRsEntityList) {
//          String conditionExpression = experimentIndicatorExpressionItemRsEntity.getConditionExpression();
//          String conditionNameList = experimentIndicatorExpressionItemRsEntity.getConditionNameList();
//          String conditionValList = experimentIndicatorExpressionItemRsEntity.getConditionValList();
//          String resultExpression = experimentIndicatorExpressionItemRsEntity.getResultExpression();
//          String resultNameList = experimentIndicatorExpressionItemRsEntity.getResultNameList();
//          String resultValList = experimentIndicatorExpressionItemRsEntity.getResultValList();
//          if (StringUtils.isBlank(conditionExpression)) {
//            if (StringUtils.isBlank(resultExpression)) {
//              log.error("ExperimentIndicatorViewPhysicalExamReportRsBiz.physicalExamCheck experimentIndicatorExpressionItemId:{} is illegal, conditionExpression && resultExpression is blank", experimentIndicatorExpressionItemRsEntity.getExperimentIndicatorExpressionItemId());
//              throw new ExperimentIndicatorViewPhysicalExamReportRsException(EnumESC.VALIDATE_EXCEPTION);
//            } else {
//              if (StringUtils.isBlank(resultNameList)) {
//                atomicReferenceResultExplain.set(resultExpression);
//              } else {
//                StandardEvaluationContext context = new StandardEvaluationContext();
//                List<String> resultNameListSplit = Arrays.stream(resultNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
//                List<String> resultValListSplit = Arrays.stream(resultValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
//                for (int i = 0; i <= resultNameListSplit.size()-1; i++) {
//                  ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(resultValListSplit.get(i));
//                  String val = experimentIndicatorValRsEntity.getCurrentVal();
//                  boolean isValDigital = NumberUtils.isCreatable(val);
//                  if (isValDigital) {
//                    context.setVariable(resultNameListSplit.get(i), Double.parseDouble(val));
//                  } else {
//                    context.setVariable(resultNameListSplit.get(i), val);
//                  }
//                }
//                ExpressionParser parser = new SpelExpressionParser();
//                Expression expression = parser.parseExpression(resultExpression);
//                String resultExpressionResult = expression.getValue(context, String.class);
//                atomicReferenceResultExplain.set(resultExpressionResult);
//              }
//            }
//          } else {
//            if (StringUtils.isBlank(resultExpression)) {
//              // do nothing
//            } else {
//              if (StringUtils.isBlank(conditionNameList)) {
//                StandardEvaluationContext context = new StandardEvaluationContext();
//                ExpressionParser parser = new SpelExpressionParser();
//                Expression expression = parser.parseExpression(conditionExpression);
//                Boolean condition = expression.getValue(context, Boolean.class);
//                if (condition) {
//                  if (StringUtils.isBlank(resultNameList)) {
//                    atomicReferenceResultExplain.set(resultExpression);
//                  } else {
//                    StandardEvaluationContext context1 = new StandardEvaluationContext();
//                    List<String> resultNameListSplit = Arrays.stream(resultNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
//                    List<String> resultValListSplit = Arrays.stream(resultValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
//                    for (int i = 0; i <= resultNameListSplit.size()-1; i++) {
//                      ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(resultValListSplit.get(i));
//                      String val = experimentIndicatorValRsEntity.getCurrentVal();
//                      boolean isValDigital = NumberUtils.isCreatable(val);
//                      if (isValDigital) {
//                        context1.setVariable(resultNameListSplit.get(i), Double.parseDouble(val));
//                      } else {
//                        context1.setVariable(resultNameListSplit.get(i), val);
//                      }
//                    }
//                    ExpressionParser parser1 = new SpelExpressionParser();
//                    Expression expression1 = parser1.parseExpression(resultExpression);
//                    String resultExpressionResult = expression1.getValue(context1, String.class);
//                    atomicReferenceResultExplain.set(resultExpressionResult);
//                  }
//                }
//              } else {
//                StandardEvaluationContext context = new StandardEvaluationContext();
//                List<String> conditionNameListSpilt = Arrays.stream(conditionNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
//                List<String> conditionValListSpilt = Arrays.stream(conditionValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
//                for (int i = 0; i <= conditionValListSpilt.size()-1; i++) {
//                  ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(conditionValListSpilt.get(i));
//                  String val = experimentIndicatorValRsEntity.getCurrentVal();
//                  boolean isValDigital = NumberUtils.isCreatable(val);
//                  if (isValDigital) {
//                    context.setVariable(conditionNameListSpilt.get(i), Double.parseDouble(val));
//                  } else {
//                    val = v1WrapStrWithDoubleSingleQuotes(val);
//                    context.setVariable(conditionNameListSpilt.get(i), val);
//                  }
//                }
//                String conditionExpression1 = experimentIndicatorExpressionItemRsEntity.getConditionExpression();
//                ExpressionParser parser1 = new SpelExpressionParser();
//                Expression expression = parser1.parseExpression(conditionExpression1);
//                Boolean condition = expression.getValue(context, Boolean.class);
//                if (condition) {
//                  if (StringUtils.isBlank(resultNameList)) {
//                    atomicReferenceResultExplain.set(resultExpression);
//                  } else {
//                    StandardEvaluationContext context1 = new StandardEvaluationContext();
//                    List<String> resultNameListSplit = Arrays.stream(resultNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
//                    List<String> resultValListSplit = Arrays.stream(resultValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
//                    for (int i = 0; i <= resultNameListSplit.size()-1; i++) {
//                      ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(resultValListSplit.get(i));
//                      String val = experimentIndicatorValRsEntity.getCurrentVal();
//                      boolean isValDigital = NumberUtils.isCreatable(val);
//                      if (isValDigital) {
//                        context1.setVariable(resultNameListSplit.get(i), Double.parseDouble(val));
//                      } else {
//                        context1.setVariable(resultNameListSplit.get(i), val);
//                      }
//                    }
//                    ExpressionParser parser2 = new SpelExpressionParser();
//                    Expression expression2 = parser2.parseExpression(resultExpression);
//                    String resultExpressionResult = expression2.getValue(context1, String.class);
//                    atomicReferenceResultExplain.set(resultExpressionResult);
//                  }
//                }
//              }
//            }
//          }
//        }
//      }
//      String currentVal = null;
//      String unit = null;
//      ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
//      ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.get(experimentIndicatorInstanceId);
//      if (Objects.nonNull(experimentIndicatorValRsEntity)) {
//        currentVal = experimentIndicatorValRsEntity.getCurrentVal();
//      }
//      if (Objects.nonNull(experimentIndicatorInstanceRsEntity)) {
//        unit = experimentIndicatorInstanceRsEntity.getUnit();
//      }
//      experimentIndicatorViewPhysicalExamReportRsEntityList.add(
//          ExperimentIndicatorViewPhysicalExamReportRsEntity
//              .builder()
//              .experimentIndicatorViewPhysicalExamReportId(idGenerator.nextIdStr())
//              .experimentId(experimentId)
//              .appId(appId)
//              .period(period)
//              .indicatorFuncId(indicatorFuncId)
//              .experimentPersonId(experimentPersonId)
//              .operateFlowId(operateFlowId)
//              .name(experimentIndicatorViewPhysicalExamRsEntity.getName())
//              .fee(experimentIndicatorViewPhysicalExamRsEntity.getFee())
//              .currentVal(currentVal)
//              .unit(unit)
//              .resultExplain(atomicReferenceResultExplain.get())
//              .build()
//      );
//    });
//    experimentIndicatorInstanceRsBiz.changeMoney(RsChangeMoneyRequest
//        .builder()
//        .appId(appId)
//        .experimentId(experimentId)
//        .experimentPersonId(experimentPersonId)
//        .periods(period)
//        .moneyChange(moneyChangeAtomicReference.get())
//        .build());
//    experimentIndicatorViewPhysicalExamReportRsService.saveOrUpdateBatch(experimentIndicatorViewPhysicalExamReportRsEntityList);
//  }

  /**
   * runsix method process
   * 1.change money
   * 2.save reportList
  */
  @Transactional(rollbackFor = Exception.class)
  public void v1PhysicalExamCheck(ExperimentPhysicalExamCheckRequestRs experimentPhysicalExamCheckRequestRs) throws ExecutionException, InterruptedException {
    /* runsix:TODO 这个期数后期根据张亮接口拿 */
    Integer periods = 1;
    String appId = experimentPhysicalExamCheckRequestRs.getAppId();
    String experimentId = experimentPhysicalExamCheckRequestRs.getExperimentId();
    String experimentPersonId = experimentPhysicalExamCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentPhysicalExamCheckRequestRs.getIndicatorFuncId();
    String experimentOrgId = experimentPhysicalExamCheckRequestRs.getExperimentOrgId();
    List<String> experimentIndicatorViewPhysicalExamIdList = experimentPhysicalExamCheckRequestRs.getExperimentIndicatorViewPhysicalExamIdList();
    /* runsix:TODO 等吴治霖弄好 */
    String operateFlowId = "1";

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
        totalFeeAtomicReference.set(totalFeeAtomicReference.get().add(experimentIndicatorViewPhysicalExamRsEntity.getFee()));
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
                    kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
                    experimentIndicatorExpressionRsEntity,
                    experimentIndicatorExpressionItemRsEntityList,
                    minExperimentIndicatorExpressionItemRsEntity,
                    maxExperimentIndicatorExpressionItemRsEntity
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
    experimentIndicatorViewPhysicalExamReportRsService.saveOrUpdateBatch(experimentIndicatorViewPhysicalExamReportRsEntityList);
  }

  public List<ExperimentPhysicalExamReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId, String experimentOrgId) {
    /* runsix:TODO 期数当前写死为1,后期从张亮获取 */
    Integer period = 1;
    /* runsix:TODO 以后找吴治霖 */
    String operateFlowId = "1";
//    ExptOrgFlowValidator exptOrgFlowValidator = ExptOrgFlowValidator.create(appId, experimentId, experimentOrgId, experimentPersonId);
//    exptOrgFlowValidator.checkOrgFlow(true);
//    String operateFlowId = exptOrgFlowValidator.getOperateFlowId();
    return experimentIndicatorViewPhysicalExamReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getPeriod, period)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getIndicatorFuncId, indicatorFuncId)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getExperimentPersonId, experimentPersonId)
        .eq(ExperimentIndicatorViewPhysicalExamReportRsEntity::getOperateFlowId, operateFlowId)
        .orderByDesc(ExperimentIndicatorViewPhysicalExamReportRsEntity::getDt)
        .list()
        .stream()
        .map(ExperimentIndicatorViewPhysicalExamReportRsBiz::experimentPhysicalExamReport2ResponseRs)
        .collect(Collectors.toList());
  }

  private static String v1WrapStrWithDoubleSingleQuotes(String str) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(EnumString.SINGLE_QUOTES.getStr());
    stringBuffer.append(str);
    stringBuffer.append(EnumString.SINGLE_QUOTES.getStr());
    return stringBuffer.toString();
  }
}
