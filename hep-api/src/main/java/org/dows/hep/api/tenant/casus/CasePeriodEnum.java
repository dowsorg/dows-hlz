package org.dows.hep.api.tenant.casus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.exceptions.BizException;

import java.util.Arrays;

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

    public static CasePeriodEnum getByCode(String code) {
        return Arrays.stream(CasePeriodEnum.values())
                .filter(item -> item.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new BizException(CaseESCEnum.DATA_NULL));
    }

}
