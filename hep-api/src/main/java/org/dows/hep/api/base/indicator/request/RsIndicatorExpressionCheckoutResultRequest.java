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
public class RsIndicatorExpressionCheckoutResultRequest implements Serializable {
  @Schema(title = "指标公式产生类型")
  private Integer source;

  @Schema(title = "指标公式域")
  private Integer field;

  @Schema(title = "原始结果")
  private String resultRaw;

  @Schema(title = "结果")
  private String resultExpression;

  @Schema(title = "结果参数名字，以英文逗号分割")
  private String resultNameList;

  @Schema(title = "结果参数数值，以英文逗号分割")
  private String resultValList;
}
