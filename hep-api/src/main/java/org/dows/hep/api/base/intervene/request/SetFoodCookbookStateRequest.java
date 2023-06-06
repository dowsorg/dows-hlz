package org.dows.hep.api.base.intervene.request;

import io.swagger.annotations.ApiModelProperty;
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
@Schema(name = "SetFoodCookbookState 对象", title = "启用、禁用菜谱")
public class SetFoodCookbookStateRequest{
    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;
    @Schema(title = "菜肴id")
    @ApiModelProperty(required = true)
    private String foodCookbookId;

    @Schema(title = "状态 1-启用 0-停用")
    @ApiModelProperty(required = true)
    private Integer state;


}
