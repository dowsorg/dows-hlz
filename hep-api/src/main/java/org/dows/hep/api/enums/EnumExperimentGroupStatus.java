package org.dows.hep.api.enums;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumExperimentGroupStatus {
  GROUP_RENAME(1, "团队命名"),
  ASSIGN_FUNC(2, "团队成员分配方案设计目录"),
  WAIT_ASSIGN_FUNC(3, "等待其它团队分配方案设计目录"),
  SCHEMA(4, "小组进行方案设计"),
  WAIT_SCHEMA(5, "提交后等待所有小组方案设计完成"),
  ASSIGN_DEPARTMENT(6, "机构分配"),
  WAIT_ALL_GROUP_ASSIGN(7, "等待所有小组机构分配完成"),
  COUNT_DOWN(8, "倒计时"),
  ;
  private Integer code;
  private String descr;
}
