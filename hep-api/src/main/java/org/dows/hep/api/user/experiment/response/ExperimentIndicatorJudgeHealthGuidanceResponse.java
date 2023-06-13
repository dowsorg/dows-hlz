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
 * @date 2023/6/5 14:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExperimentIndicatorJudgeHealthGuidance 对象", title = "判断指标危险因素")
public class ExperimentIndicatorJudgeHealthGuidanceResponse implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "实验判断健康指导分布式ID")
    private String experimentJudgeHealthGuidanceId;

    @Schema(title = "教师端判断健康指导ID")
    private String indicatorJudgeHealthGuidanceId;

    @Schema(title = "健康指导名称")
    private String name;

    @Schema(title = "指标分类ID")
    private String experimentIndicatorCategoryId;
}
