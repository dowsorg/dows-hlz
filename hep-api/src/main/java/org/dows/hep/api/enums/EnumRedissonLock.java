package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumRedissonLock {
    INDICATOR_CATEGORY_CREATE_DELETE_UPDATE(0, "indicator-category-create-delete-update"),
    INDICATOR_INSTANCE_CREATE_DELETE_UPDATE(0, "indicator-instance-create-delete-update"),
    ;
    private final Integer code;
    private final String situation;
}
