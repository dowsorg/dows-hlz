package org.dows.hep.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumIndicatorRuleType {
  INDICATOR(0, "指标"),
  VARIABLE(1, "变量"),
  ;
  private final Integer code;
  private final String desc;
}
