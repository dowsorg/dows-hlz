package org.dows.hep.api.base.evaluate.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "EvaluateQuestionnairePageResponse 对象", title = "评估问卷分页返回")
public class EvaluateQuestionnairePageResponse {
    @Schema(title = "问卷ID")
    private String evaluateQuestionnaireId;

    @Schema(title = "类别ID")
    private String evaluateCategId;

    @Schema(title = "类别名")
    private String evaluateCategName;

    @Schema(title = "问卷名")
    private String evaluateQuestionnaireName;

    @Schema(title = "状态[0-关闭|1-启用]")
    private Integer enabled;
}
