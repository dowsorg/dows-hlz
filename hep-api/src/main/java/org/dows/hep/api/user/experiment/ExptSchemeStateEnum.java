package org.dows.hep.api.user.experiment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum ExptSchemeStateEnum {
    NOT_SUBMITTED(0, "未提交"),
    SUBMITTED(1, "待审批"),
    SCORED(2, "已审批")
    ;

    private final Integer code;
    private final String name;

    private static final Map<Integer, ExptSchemeStateEnum> cacheByCode;

    static {
        cacheByCode = Arrays.stream(ExptSchemeStateEnum.values()).collect(Collectors.toMap(ExptSchemeStateEnum::getCode, item -> item));
    }

    public static ExptSchemeStateEnum getByCode(Integer code) {
        return cacheByCode.get(code);
    }
}
