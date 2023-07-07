package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@Getter
@AllArgsConstructor
public enum EnumField {
  DATABASE(0, "数据库"),
  CASE(1, "案例库"),
  ;
  private final Integer code;
  private final String desc;
}
