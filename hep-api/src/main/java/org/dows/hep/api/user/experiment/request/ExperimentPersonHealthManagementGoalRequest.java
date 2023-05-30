package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/5/29 17:29
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentPersonHealthManagementGoal 对象", title = "实验人物")
public class ExperimentPersonHealthManagementGoalRequest {
    @Schema(title = "判断指标健管目标分布式ID")
    private String indicatorJudgeHealthManagementGoalId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "范围")
    private String ranges;
}
