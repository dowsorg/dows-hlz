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
@Schema(name = "RiskCategory 对象", title = "风险类别")
public class RiskCategoryResponse {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "分类名称")
    private String riskCategoryName;


}
