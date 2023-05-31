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
public class CreateOrUpdateIndicatorExpressionRequestRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorExpressionId;

  @Schema(title = "分布式ID")
  @ApiModelProperty(required = true)
  private String principalId;

  @Schema(title = "指标公式与主体关联关系分布式ID")
  private String indicatorExpressionRefId;

  @Schema(title = "应用ID")
  @ApiModelProperty(required = true)
  private String appId;

  @Schema(title = "指标公式细项")
  @ApiModelProperty(required = true)
  private List<CreateOrUpdateIndicatorExpressionItemRequestRs> createOrUpdateIndicatorExpressionItemRequestRsList;

  @Schema(title = "公式上限")
  private CreateOrUpdateIndicatorExpressionItemRequestRs maxCreateOrUpdateIndicatorExpressionItemRequestRs;

  @Schema(title = "公式下限")
  private CreateOrUpdateIndicatorExpressionItemRequestRs minCreateOrUpdateIndicatorExpressionItemRequestRs;
}
