package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author runsix
 */
@Getter
@AllArgsConstructor
public enum EnumIndicatorType {
  USER_CREATED(0, "用户创建"),
  MONEY(1, "用户资金"),
  SEX(2, "性别"),
  HEIGHT(3, "身高"),
  WEIGHT(4, "体重"),
  HEALTH_POINT(5, "健康指数"),
  AGE(6, "年龄"),
  DURATION(7, "持续天数"),
  ;

  public final static Map<Integer, EnumIndicatorType> kTypeVEnumIndicatorTypeMap = new HashMap<>();

  public static EnumIndicatorType of(Integer code){
    return kTypeVEnumIndicatorTypeMap.get(code);
  }

  private final Integer type;
  private final String desc;

  static {
    for (EnumIndicatorType enumIndicatorType:EnumIndicatorType.values()) {
      kTypeVEnumIndicatorTypeMap.put(enumIndicatorType.getType(), enumIndicatorType);
    }
  }
}
