package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumString {
  LIMIT_1("LIMIT 1", "mybatisplus只取一个值"),
  APP_ID("3", "hep项目的appId约定为3")
  ;
  private final String str;
  private final String desc;
}
