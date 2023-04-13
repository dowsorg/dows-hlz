package org.dows.hep.api.casus.tenant.request;

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
@Schema(name = "CaseSchemeSearch 对象", title = "案例方案搜索")
public class CaseSchemeSearchRequest{
    @Schema(title = "类别ID")
    private String categId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "方案名称")
    private String schemeName;


}
