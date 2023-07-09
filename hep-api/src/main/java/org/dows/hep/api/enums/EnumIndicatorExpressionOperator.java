package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author runsix
 */
@Getter
@AllArgsConstructor
public enum EnumIndicatorExpressionOperator {
  ADD("+", "加号"),
  SUBTRACT("-", "减号"),
  MULTIPLY("*", "乘号"),
  DIVIDE("/", "除号"),
  GT(">", "大于"),
  LT("<", "小于"),
  EQ("==", "等于"),
  GE(">=", "大于等于"),
  LE("<=", "小于等于"),
  LB("(", "左括号"),
  RB(")", "右括号"),
  AND("&&", "并且"),
  OR("||", "或者"),
  ;

  private final String operator;
  private final String desc;

  public final static Map<String, EnumIndicatorExpressionOperator> kOperatorVEnumIndicatorExpressionOperatorMap = new HashMap<>();
  public final static Map<String, EnumIndicatorExpressionOperator> kJudgeOperatorVEnumIndicatorExpressionOperatorMap = new HashMap<>();

  static {
    kJudgeOperatorVEnumIndicatorExpressionOperatorMap.put(GT.getOperator(), GT);
    kJudgeOperatorVEnumIndicatorExpressionOperatorMap.put(LT.getOperator(), LT);
    kJudgeOperatorVEnumIndicatorExpressionOperatorMap.put(EQ.getOperator(), EQ);
    kJudgeOperatorVEnumIndicatorExpressionOperatorMap.put(GE.getOperator(), GE);
    kJudgeOperatorVEnumIndicatorExpressionOperatorMap.put(LE.getOperator(), LE);

    for (EnumIndicatorExpressionOperator enumIndicatorExpressionOperator : EnumIndicatorExpressionOperator.values()) {
      kOperatorVEnumIndicatorExpressionOperatorMap.put(enumIndicatorExpressionOperator.getOperator(), enumIndicatorExpressionOperator);
    }
   }

   public static EnumIndicatorExpressionOperator getOperator(String operator) {
    if (StringUtils.isBlank(operator)) {
      return null;
    }
    return kOperatorVEnumIndicatorExpressionOperatorMap.get(operator);
   }

   public static EnumIndicatorExpressionOperator getJudgeOperator(String judgeOperator) {
    if (StringUtils.isBlank(judgeOperator)) {
      return null;
    }
    return kJudgeOperatorVEnumIndicatorExpressionOperatorMap.get(judgeOperator);
   }

   public static boolean isOperator(String operator) {
    if (Objects.isNull(operator)) {
      return false;
    }
    return Objects.isNull(kOperatorVEnumIndicatorExpressionOperatorMap.get(operator));
   }

   public static boolean isJudgeOperator(String judgeOperator) {
    if (Objects.isNull(judgeOperator)) {
      return false;
    }
    return Objects.isNull(kJudgeOperatorVEnumIndicatorExpressionOperatorMap.get(judgeOperator));
   }


}
