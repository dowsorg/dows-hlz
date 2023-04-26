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
@Schema(name = "QuestionSectionResponse 对象", title = "问题集Response")
public class QuestionSectionResponse {

    @Schema(title = "问题集合")
    private List<QuestionSectionItemResponse> sectionItemList;

    @Schema(title = "维度集合")
    private List<QuestionSectionDimensionResponse> questionSectionDimensionList;
}
