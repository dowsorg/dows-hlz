package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/6/13 15:53
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentIndicatorJudgeHealthProblem 对象", title = "创建实验健康问题")
public class ExperimentIndicatorJudgeHealthProblemRequest {
    @Schema(title = "实验判断健康问题分布式ID")
    private String experimentJudgeHealthProblemId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;
}
