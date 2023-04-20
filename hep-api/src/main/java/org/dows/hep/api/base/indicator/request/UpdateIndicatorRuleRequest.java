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
@Schema(name = "UpdateIndicatorRule 对象", title = "更改指标规则对象")
public class UpdateIndicatorRuleRequest{
    @Schema(title = "分布式ID")
    private String indicatorRuleId;

    @Schema(title = "指标或变量ID")
    private String variableId;

    @Schema(title = "最小值")
    private String min;

    @Schema(title = "最大值")
    private String max;

    @Schema(title = "默认值")
    private String def;

    @Schema(title = "描述")
    private String descr;


}
