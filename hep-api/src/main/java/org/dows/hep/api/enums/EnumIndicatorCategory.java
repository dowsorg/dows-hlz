package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumIndicatorCategory {
  RESOURCE_MANAGEMENT(101, "图示管理"),
  INDICATOR_MANAGEMENT(102, "指标管理"),
  VIEW_MANAGEMENT(103, "查看指标"),
  OPERATE_MANAGEMENT(104, "操作指标"),
  JUDGE_MANAGEMENT(105, "判断指标"),
  MAKE_PLAN(106, "制定方案"),
  RISK_MODEL(107, "风险模型"),
  EVALUATE_QUESTIONNAIRE(108, "评估问卷"),
  EMERGENCY(109, "突发事件"),
  PEOPLE_MANAGEMENT(110, "人物管理"),
  PLAN_DESIGN(111, "方案设计"),
  KNOWLEDGE_POINT(112, "知识考点"),
  LABEL(113, "标签管理"),
  ;

  private final Integer code;
  private final String categoryName;
}
