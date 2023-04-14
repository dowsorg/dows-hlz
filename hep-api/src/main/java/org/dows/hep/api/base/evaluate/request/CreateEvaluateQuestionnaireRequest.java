package org.dows.hep.api.base.evaluate.request;

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
@Schema(name = "CreateEvaluateQuestionnaire 对象", title = "创建评估问卷")
public class CreateEvaluateQuestionnaireRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "问题集")
    private String questionSectionId;


}
