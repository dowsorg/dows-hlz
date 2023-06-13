package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumExperimentGroupStatus {
  GROUP_RENAME(0, "团队命名"),
  ASSIGN_FUNC(1, "分配方案设计目录"),
  SCHEMA(2, "小组进行方案设计"),
  WAIT_SCHEMA(3, "小组完成方案设计"),
  ASSIGN_DEPARTMENT(4, "机构分配"),
  WAIT_ALL_GROUP_ASSIGN(5, "所有小组机构分配结束"),
  COUNT_DOWN(6, "倒计时"),
  ;
  private Integer code;
  private String descr;
}
