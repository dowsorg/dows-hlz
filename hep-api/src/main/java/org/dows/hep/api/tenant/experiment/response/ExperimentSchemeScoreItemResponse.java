package org.dows.hep.api.tenant.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExperimentSchemeScoreItemResponse 对象", title = "评分Item表")
public class ExperimentSchemeScoreItemResponse {
    @Schema(title = "方案设计评分ID")
    private String experimentSchemeScoreId;

    @Schema(title = "方案设计评分ItemId")
    private String experimentSchemeScoreItemId;

    @Schema(title = "维度名称")
    private String dimensionName;

    @Schema(title = "内容")
    private String dimensionContent;

    @Schema(title = "分数最小值")
    private Float minScore;

    @Schema(title = "分数最大值")
    private Float maxScore;

    @Schema(title = "最终得分")
    private Float score;
}
