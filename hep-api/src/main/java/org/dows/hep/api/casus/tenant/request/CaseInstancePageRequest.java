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
@Schema(name = "CaseInstancePage 对象", title = "分页请求Request")
public class CaseInstancePageRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例名称")
    private String caseName;


}
