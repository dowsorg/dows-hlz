package org.dows.hep.api.evaluate.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;
import java.math.BigDecimal;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "CreateEvaluateReportManagement 对象", title = "创建评估报告管理对象")
public class CreateEvaluateReportManagementRequest{
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
