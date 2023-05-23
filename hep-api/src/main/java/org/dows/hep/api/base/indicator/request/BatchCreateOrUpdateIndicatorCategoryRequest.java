package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@Schema(name = "BatchCreateOrUpdateIndicatorCategoryRequest 对象", title = "批量创建或修改指标类别对象")
public class BatchCreateOrUpdateIndicatorCategoryRequest {
  @Schema(title = "应用ID")
  @ApiModelProperty(required = true)
  private String appId;

  @Schema(title = "父ID")
  @ApiModelProperty(required = true, value = "父类别的分布式id，没有父类别就为null")
  private String pid;

  @Schema(title = "批量创建或修改指标类别DTO")
  @ApiModelProperty(required = true, value = "批量创建或修改的指标类别列表")
  private List<BatchCreateOrUpdateIndicatorCategoryDTO> batchCreateOrUpdateIndicatorCategoryDTOList;
}
