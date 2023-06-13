package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/6/13 14:00
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentIndicatorJudgeHealthGuidance 对象", title = "创建实验健康指导")
public class ExperimentIndicatorJudgeHealthGuidanceRequest {

    @Schema(title = "实验判断健康指导分布式ID")
    private String experimentJudgeHealthGuidanceId;
}
