package org.dows.hep.api.tenant.casus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/5/17 17:21
 */
@Data
@NoArgsConstructor
@Schema(name = "CaseQuestionnairePageRequest 对象", title = "案例问卷Request")
public class CaseQuestionnairePageRequest {
    @Schema(title = "页数")
    private Integer pageNo;

    @Schema(title = "页大小")
    private Integer pageSize;

    @Schema(title = "案例ID")
    private String caseInstanceId;
}
