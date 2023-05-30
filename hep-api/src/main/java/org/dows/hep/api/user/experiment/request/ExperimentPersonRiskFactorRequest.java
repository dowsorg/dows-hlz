package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/5/30 10:30
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentPersonRiskFactorRequest 对象", title = "实验二级无报告")
public class ExperimentPersonRiskFactorRequest {

    @Schema(title = "危险因素分布式ID")
    private String indicatorJudgeRiskFactorId;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;

    @Schema(title = "危险因素名称")
    private String name;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;
}
