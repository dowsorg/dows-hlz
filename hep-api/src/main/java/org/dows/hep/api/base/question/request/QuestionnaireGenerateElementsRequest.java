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
@Schema(name = "QuestionnaireGenerateElements 对象", title = "试卷自动生成因素")
public class QuestionnaireGenerateElementsRequest{
    @Schema(title = "类别Id")
    private String questionInstanceCategId;

    @Schema(title = "[JSON]题目答题类型[单选|多选|判断|主观|材料]")
    private String questionType;

    @Schema(title = "题数")
    private Integer questionCount;


}
