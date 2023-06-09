package org.dows.hep.api.tenant.casus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum CasePeriodEnum {
    FIRST("FIRST", "第一期"),
    SECOND("SECOND", "第二期"),
    THIRD("THIRD", "第三期"),
    FOURTH("FOURTH", "第四期"),
    FIFTH("FIFTH", "第五期");

    private final String code;
    private final String name;

    public static String getNameByCode(String code) {
        return Arrays.stream(CasePeriodEnum.values())
                .filter(item -> item.getCode().equals(code))
                .map(CasePeriodEnum::getName)
                .findFirst()
                .orElse("");
    }

}
