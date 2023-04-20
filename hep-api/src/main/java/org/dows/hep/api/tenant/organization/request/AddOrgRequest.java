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
@Schema(name = "AddOrg 对象", title = "机构-实例")
public class AddOrgRequest{
    @Schema(title = "机构名称")
    private String orgName;

    @Schema(title = "机构头像")
    private String profile;

    @Schema(title = "开启功能")
    private String checkjButton;

    @Schema(title = "挂号费")
    private BigDecimal registrationFee;

    @Schema(title = "保险费")
    private BigDecimal insuranceFee;

    @Schema(title = "报销比例")
    private BigDecimal reimbursementRatio;

    @Schema(title = "机构操作手册")
    private String operationManual;

    @Schema(title = "应用ID")
    private String appId;


}
