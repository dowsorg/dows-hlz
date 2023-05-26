package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 事件触发类型
 * @author : wuzl
 * @date : 2023/5/22 13:57
 */
@AllArgsConstructor
@Getter
public enum EnumEventTriggerType {
    CONDITION(0,"条件触发"),
    PERIOD1(1,"时间触发-第一期"),
    PERIOD2(2,"时间触发-第二期"),
    PERIOD3(3,"时间触发-第三期"),
    PERIOD4(4,"时间触发-第四期"),
    PERIOD5(5,"时间触发-第五期"),
    ;
    private Integer code;
    private String name;

    public static EnumEventTriggerType of(Integer code){
        return  Arrays.stream(EnumEventTriggerType.values())
                .filter(i->i.getCode().equals(code))
                .findFirst()
                .orElse(EnumEventTriggerType.CONDITION);
    }

}
