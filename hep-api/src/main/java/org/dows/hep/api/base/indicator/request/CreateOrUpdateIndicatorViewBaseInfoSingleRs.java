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
public class CreateOrUpdateIndicatorViewBaseInfoSingleRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorViewBaseInfoSingleId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  @ApiModelProperty(required = true)
  private String indicatorViewBaseInfoId;

  @Schema(title = "指标ID")
  @ApiModelProperty(required = true)
  private String indicatorInstanceId;

  @Schema(title = "展示顺序")
  @ApiModelProperty(required = true)
  private Integer seq;

}
