package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
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
public class CreateOrUpdateIndicatorExpressionItemRequestRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorExpressionItemId;

  @Schema(title = "应用ID")
  @ApiModelProperty(required = true)
  private String appId;

  @Schema(title = "分布式ID")
  private String indicatorExpressionId;

  @Schema(title = "原始条件")
  private String conditionRaw;

  @Schema(title = "条件")
  private String conditionExpression;

  @Schema(title = "条件参数名字，以英文逗号分割")
  private String conditionNameList;

  @Schema(title = "条件参数数值，以英文逗号分割")
  private String conditionValList;

  @Schema(title = "原始结果")
  private String resultRaw;

  @Schema(title = "结果")
  private String resultExpression;

  @Schema(title = "结果参数名字，以英文逗号分割")
  private String resultNameList;

  @Schema(title = "结果参数数值，以英文逗号分割")
  private String resultValList;

  @Schema(title = "优先判断顺序")
  private Integer seq;
}
