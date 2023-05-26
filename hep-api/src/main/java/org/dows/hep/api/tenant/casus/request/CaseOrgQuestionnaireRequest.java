package org.dows.hep.api.tenant.casus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/5/25 11:41
 */
@Data
@NoArgsConstructor
@Schema(name = "CaseOrgQuestionnaireRequest 对象", title = "机构问卷")
public class CaseOrgQuestionnaireRequest {
    @Schema(title = "案例机构问卷ID")
    private String caseOrgQuestionnaireId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "案例问卷ID")
    private String caseQuestionnaireId;

    @Schema(title = "期数")
    private String periods;
}
