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
public class CaseCreateOrUpdateIndicatorExpressionRequestRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorExpressionId;

  @Schema(title = "分布式ID")
  @ApiModelProperty(required = true)
  private String principalId;

  @Schema(title = "指标公式与主体关联关系分布式ID")
  private String indicatorExpressionRefId;

  @Schema(title = "产生这个指标公式的分布式ID")
  private String reasonId;

  @Schema(title = "应用ID")
  @ApiModelProperty(required = true)
  private String appId;

  @Schema(title = "公式类型，0-条件公式，1-随机公式")
  @ApiModelProperty(required = true, value = "公式类型，0-条件公式，1-随机公式")
  private Integer type;

  @Schema(title = "公式来源")
  @ApiModelProperty(required = true)
  private Integer source;

  @Schema(title = "指标公式细项")
  @ApiModelProperty(required = true)
  private List<CreateOrUpdateIndicatorExpressionItemRequestRs> createOrUpdateIndicatorExpressionItemRequestRsList;

  @Schema(title = "公式上限")
  private CreateOrUpdateIndicatorExpressionItemRequestRs maxCreateOrUpdateIndicatorExpressionItemRequestRs;

  @Schema(title = "公式下限")
  private CreateOrUpdateIndicatorExpressionItemRequestRs minCreateOrUpdateIndicatorExpressionItemRequestRs;
}
