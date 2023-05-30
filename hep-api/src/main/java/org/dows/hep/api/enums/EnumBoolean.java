package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumBoolean {
  FALSE(false, "禁用"),
  TRUE(true, "启用"),
  ;
  private final Boolean code;
  private final String desc;
}
