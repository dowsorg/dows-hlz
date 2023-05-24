package org.dows.hep.api.base.evaluate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EvaluateEnabledEnum {
    ENABLED(1, "启用"),
    DISABLED(0, "禁用");

    private final Integer code;
    private final String name;
}
