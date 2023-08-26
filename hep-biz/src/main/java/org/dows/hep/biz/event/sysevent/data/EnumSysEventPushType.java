package org.dows.hep.biz.event.sysevent.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author : wuzl
 * @date : 2023/8/25 15:42
 */
@Getter
@RequiredArgsConstructor
public enum EnumSysEventPushType {
    NONE(0,"从不推送"),
    NEWEST(1,"推送最新"),
    ALWAYS(2,"始终推送")
    ;
    private final Integer code;
    private final String name;

    public static EnumSysEventPushType of(Integer code){
        return Arrays.stream(EnumSysEventPushType.values())
                .filter(i->i.getCode().equals(code))
                .findFirst()
                .orElse(EnumSysEventPushType.NONE);
    }
}
