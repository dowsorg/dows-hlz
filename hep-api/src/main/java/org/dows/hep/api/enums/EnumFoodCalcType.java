package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author : wuzl
 * @date : 2023/5/18 9:40
 */
@AllArgsConstructor
@Getter
public enum EnumFoodCalcType {
    ALL(0,"计算所有"),
    ENERGY(1,"计算能量占比"),
    CATEG(2,"计算膳食宝塔")
    ;
    private Integer code;
    private String name;

    public Boolean calcEnergy(){
        return this==EnumFoodCalcType.ALL||this==EnumFoodCalcType.ENERGY;
    }
    public Boolean calcCateg(){
        return this==EnumFoodCalcType.ALL||this==EnumFoodCalcType.CATEG;
    }

    public static EnumFoodCalcType of(Integer code){
        return Arrays.stream(EnumFoodCalcType.values())
                .filter(i->i.getCode().equals(code))
                .findFirst()
                .orElse(EnumFoodCalcType.ALL);
    }

}
