package org.dows.hep.api.tenant.organization.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "OrgFeeSetting 对象", title = "机构费用设置")
public class OrgFeeSettingRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "机构功能指标点ID")
    private String caseOrgIndicatorId;

    @Schema(title = "案例机构费用ID")
    private String caseOrgFeeId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "机构功能ID")
    private String orgFunctionId;

    @Schema(title = "功能|菜单名称")
    private String functionName;

    @Schema(title = "报销比例")
    private BigDecimal reimburseRatio;

    @Schema(title = "费用")
    private BigDecimal fee;

    @Schema(title = "费用名称")
    private String feeName;

    @Schema(title = "费用Code")
    private String feeCode;


}
