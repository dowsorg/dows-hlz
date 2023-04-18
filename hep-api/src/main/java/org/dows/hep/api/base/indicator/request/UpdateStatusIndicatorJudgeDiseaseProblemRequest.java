package org.dows.hep.api.base.indicator.request;

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
@Schema(name = "UpdateStatusIndicatorJudgeDiseaseProblem 对象", title = "更改启用状态")
public class UpdateStatusIndicatorJudgeDiseaseProblemRequest{
    @Schema(title = "判断指标健康指导分布式Id")
    private String indicatorJudgeHealthGuidanceId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
