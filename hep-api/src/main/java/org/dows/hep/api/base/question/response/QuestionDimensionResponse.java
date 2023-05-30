package org.dows.hep.api.base.question.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "QuestionDimension 对象", title = "问题维度Response")
public class QuestionDimensionResponse{

    @Schema(title = "问题ID")
    private String questionInstanceId;

    @Schema(title = "问题集维度ids")
    private List<String> questionSectionDimensionIds;
}
