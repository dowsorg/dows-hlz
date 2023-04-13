package org.dows.hep.api.organization.tenant.request;

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
@Schema(name = "AddOrgMember 对象", title = "")
public class AddOrgMemberRequest{
    @Schema(title = "账户ID")
    private String AccountId;

    @Schema(title = "机构ID")
    private String OrgId;


}
