package org.dows.hep.api.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实验状态
 */
public enum EnumExperimentState {
    UNBEGIN(0, "未开始"),
    PREPARE(1, "准备中"),
    ONGOING(2, "进行中"),
    SUSPEND(3, "暂停中"),
    SCORING(4,"算分中"),
    FINISH(5, "已结束"),

    ;

    private static final Map<Integer, EnumExperimentState> cacheByCode;

    static {
        cacheByCode = Arrays.stream(EnumExperimentState.values()).collect(Collectors.toMap(EnumExperimentState::getState, item -> item));
    }

    @Getter
    private int state;
    @Getter
    private String descr;

    EnumExperimentState(int state, String descr) {
        this.state = state;
        this.descr = descr;
    }

    public static EnumExperimentState getByCode(Integer code) {
        return cacheByCode.get(code);
    }

    public static String getNameByCode(Integer code) {
        return getByCode(code).getDescr();
    }

}
