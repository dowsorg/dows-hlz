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
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "分布式ID")
    private String evaluateDimensionExpressionId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "评估问卷ID")
    private String questionnaireId;

    @Schema(title = "维度id")
    private String dimensionId;

    @Schema(title = "维度公式")
    private String expression;


}
