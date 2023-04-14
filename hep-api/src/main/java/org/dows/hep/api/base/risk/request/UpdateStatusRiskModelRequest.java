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
@Schema(name = "UpdateStatusRiskModel 对象", title = "更改启用状态")
public class UpdateStatusRiskModelRequest {
    @Schema(title = "风险模型ID")
    private String riskModelId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
