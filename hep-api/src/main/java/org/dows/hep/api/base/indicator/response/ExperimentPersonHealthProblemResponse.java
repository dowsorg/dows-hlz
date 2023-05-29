package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jx
 * @date 2023/5/29 14:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExperimentPersonHealthProblem 对象", title = "实验人物-判断操作-三级无报告")
public class ExperimentPersonHealthProblemResponse implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "判断操作-三级无报告ID")
    private String indicatorJudgeHealthProblemId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "健康问题名称")
    private String name;

    @Schema(title = "健康问题类别名称")
    private String healthProbleamCategoryName;
}
