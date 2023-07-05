package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author : wuzl
 * @date : 2023/6/17 21:39
 */
@AllArgsConstructor
@Getter
public enum EnumExperimentEventState {

    //0-初始 1-已触发 2-用户已处理 3-系统已取消
    INIT(0,"初始"),
    TRIGGERED(1,"已触发"),
    USERAction(2,"用户已处理"),
    SYSCanceled(3,"系统已取消")
    ;

    private Integer code;
    private String title;

    public static EnumExperimentEventState of(Integer code){
        return Arrays.stream(EnumExperimentEventState.values())
                .filter(i->i.getCode().equals(code))
                .findFirst()
                .orElse(EnumExperimentEventState.INIT);
    }



}
