package org.dows.hep.api.base.indicator.request;

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
  private String appId;

  @Schema(title = "父ID")
  private String pid;

  @Schema(title = "批量创建或修改指标类别DTO")
  private List<BatchCreateOrUpdateIndicatorCategoryDTO> batchCreateOrUpdateIndicatorCategoryDTOList;
}
