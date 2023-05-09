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
public enum EnumFoodDetailType {
    NONE(0,"NA"),
    MATERIAL(1,"食材"),
    DISHES(2,"菜肴"),
    ;
    private Integer code;
    private String name;

    public static EnumFoodDetailType of(Integer code){
        for(EnumFoodDetailType item: EnumFoodDetailType.values()){
           if(ObjectUtils.nullSafeEquals(code,item.getCode())){
               return item;
           }
        }
        return NONE;
    }
}
