package org.dows.hep.api.base.materials;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fhb
 * @description 针对材料域有效
 * @date 2023/4/25 17:48
 */
@AllArgsConstructor
@Getter
public enum MaterialsEnabledEnum {
    ENABLED(1, "启用"),
    DISABLED(0, "禁用");

    private final Integer code;
    private final String name;
}
