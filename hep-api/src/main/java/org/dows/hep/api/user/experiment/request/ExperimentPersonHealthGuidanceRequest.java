package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/5/30 14:05
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentPersonHealthGuidance 对象", title = "实验二级有报告")
public class ExperimentPersonHealthGuidanceRequest {

    @Schema(title = "健康指导分布式ID")
    private String indicatorJudgeHealthGuidanceId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;

    @Schema(title = "健康指导名称")
    private String name;
}
