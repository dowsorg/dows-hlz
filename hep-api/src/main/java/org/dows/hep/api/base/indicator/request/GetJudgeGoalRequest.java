package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/10/17 23:49
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "GetJudgeGoal 对象", title = "获取管理目标详情")
public class GetJudgeGoalRequest {
    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;
    @Schema(title = "运动项目id")
    @ApiModelProperty(required = true)
    private String indicatorJudgeGoalId;
}
