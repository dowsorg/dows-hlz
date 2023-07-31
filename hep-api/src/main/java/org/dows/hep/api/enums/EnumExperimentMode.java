package org.dows.hep.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实验模式
 */
public enum EnumExperimentMode {
    SCHEME(0, "设计模式"),
    SAND(1, "沙盘模式"),
    STANDARD(2, "标准模式"),

    ;

    private static final Map<Integer, EnumExperimentMode> cacheByCode;

    static {
        cacheByCode = Arrays.stream(EnumExperimentMode.values()).collect(Collectors.toMap(EnumExperimentMode::getCode, item -> item));
    }

    @Getter
    private int code;
    @Getter
    private String descr;

    EnumExperimentMode(int code, String descr) {
        this.code = code;
        this.descr = descr;
    }

    public static EnumExperimentMode getByCode(Integer code) {
        return cacheByCode.get(code);
    }

    public static String getNameByCode(Integer code) {
        return getByCode(code).getDescr();
    }

}
