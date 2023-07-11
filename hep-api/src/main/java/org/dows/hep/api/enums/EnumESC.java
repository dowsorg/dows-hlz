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
    ;
    private final Integer code;
    private final String descr;
}
