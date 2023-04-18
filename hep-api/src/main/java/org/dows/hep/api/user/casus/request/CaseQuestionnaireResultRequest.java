package org.dows.hep.api.user.casus.request;

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
@Schema(name = "CaseQuestionnaireResult 对象", title = "案例答题结果Request")
public class CaseQuestionnaireResultRequest{
    @Schema(title = "案例问卷ID")
    private String caseQuestionnaireId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "答题记录ID")
    private String questionSectionResultId;

    @Schema(title = "答题者账号ID")
    private String accountId;

    @Schema(title = "答题者Name")
    private String accountName;


}
