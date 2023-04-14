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
@Schema(name = "UpdateStatusIndicatorJudgeHealthGuidance 对象", title = "")
public class UpdateStatusIndicatorJudgeHealthGuidanceRequest {
    @Schema(title = "判断指标健康指导分布式Id")
    private String IndicatorJudgeHealthGuidanceId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
