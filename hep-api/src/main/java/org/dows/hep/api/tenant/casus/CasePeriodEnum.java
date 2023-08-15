package org.dows.hep.api.tenant.casus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

    private static final Map<String, CasePeriodEnum> cacheByCode;
    private static final Map<Integer, CasePeriodEnum> cacheBySeq;

    static {
        cacheByCode = Arrays.stream(CasePeriodEnum.values()).collect(Collectors.toMap(CasePeriodEnum::getCode, item -> item));
        cacheBySeq = Arrays.stream(CasePeriodEnum.values()).collect(Collectors.toMap(CasePeriodEnum::getSeq, item -> item));
    }

    public static CasePeriodEnum getByCode(String code) {
        return cacheByCode.get(code);
    }

    public static CasePeriodEnum getBySeq(Integer seq) {
        return cacheBySeq.get(seq);
    }

}
