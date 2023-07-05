package org.dows.hep.api.user.experiment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum ExptSchemeStateEnum {
    NOT_SUBMITTED(0, "未提交"),
    SUBMITTED(1, "已提交"),
    ;

    private final Integer code;
    private final String name;

    private static final Map<Integer, ExptSchemeStateEnum> codeMap = new LinkedHashMap<>();

    public static ExptSchemeStateEnum getByCode(Integer code) {
        ExptSchemeStateEnum exptSchemeStateEnum = codeMap.get(code);
        if (exptSchemeStateEnum == null) {
            Arrays.stream(ExptSchemeStateEnum.values())
                    .forEach(item -> codeMap.put(item.getCode(), item));
            exptSchemeStateEnum = codeMap.get(code);
        }
        return exptSchemeStateEnum;
    }
}
