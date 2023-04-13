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
@Schema(name = "CreateEvaluateDimensionExpression 对象", title = "创建评估维度对象")
public class CreateEvaluateDimensionExpressionRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "评估问卷分布式ID")
    private String evaluateQuestionnaireId;

    @Schema(title = "维度id")
    private String dimensionId;

    @Schema(title = "维度公式")
    private String expression;


}
