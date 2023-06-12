package org.dows.hep.api.enums;

import lombok.Getter;
import org.dows.framework.api.StatusCode;

/**
 * 实验状态
 */
public enum ExperimentStatusCode implements StatusCode {
    NO_EXIST_EXPERIMENT(0, "不存在的实验"),

    NO_EXIST_GROUP_ID(1,"当前实验不存在该小组"),
    NOT_CAPTAIN(1, "该账号不是实验队长，无法创建队名!"),



    ;
    @Getter
    private Integer code;
    @Getter
    private String descr;

    ExperimentStatusCode(Integer code, String descr) {
        this.code = code;
        this.descr = descr;
    }


}
