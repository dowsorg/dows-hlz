package org.dows.hep.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : wuzl
 * @date : 2023/4/21 13:48
 */
@AllArgsConstructor
@Getter
public enum EnumCategFamily {

    FOODMaterial("food.material","食材类别"),
    FOODDishes("food.dishes","菜肴类别"),
    FOODCookBook("food.cookbook","菜谱类别"),

    SPORTItem("sport.item","运动项目类别"),
    TreatItem1("","心理干预类别"),
    ;
    private String code;
    private String name;

    public static EnumCategFamily of(String code){
        for(EnumCategFamily item: EnumCategFamily.values()){
            if(item.getCode().equalsIgnoreCase(code)){
                return item;
            }
        }
        return null;
    }



}
