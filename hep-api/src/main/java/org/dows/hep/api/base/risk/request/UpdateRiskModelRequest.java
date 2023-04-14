package org.dows.hep.api.base.risk.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "UpdateRiskModel 对象", title = "更改风险模型")
public class UpdateRiskModelRequest {
    @Schema(title = "风险模型ID")
    private String riskModelId;

    @Schema(title = "风险类别分布式ID")
    private String riskCategoryId;

    @Schema(title = "风险模型名称")
    private String name;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
