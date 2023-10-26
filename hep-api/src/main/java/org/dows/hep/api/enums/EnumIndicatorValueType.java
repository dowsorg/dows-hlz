package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.ObjectUtils;

/**
 * @author : wuzl
 * @date : 2023/10/25 23:59
 */
@AllArgsConstructor
@Getter
public enum EnumIndicatorValueType {
    NONE(null,"默认值"),
    STRING(0,"字符串"),
    INTEGER(1,"整数"),
    DECIMAL(2,"小数"),
    ;
    private Integer code;
    private String name;

    public static EnumIndicatorValueType of(Integer code){
        for(EnumIndicatorValueType item: EnumIndicatorValueType.values()){
            if(ObjectUtils.nullSafeEquals(code,item.getCode())){
                return item;
            }
        }
        return NONE;
    }
}
