package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@Schema(name = "BatchCreateOrUpdateIndicatorCategory 对象", title = "批量创建或修改指标类别对象")
public class BatchCreateOrUpdateIndicatorCategoryDTO {
  @Schema(title = "指标类别分布式id")
  @ApiModelProperty(value = "本次新增（那就不存在）或修改（存在）的指标类别的id")
  private String indicatorCategoryId;

  @Schema(title = "指标类别名称")
  @ApiModelProperty(required = true, value = "指标类别的名称")
  private String categoryName;
}
