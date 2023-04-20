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
@Schema(name = "UpdateIndicatorCategory 对象", title = "更新指标类别")
public class UpdateIndicatorCategoryRequest{
    @Schema(title = "指标类别分布式ID")
    private String indicatorCategoryId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "创建或更新指标类别列表")
    private String listCreateOrUpdateIndicatorCategory;


}
