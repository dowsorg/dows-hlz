package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dows.hep.api.base.indicator.request.RsIndicatorExpressionCheckoutConditionRequest;
import org.dows.hep.api.base.indicator.request.RsIndicatorExpressionCheckoutResultRequest;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.RsIndicatorExpressionException;
import org.dows.hep.entity.ExperimentIndicatorExpressionItemRsEntity;
import org.dows.hep.entity.ExperimentIndicatorExpressionRsEntity;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;
import org.dows.hep.entity.IndicatorRuleEntity;
import org.dows.hep.service.ExperimentIndicatorExpressionItemRsService;
import org.dows.hep.service.IndicatorRuleService;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

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
public class RsIndicatorExpressionBiz {
  private final IndicatorRuleService indicatorRuleService;
  private final RsExperimentIndicatorInstanceBiz rsExperimentIndicatorInstanceBiz;
  private final ExperimentIndicatorExpressionItemRsService experimentIndicatorExpressionItemRsService;
  private final String RESULT_DROP = "";


  public void populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap(
      Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap,
      Set<String> experimentIndicatorExpressionIdSet) {
    if (Objects.isNull(kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap)) {
      return;
    }
    if (Objects.isNull(experimentIndicatorExpressionIdSet) || experimentIndicatorExpressionIdSet.isEmpty()) {
      return;
    }
    experimentIndicatorExpressionItemRsService.lambdaQuery()
        .in(ExperimentIndicatorExpressionItemRsEntity::getIndicatorExpressionItemId)
        .list()
        .forEach(experimentIndicatorExpressionItemRsEntity -> {
          String experimentIndicatorExpressionId = experimentIndicatorExpressionItemRsEntity.getIndicatorExpressionId();
          List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap.get(experimentIndicatorExpressionId);
          if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList)) {
            experimentIndicatorExpressionItemRsEntityList = new ArrayList<>();
          }
          experimentIndicatorExpressionItemRsEntityList.add(experimentIndicatorExpressionItemRsEntity);
          kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap.put(experimentIndicatorExpressionId, experimentIndicatorExpressionItemRsEntityList);
        });
  }

  public void populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap(
      Map<String, Map<String, Boolean>> kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap,
      Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap,
      Map<String, Map<String, String>> kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
  ) {
    if (Objects.isNull(kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap)
        || Objects.isNull(kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap)
        || Objects.isNull(kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap)
        || Objects.isNull(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
    ) {
      return;
    }
    kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap.forEach((experimentPersonId, kExperimentIndicatorExpressionIdVAtomicBooleanMap) -> {
      if (Objects.isNull(kExperimentIndicatorExpressionIdVAtomicBooleanMap) || kExperimentIndicatorExpressionIdVAtomicBooleanMap.isEmpty()) {
        return;
      }
      kExperimentIndicatorExpressionIdVAtomicBooleanMap.forEach((experimentIndicatorExpressionId, resultBoolean) -> {
        List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap.get(experimentIndicatorExpressionId);
        if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList) || experimentIndicatorExpressionItemRsEntityList.isEmpty()) {
          resultBoolean = Boolean.FALSE;
        } else {
          ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity = experimentIndicatorExpressionItemRsEntityList.get(0);
          String conditionExpression = experimentIndicatorExpressionItemRsEntity.getConditionExpression();
          String conditionNameList = experimentIndicatorExpressionItemRsEntity.getConditionNameList();
          String conditionValList = experimentIndicatorExpressionItemRsEntity.getConditionValList();
          if (StringUtils.isAllBlank(conditionExpression, conditionNameList, conditionValList)) {
            resultBoolean = Boolean.FALSE;
          } else {
            try {
              checkConditionNameAndValSize(conditionNameList, conditionValList);
              List<String> conditionNameSplitList = getConditionNameSplitList(conditionNameList);
              List<String> conditionValSplitList = getConditionValSplitList(conditionValList);
              StandardEvaluationContext context = new StandardEvaluationContext();
              boolean needParse = true;
              for (int i = 0; i <= conditionNameSplitList.size() - 1; i++) {
                String indicatorInstanceId = conditionValSplitList.get(i);
                Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceId = kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(experimentPersonId);
                if (Objects.isNull(kIndicatorInstanceIdVExperimentIndicatorInstanceId) || Objects.isNull(kIndicatorInstanceIdVExperimentIndicatorInstanceId.get(indicatorInstanceId))) {
                  log.error("experimentPersonId:{}, indicatorInstanceId:{} has no experimentIndicatorInstanceId", experimentPersonId, indicatorInstanceId);
                  resultBoolean = Boolean.FALSE;
                  needParse = false;
                  break;
                } else {
                  String experimentIndicatorInstanceId = kIndicatorInstanceIdVExperimentIndicatorInstanceId.get(indicatorInstanceId);
                  ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
                  if (Objects.isNull(experimentIndicatorValRsEntity)) {
                    resultBoolean = Boolean.FALSE;
                    needParse = false;
                    break;
                  } else {
                    String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
                    boolean isValDigital = NumberUtils.isCreatable(currentVal);
                    if (isValDigital) {
                      context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)));
                    } else {
                      context.setVariable(conditionNameSplitList.get(i), currentVal);
                    }
                  }
                }
              }
              if (!needParse) {
                return;
              }
              ExpressionParser parser = new SpelExpressionParser();
              Expression expression = parser.parseExpression(conditionExpression);
              Boolean conditionResult = expression.getValue(context, Boolean.class);
              resultBoolean = Boolean.TRUE.equals(conditionResult);
            } catch (Throwable throwable) {
              log.error("experimentIndicatorExpressionId:{}, checkConditionNameAndValSize failed", experimentIndicatorExpressionId);
              resultBoolean = Boolean.FALSE;
            }
          }
        }
        kExperimentIndicatorExpressionIdVAtomicBooleanMap.put(experimentIndicatorExpressionId, resultBoolean);
        kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap.put(experimentPersonId, kExperimentIndicatorExpressionIdVAtomicBooleanMap);
      });
    });
  }

  private static String wrapStrWithDoubleSingleQuotes(String str) {
    return EnumString.SINGLE_QUOTES.getStr() +
        str +
        EnumString.SINGLE_QUOTES.getStr();
  }

  private EnumIndicatorExpressionField checkField(Integer field) {
    EnumIndicatorExpressionField enumIndicatorExpressionField = EnumIndicatorExpressionField.getByField(field);
    if (Objects.isNull(enumIndicatorExpressionField)) {
      log.error("RsIndicatorExpressionBiz.checkField field:{} is illegal", field);
      throw new RsIndicatorExpressionException("检查指标公式-指标公式域只能是数据库、案例库或实验域");
    }
    return enumIndicatorExpressionField;
  }

  private EnumIndicatorExpressionSource checkSource(Integer source) {
    EnumIndicatorExpressionSource enumIndicatorExpressionSource = EnumIndicatorExpressionSource.getBySource(source);
    if (Objects.isNull(enumIndicatorExpressionSource)) {
      log.error("RsIndicatorExpressionBiz.checkSource. 的指标公式来源:{} 不合法", source);
      throw new RsIndicatorExpressionException(String.format("检查指标公式有误，指标公式来源不合法，source:%s", source));
    }
    return enumIndicatorExpressionSource;
  }
  private void checkConditionNameAndValSize(String conditionNameList, String conditionValList) {
    if (StringUtils.isBlank(conditionNameList)) {
      if (StringUtils.isBlank(conditionValList)) {
        /* runsix:right, no condition */
      } else {
        /* runsix:conditionNameList is blank but conditionValList is not blank */
        log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionNameAndValSize conditionNameList is blank but conditionValList is not blank");
        throw new RsIndicatorExpressionException("检查指标公式条件-检查条件参数名列表以及参数值列表有误，条件参数名列表为空，但是条件值列表不为空");
      }
    } else {
      if (StringUtils.isBlank(conditionValList)) {
        /* runsix:conditionNameList is not blank but conditionValList is blank */
        log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionNameAndValSize conditionNameList is not blank but conditionValList is blank");
        throw new RsIndicatorExpressionException("检查指标公式条件-检查条件参数名列表以及参数值列表有误，条件参数名列表为空，但是条件值列表不为空");
      } else {
        List<String> conditionNameSplitList = getConditionNameSplitList(conditionNameList);
        List<String> conditionValSplitList = getConditionValSplitList(conditionValList);
        if (conditionNameSplitList.size() != conditionValSplitList.size()) {
          log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionNameAndValSize conditionNameList size:{}, conditionValList size:{}, is not same", conditionNameSplitList.size(), conditionValSplitList.size());
          throw new RsIndicatorExpressionException("检查指标公式条件-检查条件参数名列表以及参数值列表有误，条件参数名列表与条件值列表不一致");
        }
      }
    }
  }

  private void databaseCheckConditionMustBeBoolean(String conditionExpression, List<String> conditionNameSplitList, List<String> conditionValSplitList) {
    Map<String, String> kIndicatorInstanceIdVValMap = indicatorRuleService.lambdaQuery()
        .in(IndicatorRuleEntity::getVariableId, conditionValSplitList)
        .list()
        .stream().collect(Collectors.toMap(IndicatorRuleEntity::getVariableId, IndicatorRuleEntity::getDef));
    StandardEvaluationContext context = new StandardEvaluationContext();
    for (int i = 0; i <= conditionNameSplitList.size()-1; i++) {
      String indicatorInstanceId = conditionValSplitList.get(i);
      String val = kIndicatorInstanceIdVValMap.get(indicatorInstanceId);
      if (Objects.isNull(val)) {
        log.error("sIndicatorExpressionBiz.checkCondition.databaseCheckConditionMustBeBoolean field database indicatorInstanceId:{} does not exist", indicatorInstanceId);
        throw new RsIndicatorExpressionException(String.format("检查指标公式条件-条件指标id:%s不存在", indicatorInstanceId));
      }
      boolean isValDigital = NumberUtils.isCreatable(val);
      if (isValDigital) {
        context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(val)).setScale(2, RoundingMode.DOWN));
      } else {
        val = wrapStrWithDoubleSingleQuotes(val);
        context.setVariable(conditionNameSplitList.get(i), val);
      }
    }
    ExpressionParser parser = new SpelExpressionParser();
    Expression expression = parser.parseExpression(conditionExpression);
    String conditionExpressionResult = expression.getValue(context, String.class);
    if(!StringUtils.equalsIgnoreCase(conditionExpressionResult, EnumBoolean.TRUE.getCode().toString()) && !StringUtils.equalsIgnoreCase(conditionExpressionResult, EnumBoolean.FALSE.getCode().toString())) {
      log.warn("RsIndicatorExpressionBiz.checkCondition.databaseCheckConditionMustBeBoolean result:{} is not boolean", conditionExpressionResult);
      throw new RsIndicatorExpressionException("检查指标公式条件-条件解析结果不是true或false");
    }
  }
  /* runsix:TODO */
  private void caseCheckConditionMustBeBoolean() {}

  private void checkConditionMustBeBoolean(Integer field, String conditionExpression, String conditionNameList, String conditionValList) {
    /* runsix:condition can be blank */
    if (StringUtils.isBlank(conditionExpression)) {
      return;
    }
    EnumIndicatorExpressionField enumIndicatorExpressionField = checkField(field);
    List<String> conditionNameSplitList = Arrays.stream(conditionNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
    List<String> conditionValSplitList = Arrays.stream(conditionValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
    switch (enumIndicatorExpressionField) {
      case DATABASE -> databaseCheckConditionMustBeBoolean(conditionExpression, conditionNameSplitList, conditionValSplitList);
      case CASE -> caseCheckConditionMustBeBoolean();
      default -> {
        log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionMustBeBoolean field:{} is illegal", field);
        throw new RsIndicatorExpressionException("检查指标公式条件-指标公式域只能是数据库或案例库");
      }
    }
  }

  public boolean checkCondition(RsIndicatorExpressionCheckoutConditionRequest rsIndicatorExpressionCheckoutConditionRequest) {
    boolean checkConditionResult = true;
    Integer source = rsIndicatorExpressionCheckoutConditionRequest.getSource();
    Integer field = rsIndicatorExpressionCheckoutConditionRequest.getField();
    String conditionExpression = rsIndicatorExpressionCheckoutConditionRequest.getConditionExpression();
    String conditionNameList = rsIndicatorExpressionCheckoutConditionRequest.getConditionNameList();
    String conditionValList = rsIndicatorExpressionCheckoutConditionRequest.getConditionValList();
    checkSource(source);
    checkConditionNameAndValSize(conditionNameList, conditionValList);
    checkConditionMustBeBoolean(field, conditionExpression, conditionNameList, conditionValList);
    return checkConditionResult;
  }

  private static void checkResultNameAndValSize(String resultNameList, String resultValList) {
    if (StringUtils.isBlank(resultNameList)) {
      if (StringUtils.isBlank(resultValList)) {
        /* runsix:right, no result */
      } else {
        /* runsix:resultNameList is blank but resultValList is not blank */
        log.error("RsIndicatorExpressionBiz.checkResult.checkResultNameAndValSize resultNameList is blank but resultValList is not blank");
        throw new RsIndicatorExpressionException("检查指标公式结果-检查结果参数名列表以及参数值列表有误，结果参数名列表为空，但是结果值列表不为空");
      }
    } else {
      if (StringUtils.isBlank(resultValList)) {
        /* runsix:resultNameList is not blank but resultValList is blank */
        log.error("RsIndicatorExpressionBiz.checkResult.checkResultNameAndValSize resultNameList is not blank but resultValList is blank");
        throw new RsIndicatorExpressionException("检查指标公式结果-检查结果参数名列表以及参数值列表有误，结果参数名列表为空，但是结果值列表不为空");
      } else {
        String[] resultNameArray = resultNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr());
        String[] resultValArray = resultValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr());
        if (resultNameArray.length != resultValArray.length) {
          log.error("RsIndicatorExpressionBiz.checkResult.checkResultNameAndValSize resultNameList size:{}, resultValList size:{}, is not same", resultNameArray.length, resultValArray.length);
          throw new RsIndicatorExpressionException("检查指标公式结果-检查结果参数名列表以及参数值列表有误，结果参数名列表与结果值列表不一致");
        }
      }
    }
  }

  private void checkResultCannotExistJudgeOperator(String resultExpression) {
    if (Objects.nonNull(resultExpression) &&
        EnumIndicatorExpressionOperator.kJudgeOperatorVEnumIndicatorExpressionOperatorMap.keySet().stream().anyMatch(resultExpression::contains)) {
      log.error("RsIndicatorExpressionBiz.checkCondition.checkResultCannotExistJudgeOperator resultExpression contains judgeOperator");
      throw new RsIndicatorExpressionException("检查指标公式结果-指标公式结果不能包含比较运算符");
    }
  }

  private void databaseCheckResultParse(String resultExpression, List<String> resultNameSplitList, List<String> resultValSplitList) {
    Map<String, String> kIndicatorInstanceIdVValMap = indicatorRuleService.lambdaQuery()
        .in(IndicatorRuleEntity::getVariableId, resultValSplitList)
        .list()
        .stream().collect(Collectors.toMap(IndicatorRuleEntity::getVariableId, IndicatorRuleEntity::getDef));
    StandardEvaluationContext context = new StandardEvaluationContext();
    for (int i = 0; i <= resultNameSplitList.size()-1; i++) {
      String indicatorInstanceId = resultValSplitList.get(i);
      String val = kIndicatorInstanceIdVValMap.get(indicatorInstanceId);
      if (Objects.isNull(val)) {
        log.error("RsIndicatorExpressionBiz.checkResult.databaseCheckResultMustBeBoolean field database indicatorInstanceId:{} does not exist", indicatorInstanceId);
        throw new RsIndicatorExpressionException(String.format("检查指标公式结果-结果指标id：%s 不存在", indicatorInstanceId));
      }
      boolean isValDigital = NumberUtils.isCreatable(val);
      if (isValDigital) {
        context.setVariable(resultNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(val)).setScale(2, RoundingMode.DOWN));
      } else {
        context.setVariable(resultNameSplitList.get(i), val);
      }
    }
    try {
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(resultExpression);
      expression.getValue(context, String.class);
    } catch (ParseException parseException) {
      log.warn("RsIndicatorExpressionBiz.databaseCheckResultParse resultExpression:{} parser.parseExpression(resultExpression) throw exception", resultExpression);
      throw new RsIndicatorExpressionException(String.format("检查结果公式结果-解析结果表达式异常 expression:%s", resultExpression));
    } catch (EvaluationException evaluationException) {
      log.warn("RsIndicatorExpressionBiz.databaseCheckResultParse context:{} expression.getValue(context, String.class) throw exception", context);
      throw new RsIndicatorExpressionException(String.format("检查结果公式结果-获取结果值异常，expression:%s，context:%s", resultExpression, context));
    } catch (Throwable throwable) {
      log.warn("RsIndicatorExpressionBiz.databaseCheckResultParse throw unknown exception, expression:{}, context:{}, throwable:{}", resultExpression, context, throwable);
      throw new RsIndicatorExpressionException(String.format("检查结果公式结果-未知异常，expression:%s，context:%s", resultExpression, context));
    }
  }
  /* runsix:TODO */
  private void caseCheckResultParse() {}

  private void checkResultParse(Integer field, String resultExpression, String resultNameList, String resultValList) {
    /* runsix:result can be blank */
    if (StringUtils.isBlank(resultExpression)) {
      return;
    }
    EnumIndicatorExpressionField enumIndicatorExpressionField = checkField(field);
    List<String> resultNameSplitList = Arrays.stream(resultNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
    List<String> resultValSplitList = Arrays.stream(resultValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
    switch (enumIndicatorExpressionField) {
      case DATABASE -> databaseCheckResultParse(resultExpression, resultNameSplitList, resultValSplitList);
      case CASE -> caseCheckResultParse();
      default -> {
        log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionMustBeBoolean field:{} is illegal", field);
        throw new RsIndicatorExpressionException(String.format("检查指标公式条件-指标公式域只能是数据库或案例库，field:%s", field));
      }
    }
  }

  public boolean checkResult(RsIndicatorExpressionCheckoutResultRequest rsIndicatorExpressionCheckoutResultRequest) {
    boolean checkConditionResult = true;
    Integer field = rsIndicatorExpressionCheckoutResultRequest.getField();
    String resultExpression = rsIndicatorExpressionCheckoutResultRequest.getResultExpression();
    String resultNameList = rsIndicatorExpressionCheckoutResultRequest.getResultNameList();
    String resultValList = rsIndicatorExpressionCheckoutResultRequest.getResultValList();
    checkResultNameAndValSize(resultNameList, resultValList);
    checkResultCannotExistJudgeOperator(resultExpression);
    checkResultParse(field, resultExpression, resultNameList, resultValList);
    return checkConditionResult;
  }

  /* runsix:indicatorExpression is just a condition  */
  private void ePIEConditionType(
      Map<String, Map<String, Boolean>> kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap,
      Integer period
  ) throws ExecutionException, InterruptedException {
    if (Objects.isNull(kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap) || kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap.isEmpty()) {
      return;
    }
    Set<String> experimentPersonIdSet = new HashSet<>();
    Set<String> experimentIndicatorExpressionIdSet = new HashSet<>();
    kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap.forEach((experimentPersonId, kExperimentIndicatorExpressionIdVResultBooleanMap) -> {
      experimentPersonIdSet.add(experimentPersonId);
      kExperimentIndicatorExpressionIdVResultBooleanMap.forEach((experimentIndicatorExpressionId, resultBoolean) -> {
        experimentIndicatorExpressionIdSet.add(experimentIndicatorExpressionId);
      });
    });

    Map<String, Map<String, String>> kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
    CompletableFuture<Void> populateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMapByExperimentPersonIdSetCF = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorInstanceBiz.populateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMapByExperimentPersonIdSet(
          kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap, experimentPersonIdSet);
    });
    populateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMapByExperimentPersonIdSetCF.get();

    Set<String> experimentIndicatorInstanceIdSet = new HashSet<>();
    kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.values().forEach(IndicatorInstanceIdVExperimentIndicatorInstanceIdMap -> {
      if (Objects.nonNull(IndicatorInstanceIdVExperimentIndicatorInstanceIdMap)) {
        IndicatorInstanceIdVExperimentIndicatorInstanceIdMap.values().stream().filter(StringUtils::isNotBlank).forEach(experimentIndicatorInstanceIdSet::add);
      }
    });
    Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap = new HashMap<>();
    CompletableFuture<Void> populateKExperimentIndicatorInstanceIdVExperimentIndicatorValMapCF = CompletableFuture.runAsync(() -> {
      rsExperimentIndicatorInstanceBiz.populateKExperimentIndicatorInstanceIdVExperimentIndicatorValMap(
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, period, experimentIndicatorInstanceIdSet);
    });
    populateKExperimentIndicatorInstanceIdVExperimentIndicatorValMapCF.get();

    Map<String, List<ExperimentIndicatorExpressionItemRsEntity>> kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap = new HashMap<>();
    CompletableFuture<Void> populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMapCF = CompletableFuture.runAsync(() -> {
      populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap(kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap, experimentIndicatorExpressionIdSet);
    });
    populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMapCF.get();

    CompletableFuture<Void> populateKExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMapCF = CompletableFuture.runAsync(() -> {
      populateKExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap(
          kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap,
          kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap,
          kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
      );
    });
    populateKExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMapCF.get();
  }

  private String ePIEResultUsingExperimentIndicatorInstanceId(
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
  ) {
    try {
      String resultExpression = experimentIndicatorExpressionItemRsEntity.getResultExpression();
      String resultNameList = experimentIndicatorExpressionItemRsEntity.getResultNameList();
      List<String> resultNameSplitList = getResultNameSplitList(resultNameList);
      String resultValList = experimentIndicatorExpressionItemRsEntity.getResultValList();
      List<String> resultValSplitList = getResultValSplitList(resultValList);
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(resultExpression);
      if (StringUtils.isBlank(resultExpression)) {
        return RESULT_DROP;
      }
      for (int i = 0; i <= resultNameSplitList.size() - 1; i++) {
        String experimentIndicatorInstanceId = resultValSplitList.get(i);
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorValRsEntity) || StringUtils.isBlank(experimentIndicatorValRsEntity.getCurrentVal())) {
          return RESULT_DROP;
        }
        String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(resultNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(resultNameSplitList.get(i), currentVal);
        }
      }
      return expression.getValue(context, String.class);
    } catch(Exception e) {
      log.error("RsIndicatorExpressionBiz.ePIEResultUsingExperimentIndicatorInstanceId", e);
      return RESULT_DROP;
    }
  }

  private String ePIEResultUsingIndicatorInstanceId(
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap
  ) {
    try {
      String resultExpression = experimentIndicatorExpressionItemRsEntity.getResultExpression();
      String resultNameList = experimentIndicatorExpressionItemRsEntity.getResultNameList();
      List<String> resultNameSplitList = getResultNameSplitList(resultNameList);
      String resultValList = experimentIndicatorExpressionItemRsEntity.getResultValList();
      List<String> resultValSplitList = getResultValSplitList(resultValList);
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(resultExpression);
      if (StringUtils.isBlank(resultExpression)) {
        return RESULT_DROP;
      }
      for (int i = 0; i <= resultNameSplitList.size() - 1; i++) {
        String indicatorInstanceId = resultValSplitList.get(i);
        String experimentIndicatorInstanceId = kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
        if (StringUtils.isBlank(experimentIndicatorInstanceId)) {
          return RESULT_DROP;
        }
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorValRsEntity) || StringUtils.isBlank(experimentIndicatorValRsEntity.getCurrentVal())) {
          return RESULT_DROP;
        }
        String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(resultNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(resultNameSplitList.get(i), currentVal);
        }
      }
      return expression.getValue(context, String.class);
    } catch(Exception e) {
      log.error("RsIndicatorExpressionBiz.ePIEResultUsingExperimentIndicatorInstanceId", e);
      return RESULT_DROP;
    }
  }

  private boolean ePIEConditionUsingExperimentIndicatorInstanceId(
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap
      ) {
    try {
      String conditionExpression = experimentIndicatorExpressionItemRsEntity.getConditionExpression();
      String conditionNameList = experimentIndicatorExpressionItemRsEntity.getConditionNameList();
      List<String> conditionNameSplitList = getConditionNameSplitList(conditionNameList);
      String conditionValList = experimentIndicatorExpressionItemRsEntity.getConditionValList();
      List<String> conditionValSplitList = getConditionValSplitList(conditionValList);
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(conditionExpression);
      if (StringUtils.isBlank(conditionExpression)) {
        return true;
      }
      for (int i = 0; i <= conditionNameSplitList.size() - 1; i++) {
        String experimentIndicatorInstanceId = conditionValSplitList.get(i);
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorValRsEntity) || StringUtils.isBlank(experimentIndicatorValRsEntity.getCurrentVal())) {
          return false;
        }
        String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(conditionNameSplitList.get(i), currentVal);
        }
      }
      return Boolean.TRUE.equals(expression.getValue(context, Boolean.class));
    } catch(Exception e) {
      log.error("RsIndicatorExpressionBiz.ePIEConditionUsingExperimentIndicatorInstanceId", e);
      return false;
    }
  }

  private boolean ePIEConditionUsingIndicatorInstanceId(
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity,
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap
  ) {
    try {
      String conditionExpression = experimentIndicatorExpressionItemRsEntity.getConditionExpression();
      String conditionNameList = experimentIndicatorExpressionItemRsEntity.getConditionNameList();
      List<String> conditionNameSplitList = getConditionNameSplitList(conditionNameList);
      String conditionValList = experimentIndicatorExpressionItemRsEntity.getConditionValList();
      List<String> conditionValSplitList = getConditionValSplitList(conditionValList);
      StandardEvaluationContext context = new StandardEvaluationContext();
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(conditionExpression);
      if (StringUtils.isBlank(conditionExpression)) {
        return true;
      }
      for (int i = 0; i <= conditionNameSplitList.size() - 1; i++) {
        String indicatorInstanceId = conditionValSplitList.get(i);
        String experimentIndicatorInstanceId = kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(indicatorInstanceId);
        if (StringUtils.isBlank(experimentIndicatorInstanceId)) {
          return false;
        }
        ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.get(experimentIndicatorInstanceId);
        if (Objects.isNull(experimentIndicatorValRsEntity) || StringUtils.isBlank(experimentIndicatorValRsEntity.getCurrentVal())) {
          return false;
        }
        String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
        boolean isValDigital = NumberUtils.isCreatable(currentVal);
        if (isValDigital) {
          context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)).setScale(2, RoundingMode.DOWN));
        } else {
          context.setVariable(conditionNameSplitList.get(i), currentVal);
        }
      }
      return Boolean.TRUE.equals(expression.getValue(context, Boolean.class));
    } catch(Exception e) {
      log.error("RsIndicatorExpressionBiz.ePIEConditionUsingIndicatorInstanceId", e);
      return false;
    }
  }

  private void handleParsedResult(
      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity,
      ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity,
      ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity
  ) {
    Integer source = experimentIndicatorExpressionRsEntity.getSource();

  }

  /**
   * runsix method process
   * 实验解析指标公式-解析ExperimentIndicatorExpressionItemRsEntity单条结果
   * 1.按顺序解析每一个公式
   * 2.如果条件不满足，不解析结果，继续下一个
   * 3.如果一个公式有结果就跳出
   * 4.处理解析后的结果
  */
  private void ePIEResultUsingExperimentIndicatorInstanceIdCombine(
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      ExperimentIndicatorExpressionRsEntity experimentIndicatorExpressionRsEntity,
      List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList,
      ExperimentIndicatorExpressionItemRsEntity minExperimentIndicatorExpressionItemRsEntity,
      ExperimentIndicatorExpressionItemRsEntity maxExperimentIndicatorExpressionItemRsEntity
      ) {
    AtomicReference<String> resultAtomicReference = new AtomicReference<>();
    /* runsix:1.按顺序解析每一个公式 */
    experimentIndicatorExpressionItemRsEntityList.sort(Comparator.comparingInt(ExperimentIndicatorExpressionItemRsEntity::getSeq));
    for (int i = 0; i <= experimentIndicatorExpressionItemRsEntityList.size()-1; i++) {
      ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity = experimentIndicatorExpressionItemRsEntityList.get(i);

      boolean parsedCondition = ePIEConditionUsingExperimentIndicatorInstanceId(experimentIndicatorExpressionItemRsEntity, kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap);
      /* runsix:2.如果条件不满足，不解析结果，继续下一个 */
      if (!parsedCondition) {
        continue;
      }

      /* runsix:3.如果一个公式有结果就跳出 */
      String parsedResult = ePIEResultUsingExperimentIndicatorInstanceId(experimentIndicatorExpressionItemRsEntity, kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap);
      if (RESULT_DROP.equals(parsedResult)) {
        continue;
      }

      /* runsix:4.处理解析后的结果 */
      handleParsedResult(
          experimentIndicatorExpressionRsEntity, minExperimentIndicatorExpressionItemRsEntity, maxExperimentIndicatorExpressionItemRsEntity
      );
      break;
    }

  }

  /* runsix:TODO  */
  private void ePIEIndicatorManagement(Map<ExperimentIndicatorExpressionRsEntity, AtomicReference> kExperimentIndicatorExpressionRsEntityVAtomicReferenceMap) {
    if (Objects.isNull(kExperimentIndicatorExpressionRsEntityVAtomicReferenceMap) || kExperimentIndicatorExpressionRsEntityVAtomicReferenceMap.isEmpty()) {
      return;
    }
    Set<ExperimentIndicatorExpressionRsEntity> experimentIndicatorExpressionRsEntitySet = kExperimentIndicatorExpressionRsEntityVAtomicReferenceMap.keySet();

  }

  /* runsix:TODO 这个可以留到下个版本做 */
  private void ePIEIndicatorJudgeRiskFactor() {
  }

  /* runsix:now it is just a condition */
  private void ePIECrowds(
      Map<String, Map<String, Boolean>> kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap,
      Integer period
  ) throws ExecutionException, InterruptedException {
    ePIEConditionType(kExperimentPersonIdVKExperimentIndicatorExpressionIdVResultBooleanMap, period);
  }

  /* runsix:TODO  */
  private void databaseParseIndicatorExpression() {

  }

  /* runsix:TODO  */
  private void caseParseIndicatorExpression() {

  }

  /* runsix:TODO  */
  private void experimentParseIndicatorExpression(Integer source) {
    EnumIndicatorExpressionSource enumIndicatorExpressionSource = checkSource(source);
    switch (enumIndicatorExpressionSource) {
      case INDICATOR_MANAGEMENT -> ePIEIndicatorManagement(new HashMap<>());
      case INDICATOR_JUDGE_RISK_FACTOR -> ePIEIndicatorJudgeRiskFactor();
//      case CROWDS -> ePIECrowds();
    }
  }



  public void parseIndicatorExpression(Integer field, Integer source) {
    EnumIndicatorExpressionField enumIndicatorExpressionField = checkField(field);
    switch (enumIndicatorExpressionField) {
      case DATABASE -> databaseParseIndicatorExpression();
      case CASE -> caseParseIndicatorExpression();
      case EXPERIMENT -> experimentParseIndicatorExpression(source);
      default -> {
        log.error("RsIndicatorExpressionBiz.parseIndicatorExpression field:{} is illegal", field);
        throw new RsIndicatorExpressionException(String.format("解析公式-公式域只能是数据库、案例库或实验域，field:%s", field));
      }
    }
  }

  private List<String> getConditionNameSplitList(String conditionNameList) {
    if (StringUtils.isBlank(conditionNameList)) {
      return new ArrayList<>();
    }
    return Arrays.stream(conditionNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
  }

  private List<String> getConditionValSplitList(String conditionValList) {
    if (StringUtils.isBlank(conditionValList)) {
      return new ArrayList<>();
    }
    return Arrays.stream(conditionValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
  }

  private List<String> getResultNameSplitList(String resultNameList) {
    if (StringUtils.isBlank(resultNameList)) {
      return new ArrayList<>();
    }
    return Arrays.stream(resultNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
  }

  private List<String> getResultValSplitList(String resultValList) {
    if (StringUtils.isBlank(resultValList)) {
      return new ArrayList<>();
    }
    return Arrays.stream(resultValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
  }
}
