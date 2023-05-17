package org.dows.hep.api.tenant.casus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fhb
 * @description
 * @date 2023/5/16 13:58
 */
@AllArgsConstructor
@Getter
public enum CaseEnabledEnum {
    ENABLED(1, "启用"),
    DISABLED(0, "禁用");

    private final Integer code;
    private final String name;
}
