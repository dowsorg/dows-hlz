package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 事件触发时间段
 * @author : wuzl
 * @date : 2023/5/26 10:11
 */
@AllArgsConstructor
@Getter
public enum EnumEventTriggerSpan {
    NONE("0","NA"),
    FRONT("1","前期"),
    MIDDLE("2","中期"),
    TAIL("3","后期"),
    ;
    private String code;
    private String name;

    public static EnumEventTriggerSpan of(String code) {
        return Arrays.stream(EnumEventTriggerSpan.values())
                .filter(i->i.getCode().equals(code))
                .findFirst()
                .orElse(EnumEventTriggerSpan.NONE);
    }
}
