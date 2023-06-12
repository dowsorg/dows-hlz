package org.dows.hep.api.tenant.casus.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author fhb
 * @description
 * @date 2023/5/25 11:36
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaseOrgQuestionnaireResponse 对象", title = "机构问卷")
public class CaseOrgQuestionnaireResponse {
    @Schema(title = "案例机构问卷ID")
    private String caseOrgQuestionnaireId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "案例机构Name")
    private String caseOrgName;

    @Schema(title = "案例问卷ID")
    private String caseQuestionnaireId;

    @Schema(title = "案例问卷Name")
    private String questionSectionName;

    @Schema(title = "期数")
    private String periods;
}
