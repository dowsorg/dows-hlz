package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

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
