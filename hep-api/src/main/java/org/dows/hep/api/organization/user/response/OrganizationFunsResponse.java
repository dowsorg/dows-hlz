package org.dows.hep.api.organization.user.response;

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
@Schema(name = "OrganizationFuns 对象", title = "机构功能")
public class OrganizationFunsResponse{
    @Schema(title = "机构ID")
    private String orgId;

    @Schema(title = "功能|菜单名称")
    private String functionName;

    @Schema(title = "功能图标")
    private String functionIcon;

    @Schema(title = "机构功能ID")
    private String caseOrgFunctionId;


}
