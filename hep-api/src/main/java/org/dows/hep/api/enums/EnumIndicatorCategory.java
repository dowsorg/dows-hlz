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
  VIEW_MANAGEMENT_BASE_INFO(10301, "查看指标基本信息"),
  VIEW_MANAGEMENT_MONITOR_FOLLOWUP(10302, "查看指标监测随访"),
  VIEW_MANAGEMENT_PHYSICAL_EXAM(10303, "查看指标体格检查"),
  VIEW_MANAGEMENT_SUPPORT_EXAM(10304, "查看指标辅助检查"),
  OPERATE_MANAGEMENT(104, "操作指标"),
  OPERATE_MANAGEMENT_INTERVENE_DIET(10401, "操作指标饮食干预"),
  OPERATE_MANAGEMENT_INTERVENE_SPORTS(10402, "操作指标运动干预"),
  OPERATE_MANAGEMENT_INTERVENE_PSYCHOLOGY(10403, "操作指标心理干预"),
  OPERATE_MANAGEMENT_INTERVENE_TREATMENT(10404, "操作指标治疗干预"),
  JUDGE_MANAGEMENT(105, "判断指标"),
  JUDGE_MANAGEMENT_RISK_FACTOR(10501, "判断指标危险因素"),
  JUDGE_MANAGEMENT_HEALTH_PROBLEM(10502, "判断指标健康问题"),
  JUDGE_MANAGEMENT_HEALTH_GUIDANCE(10503, "判断指标健康指导"),
  JUDGE_MANAGEMENT_DISEASE_PROBLEM(10504, "判断指标疾病问题"),
  JUDGE_MANAGEMENT_HEALTH_MANAGEMENT_GOAL(10505, "判断指标健管目标"),
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
