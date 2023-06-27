package org.dows.hep.api.user.experiment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@AllArgsConstructor
@Getter
public enum ExptSchemeStateEnum {
    NOT_SUBMITTED(0, "未提交"),
    SUBMITTED(1, "已提交"),
    ;

    private final Integer code;
    private final String name;

    public static ExptSchemeStateEnum getByCode(Integer code) {
        return Arrays.stream(values())
                .filter(item -> Objects.equals(item.getCode(), code))
                .findFirst()
                .orElse(null);
    }
}
