package org.dows.hep.api.user.experiment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jx
 * @date 2023/6/5 14:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "IndicatorJudgeDiseaseProblem 对象", title = "判断指标疾病问题")
public class ExperimentIndicatorJudgeDiseaseProblemResponse implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "实验判断疾病问题分布式ID")
    private String experimentJudgeDiseaseProblemId;

    @Schema(title = "教师端判断疾病问题ID")
    private String indicatorJudgeDiseaseProblemId;

    @Schema(title = "疾病问题名称")
    private String name;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;
}
