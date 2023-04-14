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
@Schema(name = "CreateIndicatorJudgeHealthManagementGoal 对象", title = "创建判断指标健管目标")
public class CreateIndicatorJudgeHealthManagementGoalRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "分数")
    private BigDecimal point;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
