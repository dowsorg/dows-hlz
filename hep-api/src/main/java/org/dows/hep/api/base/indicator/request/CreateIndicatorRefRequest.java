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
@Schema(name = "CreateIndicatorRef 对象", title = "创建指标引用对象")
public class CreateIndicatorRefRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标ID")
    private String indicatorInstanceIdId;

    @Schema(title = "引用这个指标的指标ID")
    private String refIndicatorId;


}
