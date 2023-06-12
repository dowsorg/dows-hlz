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
  CONSTANT(2, "常量公式-系统计算直接赋值，不是通过配置公式计算"),
  ;

  private final Integer type;
  private final String name;
}
