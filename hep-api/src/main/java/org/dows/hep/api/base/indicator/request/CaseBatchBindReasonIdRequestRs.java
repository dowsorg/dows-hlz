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
public class CaseBatchBindReasonIdRequestRs implements Serializable {
  @Schema(title = "产生这个指标公式的分布式ID")
  private String reasonId;

  @Schema(title = "应用ID")
  @ApiModelProperty(required = true)
  private String appId;

  @Schema(title = "公式来源")
  @ApiModelProperty(required = true)
  private Integer source;

  @Schema(title = "公式id列表")
  @ApiModelProperty(required = true)
  private List<String> caseIndicatorExpressionIdList;
}
