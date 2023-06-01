package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumStatus {
  DISABLE(0, "禁用"),
  ENABLE(1, "启用"),
  ;
  private final Integer code;
  private final String desc;
}
