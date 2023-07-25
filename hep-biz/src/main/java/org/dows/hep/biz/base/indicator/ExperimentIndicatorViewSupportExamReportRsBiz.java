package org.dows.hep.biz.base.indicator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.ExperimentSupportExamCheckRequestRs;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.base.indicator.response.ExperimentSupportExamReportResponseRs;
import org.dows.hep.api.enums.*;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.vo.LoginContextVO;
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
public class ExperimentIndicatorViewSupportExamReportRsBiz {
  private final ExperimentIndicatorViewSupportExamReportRsService experimentIndicatorViewSupportExamReportRsService;
  private final ExperimentIndicatorViewSupportExamRsService experimentIndicatorViewSupportExamRsService;
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final IdGenerator idGenerator;
  private final ExperimentIndicatorExpressionRefRsService experimentIndicatorExpressionRefRsService;
  private final ExperimentIndicatorExpressionRsService experimentIndicatorExpressionRsService;
  private final ExperimentIndicatorExpressionItemRsService experimentIndicatorExpressionItemRsService;
  private final ExperimentIndicatorExpressionInfluenceRsService experimentIndicatorExpressionInfluenceRsService;
  private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;
  private final RsExperimentIndicatorValBiz rsExperimentIndicatorValBiz;
  private final RsExperimentIndicatorInstanceBiz rsExperimentIndicatorInstanceBiz;
  private final RsExperimentIndicatorExpressionBiz rsExperimentIndicatorExpressionBiz;
  private final ExperimentPersonService experimentPersonService;
  private final OperateCostBiz operateCostBiz;

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
//  @Transactional(rollbackFor = Exception.class)
//  public void supportExamCheck(ExperimentSupportExamCheckRequestRs experimentSupportExamCheckRequestRs) throws ExecutionException, InterruptedException {
//    List<ExperimentIndicatorViewSupportExamReportRsEntity> experimentIndicatorViewSupportExamReportRsEntityList = new ArrayList<>();
//    /* runsix:TODO 这个期数后期根据张亮接口拿 */
//    Integer period = 1;
//    String experimentPersonId = experimentSupportExamCheckRequestRs.getExperimentPersonId();
//    String indicatorFuncId = experimentSupportExamCheckRequestRs.getIndicatorFuncId();
//    List<String> experimentIndicatorViewSupportExamIdList = experimentSupportExamCheckRequestRs.getExperimentIndicatorViewSupportExamIdList();
//    String appId = experimentSupportExamCheckRequestRs.getAppId();
//    String experimentId = experimentSupportExamCheckRequestRs.getExperimentId();
//    String experimentOrgId = experimentSupportExamCheckRequestRs.getExperimentOrgId();
//    /* runsix:TODO 等吴治霖弄好 */
//    String operateFlowId = "1";
////    ExptOrgFlowValidator exptOrgFlowValidator = ExptOrgFlowValidator.create(appId, experimentId, experimentOrgId, experimentPersonId);
////    exptOrgFlowValidator.checkOrgFlow(true);
////    String operateFlowId = exptOrgFlowValidator.getOperateFlowId();
//    AtomicReference<BigDecimal> moneyChangeAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
//    Map<String, ExperimentIndicatorViewSupportExamRsEntity> kExperimentIndicatorViewSupportExamIdVExperimentIndicatorViewSupportExamRsEntityMap = new HashMap<>();
//    Set<String> indicatorInstanceIdSet = new HashSet<>();
//    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
//    if (!experimentIndicatorViewSupportExamIdList.isEmpty()) {
//      experimentIndicatorViewSupportExamRsService.lambdaQuery()
//          .eq(ExperimentIndicatorViewSupportExamRsEntity::getAppId, appId)
//          .in(ExperimentIndicatorViewSupportExamRsEntity::getExperimentIndicatorViewSupportExamId, experimentIndicatorViewSupportExamIdList)
//          .list()
//          .forEach(experimentIndicatorViewSupportExamRsEntity -> {
//            BigDecimal fee = experimentIndicatorViewSupportExamRsEntity.getFee();
//            BigDecimal currentFee = moneyChangeAtomicReference.get();
//            moneyChangeAtomicReference.set(currentFee.subtract(fee));
//            indicatorInstanceIdSet.add(experimentIndicatorViewSupportExamRsEntity.getIndicatorInstanceId());
//            kExperimentIndicatorViewSupportExamIdVExperimentIndicatorViewSupportExamRsEntityMap.put(experimentIndicatorViewSupportExamRsEntity.getExperimentIndicatorViewSupportExamId(), experimentIndicatorViewSupportExamRsEntity);
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
//    /* runsix:TODO 公式解析放到后面 */
//    kExperimentIndicatorViewSupportExamIdVExperimentIndicatorViewSupportExamRsEntityMap.forEach((experimentIndicatorViewSupportExamId, experimentIndicatorViewSupportExamRsEntity) -> {
//      String indicatorInstanceId = experimentIndicatorViewSupportExamRsEntity.getIndicatorInstanceId();
//      String experimentIndicatorInstanceId = kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
//      if (Objects.isNull(experimentIndicatorInstanceId)) {
//        log.error("ExperimentIndicatorViewSupportExamReportRsBiz.supportExamCheck indicatorInstanceId:{} mapped no experimentIndicatorInstanceId", indicatorInstanceId);
//        throw new ExperimentIndicatorViewSupportExamReportRsException(EnumESC.VALIDATE_EXCEPTION);
//      }
//      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity = kExperimentIndicatorInstanceIdVSourceExperimentIndicatorExpressionRsEntityMap.get(experimentIndicatorInstanceId);
//      if (Objects.isNull(experimentIndicatorExpressionRsEntity)) {
//        log.error("ExperimentIndicatorViewSupportExamReportRsBiz.supportExamCheck experimentIndicatorInstanceId:{} has no ExperimentIndicatorExpressionRsEntity", experimentIndicatorInstanceId);
//        throw new ExperimentIndicatorViewSupportExamReportRsException(EnumESC.VALIDATE_EXCEPTION);
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
//              log.error("ExperimentIndicatorViewSupportExamReportRsBiz.supportExamCheck experimentIndicatorExpressionItemId:{} is illegal, conditionExpression && resultExpression is blank", experimentIndicatorExpressionItemRsEntity.getExperimentIndicatorExpressionItemId());
//              throw new ExperimentIndicatorViewSupportExamReportRsException(EnumESC.VALIDATE_EXCEPTION);
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
//      experimentIndicatorViewSupportExamReportRsEntityList.add(
//          ExperimentIndicatorViewSupportExamReportRsEntity
//              .builder()
//              .experimentIndicatorViewSupportExamReportId(idGenerator.nextIdStr())
//              .experimentId(experimentId)
//              .appId(appId)
//              .period(period)
//              .indicatorFuncId(indicatorFuncId)
//              .experimentPersonId(experimentPersonId)
//              .operateFlowId(operateFlowId)
//              .name(experimentIndicatorViewSupportExamRsEntity.getName())
//              .fee(experimentIndicatorViewSupportExamRsEntity.getFee())
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
//    experimentIndicatorViewSupportExamReportRsService.saveOrUpdateBatch(experimentIndicatorViewSupportExamReportRsEntityList);
//  }

