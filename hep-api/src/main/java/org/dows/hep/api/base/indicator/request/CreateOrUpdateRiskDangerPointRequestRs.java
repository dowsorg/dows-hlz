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
public class CreateOrUpdateRiskDangerPointRequestRs implements Serializable {
  @Schema(title = "分布式ID")
  private String riskDangerPointId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "死亡模型ID")
  @ApiModelProperty(required = true)
  private String riskDeathModelId;

  @Schema(title = "分布式ID")
  @ApiModelProperty(required = true)
  private String indicatorInstanceId;

  @Schema(title = "公式")
  @ApiModelProperty(required = true)
  private String expression;
}
