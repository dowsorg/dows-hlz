package org.dows.hep.api.enums;

import lombok.Getter;

import java.util.*;

/**
 * 健康档案指标类型
 * @author : wuzl
 * @date : 2023/9/7 15:51
 */
@Getter

public enum EnumIndicatorDocType {
    NONE(0,null,"NA"),
    HP(1,Arrays.asList(EnumIndicatorType.HEALTH_POINT) ,"健康指数"),
    BASIC(2,null,"体重","BMI","bmi","舒张压","收缩压","心率","空腹血糖","总胆固醇"),
    ENERGY(3,null,"饮食摄入热量","运动消耗热量"),
    ;
    private final Integer code;

    private final Set<EnumIndicatorType> sysTypes=new HashSet<>();
    private final Set<String> indicatorNames=new LinkedHashSet<>();

    EnumIndicatorDocType(Integer code, List<EnumIndicatorType> sysTypes,String... indicatorNames){
        this.code=code;
        if(null!=sysTypes&&sysTypes.size()>0){
            this.sysTypes.addAll(sysTypes);
        }
        if(null!=indicatorNames&&indicatorNames.length>0){
            this.indicatorNames.addAll(Arrays.asList(indicatorNames));
        }

    }

    public static EnumIndicatorDocType of(Integer code){
        return Arrays.stream( EnumIndicatorDocType.values())
                .filter(i->i.getCode().equals(code))
                .findFirst()
                .orElse(NONE);
    }

    public static EnumIndicatorDocType of(EnumIndicatorType type,String indicatorName){
        return Arrays.stream( EnumIndicatorDocType.values())
                .filter(i-> i.sysTypes.contains(type)||i.indicatorNames.contains(indicatorName))
                .findFirst()
                .orElse(NONE);
    }
}
