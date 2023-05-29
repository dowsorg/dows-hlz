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
public class CreateOrUpdateIndicatorCategoryRequestRs implements Serializable {
  @Schema(title = "应用ID")
  @ApiModelProperty(required = true)
  private String appId;

  @Schema(title = "父ID")
  @ApiModelProperty(required = true, value = "父类别的分布式id，没有父类别就为null")
  private String pid;

  @Schema(title = "指标类别分布式id")
  @ApiModelProperty(value = "本次新增（那就不存在）或修改（存在）的指标类别的id")
  private String indicatorCategoryId;

  @Schema(title = "指标类别名称")
  @ApiModelProperty(required = true, value = "指标类别的名称")
  private String categoryName;

  private Integer seq;
}
