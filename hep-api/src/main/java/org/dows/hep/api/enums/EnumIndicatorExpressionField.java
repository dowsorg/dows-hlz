package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author runsix
 */
@Getter
@AllArgsConstructor
public enum EnumIndicatorExpressionField {
  DATABASE(0, "数据库"),
  CASE(1, "案例库"),
  ;
  private final Integer field;
  private final String desc;

  public final static Map<Integer, EnumIndicatorExpressionField>  kCodeVEnumIndicatorExpressionFieldMap = new HashMap<>();

  static {
    for (EnumIndicatorExpressionField enumIndicatorExpressionField : EnumIndicatorExpressionField.values()) {
      kCodeVEnumIndicatorExpressionFieldMap.put(enumIndicatorExpressionField.getField(), enumIndicatorExpressionField);
    }
  }

  public static EnumIndicatorExpressionField getByField(Integer field) {
    if (Objects.isNull(field)) {
      return null;
    }
    return kCodeVEnumIndicatorExpressionFieldMap.get(field);
  }
}
