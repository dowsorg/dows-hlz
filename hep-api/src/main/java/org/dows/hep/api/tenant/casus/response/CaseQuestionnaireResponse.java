package org.dows.hep.api.tenant.casus.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.api.tenant.casus.CaseQuestionSelectModeEnum;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "CaseQuestionnaire 对象", title = "案例问卷Response")
public class CaseQuestionnaireResponse{
    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "案例问卷ID")
    private String caseQuestionnaireId;

    @Schema(title = "试卷名称")
    private String questionSectionName;

    @Schema(title = "期数")
    private String periods;

    @Schema(title = "期数排序")
    private Integer periodSequence;

    @Schema(title = "添加方式")
    private CaseQuestionSelectModeEnum addType;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "题数")
    private Integer questionCount;

    @Schema(title = "题型结构")
    private String questionSectionStructure;

    @Schema(title = "问题集合")
    private QuestionSectionResponse questionSectionResponse;

}
