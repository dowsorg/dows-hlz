package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

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

  public static EnumStatus of(Integer src) {
    return Optional.ofNullable(src).orElse(0) > 0 ? EnumStatus.DISABLE : EnumStatus.ENABLE;
  }
}
