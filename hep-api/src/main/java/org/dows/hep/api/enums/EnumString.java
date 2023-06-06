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
  COMMA(",", "英文逗号"),
  SPLIT_DOLLAR("\\$", "美元符号"),
  ZERO("0", "0的字符串表示"),
  UNDERLINE("_", "下划线"),
  APP_ID("3", "hep项目的appId约定为3"),
  SPACE(" ", "单空格"),
  JIN("#", "#"),
  AT("@", "@"),
  SINGLE_QUOTES("'", "单引号"),
  ;
  private final String str;
  private final String desc;
}
