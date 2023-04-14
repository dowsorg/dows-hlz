package org.dows.hep.api.base.evaluate.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "CreateEvaluateReportManagement 对象", title = "创建评估报告管理对象")
public class CreateEvaluateReportManagementRequest {
    @Schema(title = "分布式ID")
    private String evaluateQuestionnaireId;

    @Schema(title = "报告名称")
    private String reportName;

    @Schema(title = "报告说明")
    private String reportDesc;

    @Schema(title = "评估结果")
    private String assessmentResult;

    @Schema(title = "相关建议")
    private String suggestion;

    @Schema(title = "分数段[最小]")
    private Integer minScore;

    @Schema(title = "分数段[最大]")
    private Integer maxScore;


}
