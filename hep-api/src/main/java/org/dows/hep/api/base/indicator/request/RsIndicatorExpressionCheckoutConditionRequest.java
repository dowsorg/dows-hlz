package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsIndicatorExpressionCheckoutConditionRequest implements Serializable {
  @Schema(title = "指标公式产生类型")
  private Integer source;

  @Schema(title = "指标公式域")
  private Integer field;

  @Schema(title = "原始条件")
  private String conditionRaw;

  @Schema(title = "条件")
  private String conditionExpression;

  @Schema(title = "条件参数名字，以英文逗号分割")
  private String conditionNameList;

  @Schema(title = "条件参数数值，以英文逗号分割")
  private String conditionValList;
}
