package org.dows.hep.api.base.risk.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "CreateRiskCategory 对象", title = "创建风险类别")
public class CreateRiskCategoryRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "风险")
    private String riskCategoryName;

    @Schema(title = "展示顺序")
    private Integer seq;


}
