package org.dows.hep.api.user.experiment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum ExptReviewStateEnum {
    NOT_SUBMITTED(0, "未提交"),
    UNREVIEWED(1, "待批阅"),
    REVIEWED(2, "已批阅"),
    ;

    private final Integer code;
    private final String name;

    private static final Map<Integer, ExptReviewStateEnum> cacheByCode;

    static {
        cacheByCode = Arrays.stream(ExptReviewStateEnum.values()).collect(Collectors.toMap(ExptReviewStateEnum::getCode, item -> item));
    }

    public static ExptReviewStateEnum getByCode(Integer code) {
        return cacheByCode.get(code);
    }
}
