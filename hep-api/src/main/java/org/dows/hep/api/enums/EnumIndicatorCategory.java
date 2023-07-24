package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.exception.IndicatorCategoryException;

import java.util.*;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumIndicatorCategory {
  RESOURCE_MANAGEMENT("101", "图示管理", 1),
  INDICATOR_MANAGEMENT("102", "指标管理", 1),
  INDICATOR_MANAGEMENT_BASE_INFO("10201", "基本信息", 1),
  INDICATOR_MANAGEMENT_MONEY("10202", "资金情况", 1),
  INDICATOR_MANAGEMENT_HEALTH("10203", "健康指数", 1),
  SYSTEM_CALCULATE_INDICATOR("10204", "系统计算指标", 1),


  VIEW_MANAGEMENT("103", "查看指标",1),
  VIEW_MANAGEMENT_BASE_INFO("10301", "查看指标基本信息", 0),
  VIEW_MANAGEMENT_MONITOR_FOLLOWUP("10302", "查看指标监测随访", 0),
  VIEW_MANAGEMENT_NO_REPORT_TWO_LEVEL("10303", "二级类-无报告，即所属类型只有一级，1+1", 0),
  VIEW_MANAGEMENT_NO_REPORT_FOUR_LEVEL("10304", "四级类-无报告，即所属类型只有三级，1+3", 0),
  OPERATE_MANAGEMENT("104", "操作指标", 1),
  OPERATE_MANAGEMENT_INTERVENE_DIET("10401", "饮食干预", 0),
  OPERATE_MANAGEMENT_INTERVENE_SPORTS("10402", "运动干预", 0),
  OPERATE_MANAGEMENT_INTERVENE_PSYCHOLOGY("10403", "操作指标心理干预", 0),
  OPERATE_MANAGEMENT_INTERVENE_TREATMENT("10404", "操作指标治疗干预", 0),
  JUDGE_MANAGEMENT("105", "判断指标", 1),
  JUDGE_MANAGEMENT_RISK_FACTOR("10501", "判断指标危险因素", 0),
  JUDGE_MANAGEMENT_HEALTH_PROBLEM("10502", "判断指标健康问题", 0),
  JUDGE_MANAGEMENT_HEALTH_GUIDANCE("10503", "判断指标健康指导", 0),
  JUDGE_MANAGEMENT_DISEASE_PROBLEM("10504", "判断指标疾病问题", 0),
  JUDGE_MANAGEMENT_HEALTH_MANAGEMENT_GOAL("10505", "判断指标健管目标", 0),
  MAKE_PLAN("106", "制定方案",1),
  RISK_MODEL("107", "风险模型", 1),
  EVALUATE_QUESTIONNAIRE("108", "评估问卷", 1),
  EMERGENCY("109", "突发事件", 1),
  PEOPLE_MANAGEMENT("110", "人物管理", 1),
  PLAN_DESIGN("111", "方案设计", 1),
  KNOWLEDGE_POINT("112", "知识考点", 1),
  LABEL("113", "标签管理", 1),
  ;

  private final String code;
  private final String categoryName;
  private final Integer cannotDelete;

  public static Map<String, String> kCodeVCategoryNameMap = new HashMap<>();
  public static Map<String, EnumIndicatorCategory> kCodeVEnumIndicatorCategoryMap = new HashMap<>();
  static {

    for (EnumIndicatorCategory enumIndicatorCategory : EnumIndicatorCategory.values()) {
      kCodeVCategoryNameMap.put(enumIndicatorCategory.getCode(), enumIndicatorCategory.getCategoryName());
      kCodeVEnumIndicatorCategoryMap.put(enumIndicatorCategory.getCode(), enumIndicatorCategory);
    }
  }

  public static EnumIndicatorCategory getByCode(String code) {
    return kCodeVEnumIndicatorCategoryMap.get(code);
  }

  public static String getCategoryNameByCode(String code) {
    return kCodeVCategoryNameMap.get(code);
  }
}
