package org.dows.hep.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumString {
  LIMIT_1("LIMIT 1", "mybatisplus只取一个值"),
  ;
  private final String str;
  private final String desc;
}
