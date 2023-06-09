package org.dows.hep.api.base.materials;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.StatusCode;

@Getter
@AllArgsConstructor
public enum MaterialsESCEnum implements StatusCode {
    PARAMS_NON_NULL(42000, "请求参数不能为空"),
    DATA_NULL(42001, "数据不存在"),
    NO_AUTH(42002, "没有权限");
    private final Integer code;
    private final String descr;
}
