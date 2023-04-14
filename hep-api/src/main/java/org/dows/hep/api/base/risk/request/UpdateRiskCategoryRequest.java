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
@Schema(name = "UpdateRiskCategory 对象", title = "更改风险类别")
public class UpdateRiskCategoryRequest {
    @Schema(title = "风险")
    private String riskCategoryId;

    @Schema(title = "风险")
    private String riskCategoryName;

    @Schema(title = "展示顺序")
    private Integer seq;


}
