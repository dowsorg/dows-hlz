package org.dows.hep.api.base.evaluate.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;

import java.util.List;
import java.util.Map;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "EvaluateQuestionnaireResponse 对象", title = "筛选评估问卷")
public class EvaluateQuestionnaireResponse{

    @Schema(title = "问卷ID")
    private String evaluateQuestionnaireId;

    @Schema(title = "类别ID")
    private String evaluateCategId;

    @Schema(title = "问卷名")
    private String evaluateQuestionnaireName;

    @Schema(title = "问卷描述")
    private String evaluateQuestionnaireDesc;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "状态[0-关闭|1-启用]")
    private Integer enabled;

    @Schema(title = "操作提示")
    private String operationPrompt;

    @Schema(title = "对话提示")
    private String tips;

    @Schema(title = "问题集合")
    private List<QuestionSectionItemResponse> sectionItemList;

    @Schema(title = "维度集合")
    private List<QuestionSectionDimensionResponse> questionSectionDimensionList;

    @Schema(title = "维度Map")
    private Map<String, List<QuestionSectionDimensionResponse>> questionSectionDimensionMap;
}
