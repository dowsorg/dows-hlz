package org.dows.hep.api.enums;

import lombok.Getter;

/**
 * 实验模式
 */
public enum ExperimentModeEnum {
    SCHEME(0, "设计模式"),
    SAND(1, "沙盘模式"),
    STANDARD(2, "标准模式"),

    ;
    @Getter
    private int state;
    @Getter
    private String descr;

    ExperimentModeEnum(int state, String descr) {
        this.state = state;
        this.descr = descr;
    }
}
