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
@Schema(name = "IndicatorRef 对象", title = "指标引用列表")
public class IndicatorRefResponse{
    @Schema(title = "引用这个指标的指标ID")
    private String refIndicatorId;


}
