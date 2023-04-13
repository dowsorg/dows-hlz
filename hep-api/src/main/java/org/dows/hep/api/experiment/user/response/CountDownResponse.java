package org.dows.hep.api.experiment.user.response;

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
@Schema(name = "CountDown 对象", title = "时间")
public class CountDownResponse{
    @Schema(title = "沙盘时间")
    private Long sandTime;

    @Schema(title = "沙盘时间单位")
    private String sandTimeUnit;

    @Schema(title = "方案时间")
    private Long schemeTime;

    @Schema(title = "方案时间单位")
    private String schemeTimeUnit;


}
