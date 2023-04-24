package org.dows.hep.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumStatus {
  ENABLE(0, "启用"),
  DISABLE(1, "禁用"),
  ;
  private final Integer code;
  private final String desc;
}
