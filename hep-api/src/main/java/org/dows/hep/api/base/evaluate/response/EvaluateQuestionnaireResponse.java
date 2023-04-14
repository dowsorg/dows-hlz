package org.dows.hep.api.base.evaluate.response;

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
@Schema(name = "EvaluateQuestionnaire 对象", title = "评估问卷")
public class EvaluateQuestionnaireResponse {
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "分布式ID")
    private String evaluateQuestionnaireId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "问题集")
    private String questionSectionId;


}
