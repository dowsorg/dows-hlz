package org.dows.hep.biz.eval.data;

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
    NONE(0,"NA"),
    PERIODEnd(1,"期数翻转"),
    FUNCTreat(2,"治疗干预"),
    FUNCHealthGuide(3,"健康指导"),
    FUNCFollowup(4,"监测随访"),


    ;
    private final Integer code;
    private final String name;

    public static EnumEvalFuncType of(Integer code){
        return Arrays.stream( EnumEvalFuncType.values())
                .filter(i->i.getCode().equals(code))
                .findFirst()
                .orElse(NONE);
    }
}
