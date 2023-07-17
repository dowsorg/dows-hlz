package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.StatusCode;

/**
 * @author runsix
 */
@Getter
@AllArgsConstructor
/* runsix:ESC = ExceptionStatusCode */
public enum EnumESC implements StatusCode {
    VALIDATE_EXCEPTION(40000, "参数不正确"),
    INDICATOR_CATEGORY_DELETE_FAILED(50101, "指标类别删除失败"),
    SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_CATEGORY_LATER(50102, "系统繁忙，请稍后重新操作指标类别"),
    INDICATOR_CATEGORY_HAS_DATA_CANNOT_DELETE(50103, "该类别下有数据，不能删除"),
    SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_INSTANCE_LATER(50104, "系统繁忙，请稍后重新操作指标类别"),
    SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_FUNC_LATER(50105, "系统繁忙，请稍后重新操作指标功能"),
    SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER(50106, "系统繁忙，请稍后重新操作查看指标基本信息"),
    SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_MONITOR_FOLLOWUP_LATER(50107, "系统繁忙，请稍后重新操作查看指标监测随访"),
    SYSTEM_INDICATOR_INSTANCE_CANNOT_DELETE(50108, "系统自带指标，不允许删除"),
    SYSTEM_INDICATOR_CATEGORY_CANNOT_DELETE(50109, "系统自带目录，不允许删除"),
    RS_CALCULATE_ERROR(50110, "计算出错，请及时联系管理员"),
    INDICATOR_EXPRESSION_ID_IS_ILLEGAL(50111,  "公式id不合法"),
    INDICATOR_EXPRESSION_CIRCLE_DEPENDENCY(50112, "指标管理公式不允许循环依赖"),
    INDICATOR_EXPRESSION_MIN_MAX_MUST_BE_DIGIT(50113, "指标管理公式上下限只能是数字"),
    INDICATOR_EXPRESSION_MAX_MUST_GE_MIN(50114, "指标管理公式上下限，如果最大最小值都存在，最大值必须大于等于最小值"),
    INDICATOR_EXPRESSION_ITEM_ID_IS_ILLEGAL(50115,  "公式细项id不合法"),

    INDICATOR_EXPRESSION_CHECK_INDICATOR_INSTANCE_ID_DOES_NOT_EXIST(50116, "检查公式对应指标不存在"),
    INDICATOR_EXPRESSION_CHECK_CONDITION_MUST_BE_TRUE_OR_FALSE(50117, "检查公式的条件必须为true或false"),
    DATABASE_INDICATOR_MANAGEMENT_RESULT_CANNOT_BE_BLANK(50118, "数据库指标管理的公式结果不能为空"),
    DATABASE_INDICATOR_JUDGE_RISK_FACTOR_RESULT_ONLY_CAN_BE_BLANK(50119, "数据库危险因素的公式是一个条件，结果不允许有值"),
    DATABASE_LABEL_MANAGEMENT_FACTOR_RESULT_ONLY_CAN_BE_BLANK(50120, "数据库标签管理的公式是一个条件，结果不允许有值"),
    DATABASE_CROWDS_FACTOR_RESULT_ONLY_CAN_BE_BLANK(50121, "数据库人群类型的公式是一个条件，结果不允许有值"),
    DATABASE_RISK_MODEL_RESULT_CANNOT_BE_BLANK(50122, "数据库指标管理的公式结果不能为空"),
    ;
    private final Integer code;
    private final String descr;
}
