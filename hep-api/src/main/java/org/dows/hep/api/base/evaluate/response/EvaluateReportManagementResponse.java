package org.dows.hep.api.base.evaluate.response;

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
@Schema(name = "EvaluateReportManagement 对象", title = "评估报告管理")
public class EvaluateReportManagementResponse {
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "分布式ID")
    private String EvaluateReportId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "分布式ID")
    private String questionnaireId;

    @Schema(title = "报告名称")
    private String reportName;

    @Schema(title = "报告说明")
    private String reportDescr;

    @Schema(title = "评估结果")
    private String assessmentResult;

    @Schema(title = "相关建议")
    private String suggestion;

    @Schema(title = "分数段[最小]")
    private Integer minScore;

    @Schema(title = "分数段[最大]")
    private Integer maxScore;


}
