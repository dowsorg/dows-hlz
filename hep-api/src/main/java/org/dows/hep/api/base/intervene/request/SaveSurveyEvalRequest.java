package org.dows.hep.api.base.intervene.request;

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
@Schema(name = "SaveSurveyEval 对象", title = "保存问卷维度公式")
public class SaveSurveyEvalRequest{
    @Schema(title = "分布式id")
    private String surveyId;

    @Schema(title = "维度公式json")
    private String expressions;


}
