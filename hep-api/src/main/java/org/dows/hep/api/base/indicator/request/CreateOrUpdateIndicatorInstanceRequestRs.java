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
public class CreateOrUpdateIndicatorInstanceRequestRs implements Serializable {
  @ApiModelProperty(required = false, value = "如果是新增则没有，如果是修改就存在")
  private String indicatorInstanceId;

  @Schema(title = "指标类别分布式ID")
  @ApiModelProperty(required = true, value = "手动新建的指标都必须有指标类别id")
  private String indicatorCategoryId;

  @Schema(title = "应用ID")
  @ApiModelProperty(required = true)
  private String appId;

  @Schema(title = "指标名称")
  @ApiModelProperty(required = true)
  private String indicatorName;

  @Schema(title = "是否按照百分比展示")
  private Integer displayByPercent;

  @ApiModelProperty(required = true, value = "指标默认值，当模版使用必须填写")
  private String def;

  private String unit;

  @Schema(title = "0-非关键指标，1-关键指标")
  @ApiModelProperty(required = true)
  private Integer core;

  @Schema(title = "0-非饮食关键指标，1-饮食关键指标")
  @ApiModelProperty(required = true)
  private Integer food;

  @Schema(title = "最小值")
  private String min;

  @Schema(title = "最大值")
  private String max;
}
