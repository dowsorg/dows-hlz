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
 * @date 2023/6/5 14:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExperimentIndicatorJudgeRiskFactor 对象", title = "判断指标危险因素")
public class ExperimentIndicatorJudgeRiskFactorResponse implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "实验判断危险因素分布式ID")
    private String experimentJudgeRiskFactorId;

    @Schema(title = "教师端判断危险因素ID")
    private String indicatorJudgeRiskFactorId;

    @Schema(title = "危险因素名称")
    private String name;

    @Schema(title = "指标分类ID")
    private String experimentIndicatorCategoryId;
}
