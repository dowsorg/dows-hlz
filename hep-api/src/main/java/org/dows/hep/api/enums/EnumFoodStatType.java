package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

/**
 * 食物统计类型枚举
 *
 * @author : wuzl
 * @date : 2023/5/6 11:34
 */
@AllArgsConstructor
@Getter
public enum EnumFoodStatType {
    NONE(0,"NA"),
    NUTRIENT(1,"饮食关键指标"),
    FOODCateg(2,"食材类别"),
    ;
    private Integer code;
    private String name;

    public static EnumFoodStatType of(Integer code){
        for(EnumFoodStatType item: EnumFoodStatType.values()){
           if(ObjectUtils.nullSafeEquals(code,item.getCode())){
               return item;
           }
        }
        return NONE;
    }
}
