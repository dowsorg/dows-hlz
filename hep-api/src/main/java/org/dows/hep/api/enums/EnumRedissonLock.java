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
    INDICATOR_INSTANCE_CREATE_DELETE_UPDATE(1, "indicator-instance-create-delete-update"),
    INDICATOR_FUNC_CREATE_DELETE_UPDATE(2, "indicator-func-create-delete-update"),
    INDICATOR_VIEW_BASE_INFO_CREATE_DELETE_UPDATE(3, "indicator-view-base-info-create-delete-update"),
    INDICATOR_VIEW_MONITOR_FOLLOWUP_CREATE_DELETE_UPDATE(4, "indicator-view-monitor-followup-create-delete-update"),
    INDICATOR_EXPRESSION_CREATE_DELETE_UPDATE(4, "indicator-expression-create-delete-update"),
    ;
    private final Integer code;
    private final String situation;
}
