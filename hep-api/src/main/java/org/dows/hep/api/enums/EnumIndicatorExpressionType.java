package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumIndicatorExpressionType {
  CONDITION(0, "条件公式"),
  RANDOM(1, "随机公式"),
  INDICATOR_JUDGE_RISK_FACTOR(2, "判断指标-危险因素"),
  ;

  private final Integer type;
  private final String name;
}
