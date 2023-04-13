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
@Schema(name = "CreateIndicatorRef 对象", title = "创建指标引用对象")
public class CreateIndicatorRefRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标ID")
    private String indicatorInstanceId;

    @Schema(title = "引用这个指标的指标ID")
    private String refIndicatorId;


}
