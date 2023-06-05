package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/6/5 14:55
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentIndicatorJudgeRiskFactor 对象", title = "创建实验危险因素")
public class ExperimentIndicatorJudgeRiskFactorRequest {
    @Schema(title = "实验判断危险因素分布式ID")
    String experimentJudgeRiskFactorId;
}
