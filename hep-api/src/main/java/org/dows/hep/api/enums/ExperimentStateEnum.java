package org.dows.hep.api.enums;

import lombok.Getter;

/**
 * 实验状态
 */
public enum ExperimentStateEnum {
    UNBEGIN(0, "未开始"),
    ONGOING(1, "进行中"),
    SUSPEND(2, "暂停中"),
    FINISH(3, "已结束"),

    ;
    @Getter
    private int state;
    @Getter
    private String descr;

    ExperimentStateEnum(int state, String descr) {
        this.state = state;
        this.descr = descr;
    }
}
