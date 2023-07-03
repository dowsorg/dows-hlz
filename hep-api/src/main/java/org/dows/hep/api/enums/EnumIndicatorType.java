package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@Getter
@AllArgsConstructor
public enum EnumIndicatorType {
  USER_CREATED(0, "用户创建"),
  MONEY(1, "用户资金"),
  SEX(2, "性别"),
  HEIGHT(3, "身高"),
  WEIGHT(4, "体重"),
  ;

  private final Integer type;
  private final String desc;
}
