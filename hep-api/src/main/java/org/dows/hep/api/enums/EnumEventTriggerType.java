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
    PERIOD1(1,"规则触发-第1期"),
    PERIOD2(2,"规则触发-第2期"),
    PERIOD3(3,"规则触发-第3期"),
    PERIOD4(4,"规则触发-第4期"),
    PERIOD5(5,"规则触发-第5期"),
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
