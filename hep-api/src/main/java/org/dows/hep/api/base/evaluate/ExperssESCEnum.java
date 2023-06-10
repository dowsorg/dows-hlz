package org.dows.hep.api.base.evaluate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.StatusCode;

@Getter
@AllArgsConstructor
public enum ExperssESCEnum implements StatusCode {
    EXPRESS_NON_NULL(40010, "表达式不能为空"),
    EXPRESS_GRAMMAR_ERROR(40011, "表达式语法错误"),
    EXPRESS_BEGIN_END_ERROR(40011, "表达式开始结束符错误"),
    EXPRESS_FIRST_LAST_ERROR(40011, "表达式首元素尾元素错误"),
    ;
    private final Integer code;
    private final String descr;

}
