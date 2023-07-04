package org.dows.hep.api.enums;

import lombok.Getter;

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
    @Getter
    private int state;
    @Getter
    private String descr;

    EnumExperimentState(int state, String descr) {
        this.state = state;
        this.descr = descr;
    }
}
