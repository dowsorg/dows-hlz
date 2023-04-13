package org.dows.hep.api.indicator.response;

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
@Schema(name = "IndicatorPrincipalRef 对象", title = "指标主体关联关系")
public class IndicatorPrincipalRefResponse{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标值ID")
    private String indicatorValId;

    @Schema(title = "主体ID")
    private String principalId;


}
