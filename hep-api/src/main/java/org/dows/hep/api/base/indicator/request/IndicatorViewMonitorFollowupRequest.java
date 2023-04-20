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
@Schema(name = "IndicatorViewMonitorFollowup 对象", title = "查看指标监测随访类")
public class IndicatorViewMonitorFollowupRequest{
    @Schema(title = "查看指标监测随访类分布式Id")
    private String indicatorViewMonitorFollowupId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
