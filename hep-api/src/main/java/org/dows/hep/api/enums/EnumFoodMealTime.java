package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

/**
 * 食物明细类型
 *
 * @author : wuzl
 * @date : 2023/5/6 11:34
 */
@AllArgsConstructor
@Getter
public enum EnumFoodMealTime {
    NONE(0,"NA"),
    BREAKFAST(1,"早餐"),
    BREAKFASTPlus(2,"早加餐"),
    LUNCH(3,"午餐"),
    LUNCHPlus(4,"午加餐"),
    DINNER(5,"晚餐"),
    DINNERPlus(6,"晚加餐"),
    ;
    private Integer code;
    private String name;

    public static EnumFoodMealTime of(Integer code){
        for(EnumFoodMealTime item: EnumFoodMealTime.values()){
           if(ObjectUtils.nullSafeEquals(code,item.getCode())){
               return item;
           }
        }
        return NONE;
    }
}
