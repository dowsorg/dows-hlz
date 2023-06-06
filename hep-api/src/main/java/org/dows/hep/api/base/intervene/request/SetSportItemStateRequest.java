package org.dows.hep.api.base.intervene.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : wuzl
 * @date : 2023/4/24 17:55
 */
@Data
@NoArgsConstructor
@Schema(name = "SetSportItemState 对象", title = "启用、禁用运动项目")
public class SetSportItemStateRequest {
    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;
    @Schema(title = "运动项目id")
    @ApiModelProperty(required = true)
    private String sportItemId;

    @Schema(title = "状态 1-启用 0-停用")
    @ApiModelProperty(required = true)
    private Integer state;
}
