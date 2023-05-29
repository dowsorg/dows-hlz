package org.dows.hep.api.base.risk.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@Schema(name = "BatchCreateOrUpdateRiskCategoryRequest 对象", title = "批量创建或修改风险类别对象")
public class BatchCreateOrUpdateRiskCategoryRequest {
  @Schema(title = "应用ID")
  @ApiModelProperty(required = true)
  private String appId;

  @Schema(title = "父ID")
  @ApiModelProperty(required = true)
  private String pid;

  @Schema(title = "批量创建或修改风险类别DTO")
  @ApiModelProperty(required = true)
  private List<BatchCreateOrUpdateRiskCategoryDTO> batchCreateOrUpdateRiskCategoryDTOList;
}
