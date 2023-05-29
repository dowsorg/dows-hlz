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
public class CreateOrUpdateIndicatorViewBaseInfoDescrRefRequestRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorViewBaseInfoDescRefId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "指标描述表ID")
  @ApiModelProperty(required = true)
  private String indicatorViewBaseInfoDescId;

  @Schema(title = "指标ID")
  @ApiModelProperty(required = true)
  private String indicatorInstanceId;

  @Schema(title = "展示顺序")
  @ApiModelProperty(required = true)
  private Integer seq;
}
