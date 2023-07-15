package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author jx
 * @date 2023/7/15 16:15
 */
@AllArgsConstructor
@Getter
public enum EnumExperimentNotice {
    startNotice(0, "开始通知"),
    endNotice(1, "结束通知"),
    ;
    private final Integer code;
    private final String desc;
}
