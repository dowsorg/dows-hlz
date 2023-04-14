package org.dows.hep.api.base.risk.response;

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
@Schema(name = "RiskModel 对象", title = "风险模型")
public class RiskModelResponse {
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "分布式ID")
    private String riskCategoryId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "风险模型ID")
    private String riskModelId;

    @Schema(title = "模型名称")
    private String modelName;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;

    @Schema(title = "死亡模型列表")
    private String ListRiskDeathModel;


}
