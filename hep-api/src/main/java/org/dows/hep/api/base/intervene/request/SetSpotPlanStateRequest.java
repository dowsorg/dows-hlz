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
@Schema(name = "SetSpotPlanState 对象", title = "启用、禁用运动方案")
public class SetSpotPlanStateRequest{
    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;
    @Schema(title = "运动方案id")
    @ApiModelProperty(required = true)
    private String sportPlanId;

    @Schema(title = "状态 1-启用 0-停用")
    @ApiModelProperty(required = true)
    private Integer state;


}
