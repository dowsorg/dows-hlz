package org.dows.hep.api.base.risk.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@Schema(name = "BatchCreateOrUpdateRiskCategory 对象", title = "批量创建或修改风险类别对象")
public class BatchCreateOrUpdateRiskCategoryDTO {
  @Schema(title = "风险类别分布式id")
  private String riskCategoryId;

  @Schema(title = "风险类别名称")
  @ApiModelProperty(required = true)
  private String riskCategoryName;
}
