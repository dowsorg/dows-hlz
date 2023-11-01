package org.dows.hep.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author : wuzl
 * @date : 2023/9/7 15:51
 */
@Getter
@RequiredArgsConstructor
public enum EnumEvalFuncType {
    INIT(0,"初始"),
    START(1,"开始"),
    PERIODEnd(2,"期数翻转"),
    FUNCTreat(11,"治疗干预"),
    FUNCHealthGuide(12,"健康指导"),
    FUNCFollowup(13,"监测随访"),

    FUNCCommon(21,"通用")

    ;
    private final Integer code;
    private final String name;

    public static boolean isNewPeriod(EnumEvalFuncType funcType){
        return funcType==START
                ||funcType==PERIODEnd;
    }
    public boolean isPeriodEnd(){
        return this==PERIODEnd;
    }
    public static EnumEvalFuncType of(Integer code){
        return Arrays.stream( EnumEvalFuncType.values())
                .filter(i->i.getCode().equals(code))
                .findFirst()
                .orElse(FUNCCommon);
    }
}
