package org.dows.hep.api.user.experiment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.StatusCode;

@Getter
@AllArgsConstructor
public enum ExperimentESCEnum implements StatusCode {
    PARAMS_NON_NULL(42000, "请求参数不能为空"),
    DATA_NULL(42001, "数据不存在"),
    CANNOT_DEL_FER_DATA(42002, "被引用数据不可删除"),
    MANY_RESULT(42003, "查询有多个结果，数据错误"),
    SCHEME_NOT_NULL(42004, "实验方案设计数据为空"),
    NO_AUTHORITY(42005, "没有权限访问"),

    ;

    private final Integer code;
    private final String descr;
}
