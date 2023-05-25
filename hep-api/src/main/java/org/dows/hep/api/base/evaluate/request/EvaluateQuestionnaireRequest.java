package org.dows.hep.api.base.evaluate.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.question.request.QuestionSectionDimensionRequest;
import org.dows.hep.api.base.question.request.QuestionSectionItemRequest;

import java.util.List;

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

    @Schema(title = "问卷ID")
    private String evaluateQuestionnaireId;

    @Schema(title = "类别ID")
    private String evaluateCategId;

    @Schema(title = "问卷名")
    private String evaluateQuestionnaireName;

    @Schema(title = "问卷描述")
    private String evaluateQuestionnaireDesc;

    @Schema(title = "状态[0-关闭|1-启用]")
    private Integer enabled;

    @Schema(title = "问题集合")
    private List<QuestionSectionItemRequest> sectionItemList;

    @Schema(title = "维度集合")
    private List<QuestionSectionDimensionRequest> questionSectionDimensionList;


    // JsonIgnore
    @Schema(title = "创建者账号ID")
    @JsonIgnore
    private String accountId;

    @Schema(title = "创建者Name")
    @JsonIgnore
    private String accountName;

    @Schema(title = "appId")
    @JsonIgnore
    private String appId;

}
