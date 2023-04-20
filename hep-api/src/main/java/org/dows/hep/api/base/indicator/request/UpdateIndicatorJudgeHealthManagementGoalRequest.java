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
@Schema(name = "UpdateIndicatorJudgeHealthManagementGoal 对象", title = "判断指标疾病问题")
public class UpdateIndicatorJudgeHealthManagementGoalRequest{
    @Schema(title = "判断指标健管目标分布式ID")
    private String indicatorJudgeHealthManagementGoalId;

    @Schema(title = "分数")
    private BigDecimal point;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
