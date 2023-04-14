package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "UpdateStatusIndicatorJudgeHealthProblem 对象", title = "更改启用状态")
public class UpdateStatusIndicatorJudgeHealthProblemRequest {
    @Schema(title = "判断指标健康问题分布式Id")
    private String IndicatorJudgeHealthProblemId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
