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
  SPLIT_DOLLAR("$", "美元符号"),
  ZERO("0", "0的字符串表示"),
  UNDERLINE("_", "下划线"),
  APP_ID("3", "hep项目的appId约定为3"),
  SPACE(" ", "单空格"),
  JIN("#", "#"),
  AT("@", "@"),
  SINGLE_QUOTES("'", "单引号"),
  INDICATOR_EXPRESSION_START("#", "指标公式变量第一个字符"),
  INDICATOR_EXPRESSION_SPLIT("\\$", "指标公式扩展点分割符"),
  INDICATOR_EXPRESSION_INPUT("@", "用户输入量第一个字符"),
  INDICATOR_EXPRESSION_LIST_SPLIT(",", "列表分割符"),
  PERIOD_FIRST("1", "实验第一期"),
  INPUT_GOAL("input_goal","管理目标-输入值")

  ;
  private final String str;
  private final String desc;
}
