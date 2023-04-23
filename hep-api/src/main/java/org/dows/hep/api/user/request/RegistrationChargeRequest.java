package org.dows.hep.api.user.request;

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
@Schema(name = "RegistrationCharge 对象", title = "挂号扣费")
public class RegistrationChargeRequest{
    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验ID")
    private String experimentAccountId;

    @Schema(title = "扣除费用")
    private BigDecimal chargeFee;


}
