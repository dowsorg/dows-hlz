package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dows.hep.api.base.indicator.request.RsIndicatorExpressionCheckConditionRequest;
import org.dows.hep.api.base.indicator.request.RsIndicatorExpressionCheckoutResultRequest;
import org.dows.hep.api.base.indicator.response.CaseIndicatorExpressionItemResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionItemResponseRs;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.RsExperimentIndicatorExpressionBizException;
import org.dows.hep.api.exception.RsIndicatorExpressionException;
import org.dows.hep.api.exception.RsUtilBizException;
import org.dows.sequence.api.IdGenerator;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsUtilBiz {
  private final IdGenerator idGenerator;
  public static final String RESULT_DROP = "";

  public String getCommaList(Collection<String> collection) {
    return String.join(EnumString.COMMA.getStr(), collection);
  }
  public void calculateRiskModelScore(AtomicReference<BigDecimal> sumRiskModeScoreAR, Map<String, BigDecimal> kPrincipalIdVScoreMap, AtomicReference<BigDecimal> minScoreAR, AtomicReference<BigDecimal> maxScoreAR) {
    if (Objects.isNull(sumRiskModeScoreAR) || Objects.isNull(kPrincipalIdVScoreMap) || Objects.isNull(minScoreAR) || Objects.isNull(maxScoreAR)
    ) {return;}
    BigDecimal minScore = minScoreAR.get();
    BigDecimal maxScore = maxScoreAR.get();
    if (kPrincipalIdVScoreMap.size() == 1) {
      /* runsix:do nothing */
    } else {
      List<BigDecimal> leOneList = new ArrayList<>();
      List<BigDecimal> gtOneList = new ArrayList<>();
      kPrincipalIdVScoreMap.values().forEach(score -> {
        if (score.compareTo(BigDecimal.ONE) <= 0) {
          leOneList.add(score);
        } else {
          gtOneList.add(score);
        }
      });
      BigDecimal multiplyResult = BigDecimal.ZERO;
      if (!leOneList.isEmpty()) {
        multiplyResult =  leOneList.stream().reduce(BigDecimal.ONE, BigDecimal::multiply);
      }
      BigDecimal addResult = gtOneList.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
      sumRiskModeScoreAR.set(addResult.add(multiplyResult).setScale(2, RoundingMode.DOWN));
    }
    BigDecimal beforeMinAndMax = sumRiskModeScoreAR.get();
    if (minScore.compareTo(maxScore) == 0) {sumRiskModeScoreAR.set(BigDecimal.ZERO); return;}
    sumRiskModeScoreAR.set(BigDecimal.valueOf(100).multiply(beforeMinAndMax.subtract(maxScore)).divide(minScore.subtract(maxScore), 2, RoundingMode.DOWN));
  }

  public BigDecimal newCalculateFinalHealthScore(
      Map<String, BigDecimal> kRiskModelIdVTotalScoreMap,
      Map<String, Integer> kRiskModelIdVRiskDeathProbabilityMap,
      Integer totalRiskDeathProbability
  ) {
    AtomicReference<BigDecimal> finalScoreBigDecimal = new AtomicReference<>(BigDecimal.ZERO);
    kRiskModelIdVTotalScoreMap.forEach((riskModelId, totalScore) -> {
      Integer riskDeathProbability = kRiskModelIdVRiskDeathProbabilityMap.get(riskModelId);
      finalScoreBigDecimal.set(
          finalScoreBigDecimal.get().add(
              BigDecimal.valueOf(riskDeathProbability)
                  .divide(BigDecimal.valueOf(totalRiskDeathProbability), 2, RoundingMode.DOWN)
                  .multiply(totalScore)
          ).setScale(2, RoundingMode.DOWN)
      );
    });
    return finalScoreBigDecimal.get();
  }

  public void populateKOldIdVNewIdMap(
      Map<String, String> kOldIdVNewIdMap,
      Set<String> oldIdSet
  ) {
    if (Objects.isNull(kOldIdVNewIdMap)
        || Objects.isNull(oldIdSet) || oldIdSet.isEmpty()
    ) {return;}
    oldIdSet.forEach(oldId -> {
      kOldIdVNewIdMap.put(oldId, idGenerator.nextIdStr());
    });
  }
  public String wrapStrWithDoubleSingleQuotes(String str) {
    return EnumString.SINGLE_QUOTES.getStr() +
        str +
        EnumString.SINGLE_QUOTES.getStr();
  }

  public List<String> getSplitList(String rawList) {
    if (StringUtils.isBlank(rawList)) {
      return new ArrayList<>();
    }
    return Arrays.stream(rawList.split(EnumString.COMMA.getStr())).collect(Collectors.toList());
  }

  public List<String> getConditionNameSplitList(String conditionNameList) {
    if (StringUtils.isBlank(conditionNameList)) {
      return new ArrayList<>();
    }
    return Arrays.stream(conditionNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
  }

  public List<String> getConditionValSplitList(String conditionValList) {
    if (StringUtils.isBlank(conditionValList)) {
      return new ArrayList<>();
    }
    return Arrays.stream(conditionValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
  }

  public List<String> getResultNameSplitList(String resultNameList) {
    if (StringUtils.isBlank(resultNameList)) {
      return new ArrayList<>();
    }
    return Arrays.stream(resultNameList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
  }

  public List<String> getResultValSplitList(String resultValList) {
    if (StringUtils.isBlank(resultValList)) {
      return new ArrayList<>();
    }
    return Arrays.stream(resultValList.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr())).collect(Collectors.toList());
  }

  public EnumIndicatorExpressionField checkField(Integer field) {
    EnumIndicatorExpressionField enumIndicatorExpressionField = EnumIndicatorExpressionField.getByField(field);
    if (Objects.isNull(enumIndicatorExpressionField)) {
      log.error("RsIndicatorExpressionBiz.checkField field:{} is illegal", field);
      throw new RsIndicatorExpressionException("检查指标公式-指标公式域只能是数据库、案例库或实验域");
    }
    return enumIndicatorExpressionField;
  }
  public EnumIndicatorExpressionScene checkScene(Integer scene) {
    EnumIndicatorExpressionScene enumIndicatorExpressionScene = EnumIndicatorExpressionScene.getByScene(scene);
    if (Objects.isNull(enumIndicatorExpressionScene)) {
      log.error("RsIndicatorExpressionBiz.checkScene scene:{} is illegal", scene);
      throw new RsIndicatorExpressionException("检查指标公式-指标公式域只能是数据库、案例库或实验域");
    }
    return enumIndicatorExpressionScene;
  }

  public EnumIndicatorExpressionSource checkSource(Integer source) {
    EnumIndicatorExpressionSource enumIndicatorExpressionSource = EnumIndicatorExpressionSource.getBySource(source);
    if (Objects.isNull(enumIndicatorExpressionSource)) {
      log.error("RsIndicatorExpressionBiz.checkSource. 的指标公式来源:{} 不合法", source);
      throw new RsIndicatorExpressionException(String.format("检查指标公式有误，指标公式来源不合法，source:%s", source));
    }
    return enumIndicatorExpressionSource;
  }

  public void checkConditionNameAndValSize(String conditionNameList, String conditionValList) {
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

  private void databaseCheckConditionMustBeBoolean(Map<String, String> kIndicatorInstanceIdVValMap, String conditionExpression, List<String> conditionNameSplitList, List<String> conditionValSplitList) {
    StandardEvaluationContext context = new StandardEvaluationContext();
    for (int i = 0; i <= conditionNameSplitList.size()-1; i++) {
      String indicatorInstanceId = conditionValSplitList.get(i);
      String val = kIndicatorInstanceIdVValMap.get(indicatorInstanceId);
      if (Objects.isNull(val)) {
        log.error("sIndicatorExpressionBiz.checkCondition.databaseCheckConditionMustBeBoolean field database indicatorInstanceId:{} does not exist", indicatorInstanceId);
        throw new RsIndicatorExpressionException(EnumESC.INDICATOR_EXPRESSION_CHECK_INDICATOR_INSTANCE_ID_DOES_NOT_EXIST);
      }
      boolean isValDigital = NumberUtils.isCreatable(val);
      if (isValDigital) {
        context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(val)).setScale(2, RoundingMode.DOWN));
      } else {
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

  private void caseCheckConditionMustBeBoolean(Map<String, String> kCaseIndicatorInstanceIdVValMap, String conditionExpression, List<String> conditionNameSplitList, List<String> conditionValSplitList) {
    StandardEvaluationContext context = new StandardEvaluationContext();
    for (int i = 0; i <= conditionNameSplitList.size()-1; i++) {
      String indicatorInstanceId = conditionValSplitList.get(i);
      String val = kCaseIndicatorInstanceIdVValMap.get(indicatorInstanceId);
      if (Objects.isNull(val)) {
        log.error("RsIndicatorExpressionBiz.checkCondition.caseCheckConditionMustBeBoolean field case indicatorInstanceId:{} does not exist", indicatorInstanceId);
        throw new RsIndicatorExpressionException(EnumESC.INDICATOR_EXPRESSION_CHECK_INDICATOR_INSTANCE_ID_DOES_NOT_EXIST);
      }
      boolean isValDigital = NumberUtils.isCreatable(val);
      if (isValDigital) {
        context.setVariable(conditionNameSplitList.get(i), BigDecimal.valueOf(Double.parseDouble(val)).setScale(2, RoundingMode.DOWN));
      } else {
        context.setVariable(conditionNameSplitList.get(i), val);
      }
    }
    ExpressionParser parser = new SpelExpressionParser();
    Expression expression = parser.parseExpression(conditionExpression);
    String conditionExpressionResult = expression.getValue(context, String.class);
    if(!StringUtils.equalsIgnoreCase(conditionExpressionResult, EnumBoolean.TRUE.getCode().toString()) && !StringUtils.equalsIgnoreCase(conditionExpressionResult, EnumBoolean.FALSE.getCode().toString())) {
      log.warn("RsIndicatorExpressionBiz.checkCondition.caseCheckConditionMustBeBoolean result:{} is not boolean", conditionExpressionResult);
      throw new RsIndicatorExpressionException("检查指标公式条件-条件解析结果不是true或false");
    }
  }
  private void checkConditionMustBeBoolean(
      Map<String, String> kIndicatorInstanceIdVValMap,
      Integer field, String conditionExpression, String conditionNameList, String conditionValList) {
    try {
      /* runsix:condition can be blank */
      if (StringUtils.isBlank(conditionExpression)) {
        return;
      }
      EnumIndicatorExpressionField enumIndicatorExpressionField = checkField(field);
      List<String> conditionNameSplitList = this.getConditionNameSplitList(conditionNameList);
      List<String> conditionValSplitList = this.getConditionValSplitList(conditionValList);
      switch (enumIndicatorExpressionField) {
        case DATABASE -> databaseCheckConditionMustBeBoolean(kIndicatorInstanceIdVValMap, conditionExpression, conditionNameSplitList, conditionValSplitList);
        case CASE -> caseCheckConditionMustBeBoolean(kIndicatorInstanceIdVValMap, conditionExpression, conditionNameSplitList, conditionValSplitList);
        default -> {
          log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionMustBeBoolean field:{} is illegal", field);
          throw new RsIndicatorExpressionException("检查指标公式条件-指标公式域只能是数据库或案例库");
        }
      }
    } catch (Exception e) {
      throw new RsUtilBizException(EnumESC.INDICATOR_EXPRESSION_FORMAT_IS_ILLEGAL);
    }
  }

  public boolean checkCondition(
      Map<String, String> kIndicatorInstanceIdVValMap,
      RsIndicatorExpressionCheckConditionRequest rsIndicatorExpressionCheckConditionRequest) {
    boolean checkConditionResult = true;
    Integer source = rsIndicatorExpressionCheckConditionRequest.getSource();
    Integer field = rsIndicatorExpressionCheckConditionRequest.getField();
    String conditionExpression = rsIndicatorExpressionCheckConditionRequest.getConditionExpression();
    String conditionNameList = rsIndicatorExpressionCheckConditionRequest.getConditionNameList();
    String conditionValList = rsIndicatorExpressionCheckConditionRequest.getConditionValList();
    this.checkSource(source);
    this.checkConditionNameAndValSize(conditionNameList, conditionValList);
    checkConditionMustBeBoolean(kIndicatorInstanceIdVValMap, field, conditionExpression, conditionNameList, conditionValList);
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

  private void databaseCheckResultParse(Map<String, String> kIndicatorInstanceIdVValMap, String resultExpression, List<String> resultNameSplitList, List<String> resultValSplitList) {
    try {
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
      ExpressionParser parser = new SpelExpressionParser();
      Expression expression = parser.parseExpression(resultExpression);
      expression.getValue(context, String.class);
    } catch (Exception e) {
      throw new RsUtilBizException(EnumESC.INDICATOR_EXPRESSION_FORMAT_IS_ILLEGAL);
    }
  }
  /* runsix:TODO */
  private void caseCheckResultParse() {}

  public String handleResultExpression(String resultExpression) {
    if (StringUtils.isBlank(resultExpression)) {return resultExpression;}
    if (NumberUtils.isCreatable(resultExpression) || resultExpression.contains(EnumString.AT.getStr())) {
      return resultExpression;
    } else {
      return wrapStrWithDoubleSingleQuotes(resultExpression);
    }
  }
  private void checkResultParse(Map<String, String> kIndicatorInstanceIdVValMap, Integer field, String resultExpression, String resultNameList, String resultValList) {
    /* runsix:result can be blank */
    if (StringUtils.isBlank(resultExpression)) {
      return;
    }
    EnumIndicatorExpressionField enumIndicatorExpressionField = this.checkField(field);
    List<String> resultNameSplitList = this.getResultNameSplitList(resultNameList);
    List<String> resultValSplitList = this.getResultValSplitList(resultValList);
    switch (enumIndicatorExpressionField) {
      case DATABASE -> databaseCheckResultParse(kIndicatorInstanceIdVValMap, resultExpression, resultNameSplitList, resultValSplitList);
      case CASE -> caseCheckResultParse();
      default -> {
        log.error("RsIndicatorExpressionBiz.checkCondition.checkConditionMustBeBoolean field:{} is illegal", field);
        throw new RsIndicatorExpressionException(String.format("检查指标公式条件-指标公式域只能是数据库或案例库，field:%s", field));
      }
    }
  }

  public boolean checkResult(Map<String, String> kIndicatorInstanceIdVValMap, RsIndicatorExpressionCheckoutResultRequest rsIndicatorExpressionCheckoutResultRequest) {
    boolean checkConditionResult = true;
    Integer source = rsIndicatorExpressionCheckoutResultRequest.getSource();
    Integer field = rsIndicatorExpressionCheckoutResultRequest.getField();
    String resultRaw = rsIndicatorExpressionCheckoutResultRequest.getResultRaw();
    String resultExpression = rsIndicatorExpressionCheckoutResultRequest.getResultExpression();
    String resultNameList = rsIndicatorExpressionCheckoutResultRequest.getResultNameList();
    String resultValList = rsIndicatorExpressionCheckoutResultRequest.getResultValList();
    EnumIndicatorExpressionSource enumIndicatorExpressionSource = checkSource(source);
    switch (enumIndicatorExpressionSource) {
      case INDICATOR_MANAGEMENT -> {
        if (StringUtils.isAnyBlank(resultRaw, resultExpression)) {
          log.error("RsExperimentIndicatorExpressionBiz.checkResult INDICATOR_MANAGEMENT resultRaw:{}, resultExpression:{} is blank", resultRaw, resultExpression);
          throw new RsExperimentIndicatorExpressionBizException(EnumESC.DATABASE_INDICATOR_MANAGEMENT_RESULT_CANNOT_BE_BLANK);
        }
      }
      case INDICATOR_JUDGE_RISK_FACTOR -> {
        if (StringUtils.isNotBlank(resultRaw) || StringUtils.isNotBlank(resultExpression)) {
          log.error("RsExperimentIndicatorExpressionBiz.checkResult INDICATOR_JUDGE_RISK_FACTOR resultRaw:{}, resultExpression:{} is not blank", resultRaw, resultExpression);
        }
      }
      case LABEL_MANAGEMENT -> {
        if (StringUtils.isNotBlank(resultRaw) || StringUtils.isNotBlank(resultExpression)) {
          log.error("RsExperimentIndicatorExpressionBiz.checkResult LABEL_MANAGEMENT resultRaw:{}, resultExpression:{} is not blank", resultRaw, resultExpression);
        }
      }
      case CROWDS -> {
        if (StringUtils.isNotBlank(resultRaw) || StringUtils.isNotBlank(resultExpression)) {
          log.error("RsExperimentIndicatorExpressionBiz.checkResult CROWDS resultRaw:{}, resultExpression:{} is not blank", resultRaw, resultExpression);
        }
      }
      case RISK_MODEL -> {
        if (StringUtils.isAnyBlank(resultRaw, resultExpression)) {
          log.error("RsExperimentIndicatorExpressionBiz.checkResult RISK_MODEL resultRaw:{}, resultExpression:{} is blank", resultRaw, resultExpression);
          throw new RsExperimentIndicatorExpressionBizException(EnumESC.DATABASE_RISK_MODEL_RESULT_CANNOT_BE_BLANK);
        }
      }
      default -> {
        log.warn("RsExperimentIndicatorExpressionBiz.checkResult 指标公式来源:{} 不合法", source);
      }
    }
    checkResultNameAndValSize(resultNameList, resultValList);
    checkResultCannotExistJudgeOperator(resultExpression);
    checkResultParse(kIndicatorInstanceIdVValMap, field, resultExpression, resultNameList, resultValList);
    return checkConditionResult;
  }

  public void specialHandleIndicatorExpressionItemResponseRsList(List<IndicatorExpressionItemResponseRs> indicatorExpressionItemResponseRsList) {
    if (Objects.isNull(indicatorExpressionItemResponseRsList)) {return;}
    if (indicatorExpressionItemResponseRsList.size() == 0) {
      IndicatorExpressionItemResponseRs indicatorExpressionItemResponseRs0 = new IndicatorExpressionItemResponseRs();
      indicatorExpressionItemResponseRs0.setAppId(EnumString.APP_ID.getStr());
      indicatorExpressionItemResponseRs0.setSeq(-2);
      indicatorExpressionItemResponseRsList.add(indicatorExpressionItemResponseRs0);
      IndicatorExpressionItemResponseRs indicatorExpressionItemResponseRs1 = new IndicatorExpressionItemResponseRs();
      indicatorExpressionItemResponseRs1.setAppId(EnumString.APP_ID.getStr());
      indicatorExpressionItemResponseRs1.setSeq(-1);
      indicatorExpressionItemResponseRsList.add(indicatorExpressionItemResponseRs1);
    } else if (indicatorExpressionItemResponseRsList.size() == 1) {
      IndicatorExpressionItemResponseRs indicatorExpressionItemResponseRs0 = new IndicatorExpressionItemResponseRs();
      indicatorExpressionItemResponseRs0.setAppId(EnumString.APP_ID.getStr());
      IndicatorExpressionItemResponseRs indicatorExpressionItemResponseRs = indicatorExpressionItemResponseRsList.get(0);
      Integer seq = indicatorExpressionItemResponseRs.getSeq();
      if (Integer.MAX_VALUE == seq) {
        indicatorExpressionItemResponseRs0.setSeq(0);
      } else {
        indicatorExpressionItemResponseRs0.setSeq(Integer.MAX_VALUE);
      }
      indicatorExpressionItemResponseRsList.add(indicatorExpressionItemResponseRs0);
      indicatorExpressionItemResponseRsList.sort(Comparator.comparingInt(IndicatorExpressionItemResponseRs::getSeq));
    } else {
      /* runsix:do nothing */
    }
  }

  public void specialHandleCaseIndicatorExpressionItemResponseRsList(List<CaseIndicatorExpressionItemResponseRs> caseIndicatorExpressionItemResponseRsList) {
    if (Objects.isNull(caseIndicatorExpressionItemResponseRsList)) {return;}
    if (caseIndicatorExpressionItemResponseRsList.size() == 0) {
      CaseIndicatorExpressionItemResponseRs caseIndicatorExpressionItemResponseRs0 = new CaseIndicatorExpressionItemResponseRs();
      caseIndicatorExpressionItemResponseRs0.setAppId(EnumString.APP_ID.getStr());
      caseIndicatorExpressionItemResponseRs0.setSeq(-2);
      caseIndicatorExpressionItemResponseRsList.add(caseIndicatorExpressionItemResponseRs0);
      CaseIndicatorExpressionItemResponseRs caseIndicatorExpressionItemResponseRs1 = new CaseIndicatorExpressionItemResponseRs();
      caseIndicatorExpressionItemResponseRs1.setAppId(EnumString.APP_ID.getStr());
      caseIndicatorExpressionItemResponseRs1.setSeq(-1);
      caseIndicatorExpressionItemResponseRsList.add(caseIndicatorExpressionItemResponseRs1);
    } else if (caseIndicatorExpressionItemResponseRsList.size() == 1) {
      CaseIndicatorExpressionItemResponseRs caseIndicatorExpressionItemResponseRs0 = new CaseIndicatorExpressionItemResponseRs();
      caseIndicatorExpressionItemResponseRs0.setAppId(EnumString.APP_ID.getStr());
      CaseIndicatorExpressionItemResponseRs caseIndicatorExpressionItemResponseRs = caseIndicatorExpressionItemResponseRsList.get(0);
      Integer seq = caseIndicatorExpressionItemResponseRs.getSeq();
      if (Integer.MAX_VALUE == seq) {
        caseIndicatorExpressionItemResponseRs0.setSeq(0);
      } else {
        caseIndicatorExpressionItemResponseRs0.setSeq(Integer.MAX_VALUE);
      }
      caseIndicatorExpressionItemResponseRsList.add(caseIndicatorExpressionItemResponseRs0);
      caseIndicatorExpressionItemResponseRsList.sort(Comparator.comparingInt(CaseIndicatorExpressionItemResponseRs::getSeq));
    } else {
      /* runsix:do nothing */
    }
  }

  public void algorithmKahn(
      List<String> seqCalculateIndicatorInstanceIdList,
      Map<String, Set<String>> kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap
  ) {
    if (Objects.isNull(kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap)
    ) {return;}

    Map<String, Set<String>> copyKIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap = new HashMap<>();
    kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.forEach((indicatorInstanceId, influencedIndicatorInstanceIdSet) -> {
      copyKIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put(indicatorInstanceId, new HashSet<>(influencedIndicatorInstanceIdSet));
    });

    Set<String> needCalculateIndicatorInstanceIdSet = copyKIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.keySet();
    /* runsix:维护顺序使用 */
    while (!needCalculateIndicatorInstanceIdSet.isEmpty()) {
      Set<String> zeroInfluencedIndicatorInstanceIdSet = new HashSet<>();
      needCalculateIndicatorInstanceIdSet.forEach(needCalculateIndicatorInstanceId -> {
        Set<String> influencedIndicatorInstanceIdSet = copyKIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.get(needCalculateIndicatorInstanceId);
        if (Objects.isNull(influencedIndicatorInstanceIdSet) || influencedIndicatorInstanceIdSet.isEmpty()) {
          zeroInfluencedIndicatorInstanceIdSet.add(needCalculateIndicatorInstanceId);
          if (Objects.nonNull(seqCalculateIndicatorInstanceIdList)) {
            seqCalculateIndicatorInstanceIdList.add(needCalculateIndicatorInstanceId);
          }
        }
      });
      if (zeroInfluencedIndicatorInstanceIdSet.isEmpty()) {
        throw new RsUtilBizException(EnumESC.INDICATOR_EXPRESSION_CIRCLE_DEPENDENCY);
      }
      needCalculateIndicatorInstanceIdSet.removeAll(zeroInfluencedIndicatorInstanceIdSet);
      copyKIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.forEach((indicatorInstanceId, influencedIndicatorInstanceIdSet) -> {
        influencedIndicatorInstanceIdSet.removeAll(zeroInfluencedIndicatorInstanceIdSet);
        copyKIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put(indicatorInstanceId, influencedIndicatorInstanceIdSet);
      });
    }
  }

  /* runsix:TODO DELETE  */
  public static void testAlgorithmKahn(
      List<String> seqCalculateIndicatorInstanceIdList,
      Map<String, Set<String>> kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap
  ) {
    if (Objects.isNull(kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap)
    ) {return;}

    Map<String, Set<String>> copyKIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap = new HashMap<>();
    kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.forEach((indicatorInstanceId, influencedIndicatorInstanceIdSet) -> {
      copyKIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put(indicatorInstanceId, new HashSet<>(influencedIndicatorInstanceIdSet));
    });

    Set<String> needCalculateIndicatorInstanceIdSet = copyKIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.keySet();
    /* runsix:维护顺序使用 */
    while (!needCalculateIndicatorInstanceIdSet.isEmpty()) {
      Set<String> zeroInfluencedIndicatorInstanceIdSet = new HashSet<>();
      needCalculateIndicatorInstanceIdSet.forEach(needCalculateIndicatorInstanceId -> {
        Set<String> influencedIndicatorInstanceIdSet = copyKIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.get(needCalculateIndicatorInstanceId);
        if (Objects.isNull(influencedIndicatorInstanceIdSet) || influencedIndicatorInstanceIdSet.isEmpty()) {
          zeroInfluencedIndicatorInstanceIdSet.add(needCalculateIndicatorInstanceId);
          if (Objects.nonNull(seqCalculateIndicatorInstanceIdList)) {
            seqCalculateIndicatorInstanceIdList.add(needCalculateIndicatorInstanceId);
          }
        }
      });
      if (zeroInfluencedIndicatorInstanceIdSet.isEmpty()) {
        throw new RsUtilBizException(EnumESC.INDICATOR_EXPRESSION_CIRCLE_DEPENDENCY);
      }
      needCalculateIndicatorInstanceIdSet.removeAll(zeroInfluencedIndicatorInstanceIdSet);
      copyKIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.forEach((indicatorInstanceId, influencedIndicatorInstanceIdSet) -> {
        influencedIndicatorInstanceIdSet.removeAll(zeroInfluencedIndicatorInstanceIdSet);
        copyKIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put(indicatorInstanceId, influencedIndicatorInstanceIdSet);
      });
    }
  }

  public static void main(String[] args) {
    List<String> seqCalculateIndicatorInstanceIdList = new ArrayList<>();
    Map<String, Set<String>> kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap = new HashMap<>();
    Set<String> firstSet = new HashSet<>();
    firstSet.add("2");
    firstSet.add("5");
    Set<String> secondSet = new HashSet<>();
    secondSet.add("3");
    secondSet.add("5");
    Set<String> thirdSet = new HashSet<>();
    thirdSet.add("4");
    Set<String> fourthSet = new HashSet<>();
    Set<String> fifthSet = new HashSet<>();
    kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put("1", firstSet);
    kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put("2", secondSet);
    kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put("3", thirdSet);
    kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put("4", fourthSet);
    kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put("5", fifthSet);
    RsUtilBiz.testAlgorithmKahn(seqCalculateIndicatorInstanceIdList, kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap);
    System.out.println(seqCalculateIndicatorInstanceIdList);
  }
}
