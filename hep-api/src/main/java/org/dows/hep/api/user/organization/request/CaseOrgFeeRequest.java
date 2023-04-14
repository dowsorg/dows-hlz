package org.dows.hep.api.user.organization.request;

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
@Schema(name = "CaseOrgFee 对象", title = "账户ID")
public class CaseOrgFeeRequest {
    @Schema(title = "机构ID")
    private String orgId;

    @Schema(title = "")
    private String caseOrgFeeId;


}
