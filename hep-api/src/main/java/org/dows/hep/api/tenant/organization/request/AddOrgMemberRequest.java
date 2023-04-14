package org.dows.hep.api.tenant.organization.request;

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
@Schema(name = "AddOrgMember 对象", title = "")
public class AddOrgMemberRequest {
    @Schema(title = "账户ID")
    private String AccountId;

    @Schema(title = "机构ID")
    private String OrgId;


}
