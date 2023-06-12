package org.dows.hep.api.enums;

import lombok.Getter;

/**
 * 参与者类型
 */
public enum ParticipatorTypeEnum {

    TEACHER(0,"老师"),

    CAPTAIN(1,"组长"),

    STUDENT(2,"学生"),
            ;

    @Getter
    private int code;
    @Getter
    private String descr;
    ParticipatorTypeEnum(int code, String descr) {
        this.code = code;
        this.descr = descr;
    }


}
