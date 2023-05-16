package org.dows.hep.api.base.question.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "QuestionSectionDimensionResponse 对象", title = "维度集Response")
public class QuestionSectionDimensionResponse{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "维度ID")
    private String questionSectionDimensionId;

    @Schema(title = "维度名称")
    private String dimensionName;

    @Schema(title = "内容")
    private String dimensionContent;

    @Schema(title = "分数")
    private BigDecimal score;
}
