package org.dows.hep.api.user.experiment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExptSchemeStateEnum {
    NOT_SUBMITTED(0, "未提交"),
    SUBMITTED(1, "已提交"),
    ;

    private final Integer code;
    private final String name;
}
