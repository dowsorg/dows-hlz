package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
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
@Schema(name = "CreateIndicatorInstance 对象", title = "创建指标实例")
public class CreateIndicatorInstanceRequest{
    @Schema(title = "指标类别分布式ID")
    @ApiModelProperty(required = true, value = "手动新建的指标都必须有指标类别id")
    private String indicatorCategoryId;

    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;

    @Schema(title = "指标名称")
    @ApiModelProperty(required = true)
    private String indicatorName;

    @Schema(title = "默认值")
    private String def;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "0-非关键指标，1-关键指标")
    private Integer core;

    @Schema(title = "0-非饮食关键指标，1-饮食关键指标")
    private Integer food;

    @Schema(title = "最小值")
    private String min;

    @Schema(title = "最大值")
    private String max;
}
