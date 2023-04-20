package org.dows.hep.api.base.indicator.response;

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
@Schema(name = "IndicatorVal 对象", title = "指标值")
public class IndicatorValResponse{
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "分布式ID")
    private String indicatorValId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标ID")
    private String indicatorInstanceId;

    @Schema(title = "当前值")
    private String currentVal;

    @Schema(title = "最小值")
    private String min;

    @Schema(title = "最大值")
    private String max;

    @Schema(title = "默认值")
    private String def;

    @Schema(title = "描述")
    private String descr;

    @Schema(title = "期数")
    private String periods;


}
