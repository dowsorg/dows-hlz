package org.dows.hep.api.base.intervene.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "DelRefIndicator 对象", title = "删除食材关联指标")
public class DelRefIndicatorRequest {

    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;
    @Schema(title = "indicatorExpressionId 表达式ID列表")
    @ApiModelProperty(required = true)
    private List<String> ids;


}
