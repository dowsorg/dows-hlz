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
@Schema(name = "EvaluateDimensionExpression 对象", title = "评估维度公式")
public class EvaluateDimensionExpressionResponse {
    @Schema(title = "表达式ID")
    private String evaluateDimensionExpressionId;

    @Schema(title = "评估问卷分布式ID")
    private String evaluateQuestionnaireId;

    @Schema(title = "维度id")
    private String dimensionId;

    @Schema(title = "维度公式")
    private String expression;
}
