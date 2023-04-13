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
@Schema(name = "UpdateIndicatorInstance 对象", title = "更新指标")
public class UpdateIndicatorInstanceRequest{
    @Schema(title = "分布式ID")
    private String indicatorInstanceId;

    @Schema(title = "指标名称")
    private String indicatorName;

    @Schema(title = "描述")
    private String descr;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "0-非核心指标，1-核心指标")
    private Integer core;

    @Schema(title = "默认值")
    private String def;


}