  public List<ExperimentSupportExamReportResponseRs> get(String appId, String experimentId, String indicatorFuncId, String experimentPersonId, String experimentOrgId) {
    /* runsix:TODO 期数当前写死为1,后期从张亮获取 */
    Integer period = 1;
    /* runsix:TODO 以后找吴治霖 */
    String operateFlowId = "1";
//    ExptOrgFlowValidator exptOrgFlowValidator = ExptOrgFlowValidator.create(appId, experimentId, experimentOrgId, experimentPersonId);
//    exptOrgFlowValidator.checkOrgFlow(true);
//    String operateFlowId = exptOrgFlowValidator.getOperateFlowId();
    return experimentIndicatorViewSupportExamReportRsService.lambdaQuery()
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getAppId, appId)
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getExperimentId, experimentId)
        .eq(ExperimentIndicatorViewSupportExamReportRsEntity::getPeriod, period)
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
  public void v1SupportExamCheck(ExperimentSupportExamCheckRequestRs experimentSupportExamCheckRequestRs, HttpServletRequest request) throws ExecutionException, InterruptedException {
    /* runsix:TODO 这个期数后期根据张亮接口拿 */
    Integer period = 1;
    String appId = experimentSupportExamCheckRequestRs.getAppId();
    String experimentId = experimentSupportExamCheckRequestRs.getExperimentId();
    String experimentPersonId = experimentSupportExamCheckRequestRs.getExperimentPersonId();
    String indicatorFuncId = experimentSupportExamCheckRequestRs.getIndicatorFuncId();
    String experimentOrgId = experimentSupportExamCheckRequestRs.getExperimentOrgId();
    List<String> experimentIndicatorViewSupportExamIdList = experimentSupportExamCheckRequestRs.getExperimentIndicatorViewSupportExamIdList();
    // 获取人物正在进行的流水号
    String operateFlowId = ShareBiz.checkRunningOperateFlowId(experimentSupportExamCheckRequestRs.getAppId(),
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
        totalFeeAtomicReference.set(totalFeeAtomicReference.get().add(experimentIndicatorViewSupportExamRsEntity.getFee()));
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
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, experimentPersonId, period
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
        ExperimentIndicatorViewSupportExamReportRsEntity experimentIndicatorViewSupportExamReportRsEntity = ExperimentIndicatorViewSupportExamReportRsEntity
            .builder()
            .experimentIndicatorViewSupportExamReportId(idGenerator.nextIdStr())
            .experimentId(experimentId)
            .appId(appId)
            .period(period)
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
        .periods(period)
        .moneyChange(totalFeeAtomicReference.get())
        .build());
    // 保存消费记录
    LoginContextVO voLogin= ShareBiz.getLoginUser(request);
    // 获取小组信息
    ExperimentPersonEntity personEntity = experimentPersonService.lambdaQuery()
            .eq(ExperimentPersonEntity::getExperimentPersonId,experimentSupportExamCheckRequestRs.getExperimentPersonId())
            .eq(ExperimentPersonEntity::getDeleted,false)
            .one();
    CostRequest costRequest = CostRequest.builder()
            .operateCostId(idGenerator.nextIdStr())
            .experimentInstanceId(experimentSupportExamCheckRequestRs.getExperimentId())
            .experimentGroupId(personEntity.getExperimentGroupId())
            .operatorId(voLogin.getAccountId())
            .experimentOrgId(experimentSupportExamCheckRequestRs.getExperimentOrgId())
            .operateFlowId(operateFlowId)
            .patientId(experimentSupportExamCheckRequestRs.getExperimentPersonId())
            .feeName(EnumOrgFeeType.TGJCF.getName())
            .feeCode(EnumOrgFeeType.TGJCF.getCode())
            .cost(totalFeeAtomicReference.get())
            .period(experimentSupportExamCheckRequestRs.getPeriods())
            .build();
    operateCostBiz.saveCost(costRequest);
    experimentIndicatorViewSupportExamReportRsService.saveOrUpdateBatch(experimentIndicatorViewSupportExamReportRsEntityList);
  }
  private static String v1WrapStrWithDoubleSingleQuotes(String str) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(EnumString.SINGLE_QUOTES.getStr());
    stringBuffer.append(str);
    stringBuffer.append(EnumString.SINGLE_QUOTES.getStr());
    return stringBuffer.toString();
  }
}
