package org.dows.hep.api.indicator.request;

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
@Schema(name = "CreateIndicatorPrincipalRef 对象", title = "创建指标主体关联关系")
public class CreateIndicatorPrincipalRefRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标值ID")
    private String indicatorValId;

    @Schema(title = "主体ID")
    private String principalId;


}
