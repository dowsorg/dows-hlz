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
import java.util.concurrent.atomic.AtomicBoolean;
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
        List<String> conditionNameSplitList = getConditionNameSplitListByConditionNameList(conditionNameList);
        List<String> conditionValSplitList = getConditionValArrayByConditionValList(conditionValList);
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
//  private void epIEConditionList(
//      Map<String, Map<String, AtomicBoolean>> kExperimentPersonIdVKExperimentIndicatorExpressionIdVAtomicBooleanMap
//  ) {
//    if (Objects.isNull(kExperimentPersonIdVKExperimentIndicatorExpressionIdVAtomicBooleanMap) || kExperimentPersonIdVKExperimentIndicatorExpressionIdVAtomicBooleanMap.isEmpty()) {
//      return;
//    }
//    kExperimentPersonIdVKExperimentIndicatorExpressionIdVAtomicBooleanMap.forEach((experimentPersonId, kExperimentIndicatorExpressionIdVAtomicBooleanMap) -> {
//      if (Objects.isNull(kExperimentIndicatorExpressionIdVAtomicBooleanMap) || kExperimentIndicatorExpressionIdVAtomicBooleanMap.isEmpty()) {
//        return;
//      }
//      kExperimentIndicatorExpressionIdVAtomicBooleanMap.forEach((experimentIndicatorExpressionId, atomicBoolean) -> {
//        List<ExperimentIndicatorExpressionItemRsEntity> experimentIndicatorExpressionItemRsEntityList = kExperimentIndicatorExpressionIdVExperimentIndicatorExpressionItemListMap.get(experimentIndicatorExpressionId);
//        if (Objects.isNull(experimentIndicatorExpressionItemRsEntityList) || experimentIndicatorExpressionItemRsEntityList.isEmpty()) {
//          atomicBoolean.set(Boolean.FALSE);
//        } else {
//          ExperimentIndicatorExpressionItemRsEntity experimentIndicatorExpressionItemRsEntity = experimentIndicatorExpressionItemRsEntityList.get(0);
//          String conditionExpression = experimentIndicatorExpressionItemRsEntity.getConditionExpression();
//          String conditionNameList = experimentIndicatorExpressionItemRsEntity.getConditionNameList();
//          String conditionValList = experimentIndicatorExpressionItemRsEntity.getConditionValList();
//          if (StringUtils.isAllBlank(conditionExpression, conditionNameList, conditionValList)) {
//            atomicBoolean.set(Boolean.FALSE);
//          } else {
//            try {
//              checkConditionNameAndValSize(conditionNameList, conditionValList);
//              List<String> conditionNameSplitList = getConditionNameSplitListByConditionNameList(conditionNameList);
//              List<String> conditionValSplitList = getConditionValArrayByConditionValList(conditionValList);
//              StandardEvaluationContext context = new StandardEvaluationContext();
//              for (int i = 0; i <= conditionNameSplitList.size()-1; i++) {
//                String indicatorInstanceId = conditionValSplitList.get(i);
//                Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceId = kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(experimentPersonId);
//                if (Objects.isNull(kIndicatorInstanceIdVExperimentIndicatorInstanceId) || Objects.isNull(kIndicatorInstanceIdVExperimentIndicatorInstanceId.get(indicatorInstanceId))) {
//                  log.error("experimentPersonId:{}, indicatorInstanceId:{} has no experimentIndicatorInstanceId", experimentPersonId, indicatorInstanceId);
//                  atomicBoolean.set(Boolean.FALSE);
//                  break;
//                } else {
//                  String experimentIndicatorInstanceId = kIndicatorInstanceIdVExperimentIndicatorInstanceId.get(indicatorInstanceId);
//                  ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = kExperimentIndicatorInstanceIdVExperimentIndicatorValMap.get(experimentIndicatorInstanceId);
//                  if (Objects.isNull(experimentIndicatorValRsEntity)) {
//                    atomicBoolean.set(Boolean.FALSE);
//                    break;
//                  } else {
//                    String currentVal = experimentIndicatorValRsEntity.getCurrentVal();
//                    boolean isValDigital = NumberUtils.isCreatable(currentVal);
//                    if (isValDigital) {
//                      context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(currentVal)));
//                    } else {
//                      context.setVariable(conditionNameSplitList.get(i), currentVal);
//                    }
//                  }
//                }
//              }
//              ExpressionParser parser = new SpelExpressionParser();
//              Expression expression = parser.parseExpression(conditionExpression);
//              Boolean conditionResult = expression.getValue(context, Boolean.class);
//              atomicBoolean.set(Boolean.TRUE.equals(conditionResult));
//            } catch (Throwable throwable) {
//              log.error("experimentIndicatorExpressionId:{}, checkConditionNameAndValSize failed", experimentIndicatorExpressionId);
//              atomicBoolean.set(Boolean.FALSE);
//            }
//          }
//        }
//        kExperimentIndicatorExpressionIdVAtomicBooleanMap.put(experimentIndicatorExpressionId, atomicBoolean);
//        kExperimentPersonIdVKExperimentIndicatorExpressionIdVAtomicBooleanMap.put(experimentPersonId, kExperimentIndicatorExpressionIdVAtomicBooleanMap);
//      });
//    });
//  }

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
  private void ePIECrowds() {
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
      case CROWDS -> ePIECrowds();
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

  private List<String> getConditionNameSplitListByConditionNameList(String conditionNameList) {
    if (StringUtils.isBlank(conditionNameList)) {
      return new ArrayList<>();
    }
    return Arrays.stream(conditionNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
  }

  private List<String> getConditionValArrayByConditionValList(String conditionValList) {
    if (StringUtils.isBlank(conditionValList)) {
      return new ArrayList<>();
    }
    return Arrays.stream(conditionValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
  }
}
