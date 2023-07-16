package org.dows.hep.api.enums;

import lombok.Getter;

/**
 * @author jx
 * @date 2023/7/10 16:22
 */
public enum EnumExperimentGroupState {
    GROUP_RENAME(0, "团队命名"),
    ASSIGN_FUNC(1, "分配方案设计目录"),
    SCHEMA(2, "小组进行方案设计"),
    WAIT_SCHEMA(3, "小组完成方案设计"),
    ASSIGN_DEPARTMENT(4, "机构分配"),
    WAIT_ALL_GROUP_ASSIGN(5, "所有小组机构分配结束"),
    COUNT_DOWN(6, "倒计时"),
            ;
    @Getter
    private int state;
    @Getter
    private String descr;

    EnumExperimentGroupState(int state, String descr) {
        this.state = state;
        this.descr = descr;
    }
}
