package org.dows.hep.api.base.evaluate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.StatusCode;

@Getter
@AllArgsConstructor
public enum EvaluateESCEnum implements StatusCode{
    PARAMS_NON_NULL(42000, "请求参数不能为空"),
    DATA_NULL(42001, "数据不存在"),
    CANNOT_DEL_FER_DATA(42002, "被引用数据不可删除"),

    ;
    private final Integer code;
    private final String descr;
}
