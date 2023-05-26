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
public class CreateOrUpdateRiskModelRequestRs implements Serializable {
  @Schema(title = "风险模型ID")
  private String riskModelId;

  @Schema(title = "应用ID")
  @ApiModelProperty(required = true)
  private String appId;

  @Schema(title = "分布式ID")
  @ApiModelProperty(required = true)
  private String riskCategoryId;

  @Schema(title = "模型名称")
  @ApiModelProperty(required = true)
  private String name;

  @ApiModelProperty(required = true)
  @Schema(title = "0-禁用，1-启用")
  private Integer status;

  @Schema(title = "死亡模型列表")
  @ApiModelProperty(required = true)
  private List<CreateOrUpdateRiskDeathModelRequestRs> createOrUpdateRiskDeathModelRequestRsList;
}
