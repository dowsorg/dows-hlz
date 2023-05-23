package org.dows.hep.api.base.evaluate.request;

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
@Schema(name = "CreateEvaluateQuestionnaire 对象", title = "创建评估问卷")
public class EvaluateQuestionnaireRequest {

    @Schema(title = "分布式ID")
    private String evaluateQuestionnaireId;

    @Schema(title = "问题集")
    private String questionSectionId;

}
