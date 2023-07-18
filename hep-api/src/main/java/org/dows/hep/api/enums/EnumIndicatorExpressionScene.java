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
  EXPERIMENT_RE_CALCULATE(1, "实验重新计算指标"),
  PHYSICAL_EXAM(3, "体格检查"),
  SUPPORT_EXAM(4, "辅助检查"),
  RISK_MODEL(5, "死亡模型"),
  DATABASE_CREATE_OR_UPDATE_INDICATOR_EXPRESSION(6, "数据库创建或更新指标公式"),
  CASE_RE_CALCULATE(7, "案例重新计算指标"),
  CASE_CALCULATE_HEALTH_POINT(8, "案例计算健康指数"),
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
