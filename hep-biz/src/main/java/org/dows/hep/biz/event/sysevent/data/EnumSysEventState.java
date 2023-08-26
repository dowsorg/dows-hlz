package org.dows.hep.biz.event.sysevent.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author : wuzl
 * @date : 2023/8/24 15:27
 */

@Getter
@AllArgsConstructor
public enum EnumSysEventState {
    INIT(0,"初始"),
    TRIGGERED(1,"已触发"),
    DEALT(2,"已处理"),
    ;
    private Integer code;
    private String name;

    public static EnumSysEventState of(Integer code) {
        return Arrays.stream(EnumSysEventState.values())
                .filter(i -> i.getCode().equals(code))
                .findFirst()
                .orElse(EnumSysEventState.INIT);
    }
}
