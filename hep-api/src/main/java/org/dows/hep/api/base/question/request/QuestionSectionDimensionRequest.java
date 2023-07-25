package org.dows.hep.api.base.question.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
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
    @Size(max = 50, message = "维度名称长度上限为 50")
    private String dimensionName;

    @Schema(title = "内容")
    @Size(max = 500, message = "维度说明长度上限为 500")
    private String dimensionContent;

    @Schema(title = "分数最小值")
    private Float minScore;

    @Schema(title = "分数最大值")
    private Float maxScore;

}
