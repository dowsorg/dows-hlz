package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 机构费用类型
 * @author : wuzl
 * @date : 2023/6/5 10:37
 */
@AllArgsConstructor
@Getter
public enum EnumOrgFeeType {
    NONE("","NA"),
    GHF("GHF","挂号费"),
    BXF("BXF","保险费"),
    ;

    private String code;
    private String name;

    public static EnumOrgFeeType of(String code){
        return Arrays.stream(EnumOrgFeeType.values())
                .filter(i->i.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(EnumOrgFeeType.NONE);
    }

}
