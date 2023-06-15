package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumIndicatorExpressionSource {
  NONE(0,""),
  INDICATOR_MANAGEMENT(1, "指标管理"),
  INDICATOR_JUDGE_RISK_FACTOR(2, "判断指标-危险因素"),
  INDICATOR_OPERATOR_INGREDIENT(3, "操作指标-食材"),
  INDICATOR_OPERATOR_SPORT(4, "操作指标-运动项目"),
  INDICATOR_OPERATOR_NO_REPORT_TWO_LEVEL(5, "二级类-无报告"),
  INDICATOR_OPERATOR_HAS_REPORT_FOUR_LEVEL(6, "四级类-有报告"),
  EMERGENCY_TRIGGER_CONDITION(7, "突发事件-触发条件"),
  EMERGENCY_INFLUENCE_INDICATOR(8, "突发事件-影响指标"),
  EMERGENCY_ACTION_INFLUENCE_INDICATOR(9, "突发事件-措施影响指标"),
  LABEL_MANAGEMENT(10, "标签管理"),
  ;

  private final Integer type;
  private final String name;

  public static EnumIndicatorExpressionSource of(Integer code){
    return Arrays.stream(EnumIndicatorExpressionSource.values())
            .filter(i->i.getType().equals(code))
            .findFirst()
            .orElse(NONE);
  }
}
