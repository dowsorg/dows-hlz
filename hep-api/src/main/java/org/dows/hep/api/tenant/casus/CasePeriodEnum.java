package org.dows.hep.api.tenant.casus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CasePeriodEnum {
    FIRST(1, "FIRST"),
    SECOND(2, "SECOND"),
    THIRD(3, "THIRD"),
    FOURTH(4, "FOURTH"),
    FIFTH(5, "FIFTH");

    private final Integer code;
    private final String name;

}
