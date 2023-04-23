package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "OrgJudgeGoals 对象", title = "健管目标")
public class OrgJudgeGoalsResponse{
    @Schema(title = "指标目标列表")
    private String goals;

    @Schema(title = "保险状态 0-未购买 1-已购买")
    private Integer insuranceState;


}
