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
  DISABLE(0, "禁用"),
  ENABLE(1, "启用"),
  ;
  private final Integer code;
  private final String desc;

  public static EnumStatus of(Integer src) {
    return Optional.ofNullable(src).orElse(0) > 0 ? EnumStatus.ENABLE : EnumStatus.DISABLE;
  }
}
