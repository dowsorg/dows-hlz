package org.dows.hep.api.tenant.casus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum CasePeriodEnum {
    FIRST("FIRST", "第一期", 1),
    SECOND("SECOND", "第二期", 2),
    THIRD("THIRD", "第三期", 3),
    FOURTH("FOURTH", "第四期", 4),
    FIFTH("FIFTH", "第五期", 5);

    private final String code;
    private final String name;
    private final Integer seq;

    private static final Map<String, CasePeriodEnum> codeMap = new LinkedHashMap<>();

    public static CasePeriodEnum getByCode(String code) {
        CasePeriodEnum casePeriodEnum = codeMap.get(code);
        if (casePeriodEnum == null) {
            Arrays.stream(CasePeriodEnum.values())
                    .forEach(item -> codeMap.put(item.getCode(), item));
            casePeriodEnum = codeMap.get(code);
        }
        return casePeriodEnum;
    }

}
