package org.dows.hep.api.user.organization.response;

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
@Schema(name = "OrganizationFuns 对象", title = "机构功能")
public class OrganizationFunsResponse {
    @Schema(title = "机构ID")
    private String orgId;

    @Schema(title = "功能|菜单名称")
    private String functionName;

    @Schema(title = "功能图标")
    private String functionIcon;

    @Schema(title = "机构功能ID")
    private String caseOrgFunctionId;


}
