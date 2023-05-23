package org.dows.hep.api.base.evaluate.request;

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
@Schema(name = "CreateEvaluateDimensionExpression 对象", title = "创建评估维度对象")
public class EvaluateDimensionExpressionRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "评估问卷分布式ID")
    private String evaluateQuestionnaireId;

    @Schema(title = "维度id")
    private String dimensionId;

    @Schema(title = "维度公式")
    private String expression;


}
