package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
  CROWDS(11, "人群类型"),
  RISK_MODEL(12, "死亡模型"),

  INDICATOR_JUDGE_CHECKRULE(21,"判断指标-判断规则"),
  INDICATOR_JUDGE_REFINDICATOR(22,"判断指标-关联指标"),
  INDICATOR_JUDGE_GOAL_CHECKRULE(23,"管理目标-判断规则"),
  INDICATOR_JUDGE_GOAL_REFINDICATOR(24,"管理目标-关联指标"),
  ;

  private final Integer source;
  private final String name;

  public final static Map<Integer, EnumIndicatorExpressionSource> kTypeVEnumIndicatorExpressionSourceMap = new HashMap<>();

  static {
    for (EnumIndicatorExpressionSource enumIndicatorExpressionSource : EnumIndicatorExpressionSource.values()) {
      kTypeVEnumIndicatorExpressionSourceMap.put(
          enumIndicatorExpressionSource.getSource(), enumIndicatorExpressionSource
      );
    }
  }

  public static EnumIndicatorExpressionSource of(Integer code){
    return Arrays.stream(EnumIndicatorExpressionSource.values())
            .filter(i->i.getSource().equals(code))
            .findFirst()
            .orElse(NONE);
  }

  public static EnumIndicatorExpressionSource getBySource(Integer source) {
    if (Objects.isNull(source)) {
      return null;
    }
    return kTypeVEnumIndicatorExpressionSourceMap.get(source);
  }
}
