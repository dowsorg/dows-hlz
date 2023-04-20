package org.dows.hep.api.tenant.organization.request;

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
@Schema(name = "OrgFunc 对象", title = "机构功能")
public class OrgFuncRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "机构功能ID")
    private String caseOrgFunctionId;

    @Schema(title = "功能|菜单名称")
    private String functionName;

    @Schema(title = "功能图标")
    private String functionIcon;

    @Schema(title = "机构名称")
    private String orgName;


}
