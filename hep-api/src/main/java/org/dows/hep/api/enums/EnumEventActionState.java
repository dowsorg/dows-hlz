package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : wuzl
 * @date : 2023/7/7 10:47
 */
@AllArgsConstructor
@Getter
public enum EnumEventActionState {
    NONE(0,"无需处理"),
    TODO(1,"待处理"),
    DONE(2,"已处理"),
    ;

    private Integer code;
    private String descr;


}
