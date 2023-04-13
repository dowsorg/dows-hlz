package org.dows.hep.api.evaluate.response;

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
@Schema(name = "EvaluateDimensionExpression 对象", title = "评估维度公式")
public class EvaluateDimensionExpressionResponse{
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
