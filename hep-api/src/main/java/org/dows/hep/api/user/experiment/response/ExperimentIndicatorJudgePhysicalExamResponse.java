package org.dows.hep.api.user.experiment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/6/5 17:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExperimentIndicatorJudgePhysicalExam 对象", title = "判断指标体格检查")
public class ExperimentIndicatorJudgePhysicalExamResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "实验判断体格检查分布式ID")
    private String experimentJudgePhysicalExamId;

    @Schema(title = "教师端判断体格检查ID")
    private String indicatorJudgePhysicalExamId;

    @Schema(title = "体格检查名称")
    private String name;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;
}
