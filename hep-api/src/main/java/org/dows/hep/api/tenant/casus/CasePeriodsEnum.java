package org.dows.hep.api.tenant.casus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CasePeriodsEnum {
    FIRST(1, "第一期"),
    SECOND(2, "第二期"),
    THIRD(3, "第三期"),
    FOURTH(4, "第四期"),
    FIFTH(5, "第五期");

    private final Integer code;
    private final String name;

}
