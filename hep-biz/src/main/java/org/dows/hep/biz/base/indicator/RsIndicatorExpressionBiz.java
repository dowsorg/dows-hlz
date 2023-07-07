package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dows.hep.api.base.indicator.request.RsIndicatorExpressionCheckoutConditionRequest;
import org.dows.hep.api.base.indicator.request.RsIndicatorExpressionCheckoutResultRequest;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.IndicatorExpressionException;
import org.dows.hep.api.exception.RsIndicatorExpressionException;
import org.dows.hep.entity.IndicatorRuleEntity;
import org.dows.hep.service.IndicatorRuleService;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.*;
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
  private static void checkConditionSource(Integer source) {
    EnumIndicatorExpressionSource enumIndicatorExpressionSource = EnumIndicatorExpressionSource.getBySource(source);
    if (Objects.isNull(enumIndicatorExpressionSource)) {
      log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionSource 的指标公式来源:{} 不合法", source);
      throw new RsIndicatorExpressionException("检查指标公式条件有误，指标公式来源不合法");
    }
  }
  private static void checkConditionNameAndVal(String conditionNameList, String conditionValList) {
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
        String[] conditionNameArray = conditionNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr());
        String[] conditionValArray = conditionValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr());
        if (conditionNameArray.length != conditionValArray.length) {
          log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionNameAndValSize conditionNameList size:{}, conditionValList size:{}, is not same", conditionNameArray.length, conditionValArray.length);
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
        context.setVariable(conditionNameSplitList.get(i), Double.parseDouble(val));
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
    EnumIndicatorExpressionField enumIndicatorExpressionField = EnumIndicatorExpressionField.getByField(field);
    if (Objects.isNull(enumIndicatorExpressionField)) {
      log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionMustBeBoolean field:{} is illegal", field);
      throw new RsIndicatorExpressionException("检查指标公式条件-指标公式域只能是数据库或案例库");
    }
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
    checkConditionSource(source);
    checkConditionNameAndVal(conditionNameList, conditionValList);
    checkConditionMustBeBoolean(field, conditionExpression, conditionNameList, conditionValList);
    return checkConditionResult;
  }

  public boolean checkResult(RsIndicatorExpressionCheckoutResultRequest rsIndicatorExpressionCheckoutResultRequest) {
    boolean checkConditionResult = true;

    return checkConditionResult;
  }
}
