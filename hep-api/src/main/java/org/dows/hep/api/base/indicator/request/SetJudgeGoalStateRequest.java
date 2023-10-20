package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : wuzl
 * @date : 2023/10/17 23:50
 */
@Data
@NoArgsConstructor
@Schema(name = "SetJudgeGoalState 对象", title = "启用、禁用管理目标")
public class SetJudgeGoalStateRequest {

    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;
    @Schema(title = "运动项目id")
    @ApiModelProperty(required = true)
    private String indicatorJudgeGoalId;

    @Schema(title = "状态 1-启用 0-停用")
    @ApiModelProperty(required = true)
    private Integer state;
}
