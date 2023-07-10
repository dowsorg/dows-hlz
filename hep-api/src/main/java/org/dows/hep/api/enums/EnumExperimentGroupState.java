package org.dows.hep.api.enums;

import lombok.Getter;

/**
 * @author jx
 * @date 2023/7/10 16:22
 */
public enum EnumExperimentGroupState {
    CREATE_GROUP(0, "新建（待重新命名)"),
    ASSIGN_MEMBER_ROLE(1, "分配成员角色"),
    ASSIGN_FINISH(2, "分配完成"),
    LOCK_IN(3, "已锁定"),
    DISBAND_FINISH(4, "已解散"),
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
