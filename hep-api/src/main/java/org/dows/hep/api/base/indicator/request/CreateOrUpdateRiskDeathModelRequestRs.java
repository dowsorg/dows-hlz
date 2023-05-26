package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrUpdateRiskDeathModelRequestRs implements Serializable {
  @Schema(title = "死亡模型ID")
  private String riskDeathModelId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  @ApiModelProperty(required = true)
  private String riskModelId;

  @Schema(title = "死亡原因名称")
  @ApiModelProperty(required = true)
  private String riskDeathReasonName;

  @Schema(title = "死亡概率")
  @ApiModelProperty(required = true)
  private Integer riskDeathProbability;

  @Schema(title = "危险分数")
  @ApiModelProperty(required = true)
  private List<CreateOrUpdateRiskDangerPointRequestRs> createOrUpdateRiskDangerPointRequestRsList;
}
