package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 营养成分
 *
 * @author : wuzl
 * @date : 2023/5/18 9:10
 */
@AllArgsConstructor
@Getter
public enum EnumFoodNutrient {
    NONE("NA","", BigDecimal.ZERO),
    PROTEIN("蛋白质","g",new BigDecimal(4)),
    FAT("脂肪","g",new BigDecimal(9)),
    CHO("碳水化合物","g",new BigDecimal(4)),
    ENERGY("能量","kcal",BigDecimal.ONE),
    ;

    private String name;
    private String unit;

    /**
     * 热量系数
     */
    private BigDecimal energyRate;


    /**
     * 计算热量
     * @param weight
     * @return
     */
    public BigDecimal calcEnergy(BigDecimal weight){
        return energyRate.multiply(Optional.ofNullable(weight).orElse(BigDecimal.ZERO));
    }

    public static EnumFoodNutrient of(String name){
        return Arrays.stream(EnumFoodNutrient.values())
                .filter(i->i.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(NONE);
    }



    public final static List<EnumFoodNutrient> BASENutrients3 =Arrays.asList(PROTEIN,FAT,CHO);

    public final static List<String> BASENutrientNames3 =Arrays.asList(PROTEIN.getName(),FAT.getName(),CHO.getName());
    public final static List<String> BASENutrientNames4 =Arrays.asList(PROTEIN.getName(),FAT.getName(),CHO.getName(),ENERGY.getName());



}
