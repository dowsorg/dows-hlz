package org.dows.hep.api.base.question.request;

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
@Schema(name = "QuestionSectionDimension 对象", title = "维度Request")
public class QuestionSectionDimensionRequest{

    @Schema(title = "维度ID")
    private String questionSectionDimensionId;

    @Schema(title = "维度名称")
    private String dimensionName;

    @Schema(title = "内容")
    private String dimensionContent;

    @Schema(title = "分数")
    private Float score;

}
