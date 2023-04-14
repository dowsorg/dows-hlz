package org.dows.hep.api.base.indicator.request;

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
@Schema(name = "UpdateStatusIndicatorJudgeHealthManagementGoal 对象", title = "更改启用状态")
public class UpdateStatusIndicatorJudgeHealthManagementGoalRequest {
    @Schema(title = "判断指标健管目标分布式ID")
    private String IndicatorJudgeHealthManagementGoalId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
