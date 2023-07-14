package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumIndicatorExpressionScene {
  PHYSICAL_EXAM(3, "体格检查"),
  SUPPORT_EXAM(4, "辅助检查"),
  RISK_MODEL(5, "死亡模型"),
  ;

  private final Integer scene;
  private final String name;

  public final static Map<Integer, EnumIndicatorExpressionScene> kTypeVEnumIndicatorExpressionSourceMap = new HashMap<>();

  static {
    for (EnumIndicatorExpressionScene enumIndicatorExpressionSource : EnumIndicatorExpressionScene.values()) {
      kTypeVEnumIndicatorExpressionSourceMap.put(
          enumIndicatorExpressionSource.getScene(), enumIndicatorExpressionSource
      );
    }
  }

  public static EnumIndicatorExpressionScene getByScene(Integer scene) {
    if (Objects.isNull(scene)) {
      return null;
    }
    return kTypeVEnumIndicatorExpressionSourceMap.get(scene);
  }
}
