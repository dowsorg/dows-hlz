package org.dows.hep.api.base.evaluate.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "EvaluateReportManagementResponse 对象", title = "筛选评估报告管理")
public class EvaluateReportManagementResponse{
    @Schema(title = "分布式ID")
    private String evaluateReportManagementId;

    @Schema(title = "评估问卷分布式ID")
    private String evaluateQuestionnaireId;

    @Schema(title = "报告名称")
    private String reportName;

    @Schema(title = "报告说明")
    private String reportDescr;

    @Schema(title = "评估结果")
    private String assessmentResult;

    @Schema(title = "相关建议")
    private String suggestion;

    @Schema(title = "分数段[最小]")
    private Float minScore;

    @Schema(title = "分数段[最大]")
    private Float maxScore;
}
