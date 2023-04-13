package org.dows.hep.api.organization.user.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;
import java.math.BigDecimal;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "CaseOrgFee 对象", title = "账户ID")
public class CaseOrgFeeRequest{
    @Schema(title = "机构ID")
    private String orgId;

    @Schema(title = "")
    private String caseOrgFeeId;


}
