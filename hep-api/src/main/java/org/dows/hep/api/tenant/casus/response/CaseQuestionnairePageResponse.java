package org.dows.hep.api.tenant.casus.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/5/17 17:23
 */
@Data
@NoArgsConstructor
@Schema(name = "CaseSchemePageResponse 对象", title = "案例方案分页 Response")
public class CaseQuestionnairePageResponse {
    @Schema(title = "案例问卷ID")
    private String caseQuestionnaireId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "试卷名称")
    private String questionSectionName;

    @Schema(title = "答题位置-期数")
    private String periods;

    @Schema(title = "题数")
    private Integer questionCount;

    @Schema(title = "题型结构")
    private String questionSectionStructure;

}
