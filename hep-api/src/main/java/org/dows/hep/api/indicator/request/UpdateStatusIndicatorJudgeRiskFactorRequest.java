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
@Schema(name = "UpdateStatusIndicatorJudgeRiskFactor 对象", title = "更改启用状态")
public class UpdateStatusIndicatorJudgeRiskFactorRequest{
    @Schema(title = "判断指标危险因素分布式Id")
    private  IndicatorJudgeRiskFactorId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
