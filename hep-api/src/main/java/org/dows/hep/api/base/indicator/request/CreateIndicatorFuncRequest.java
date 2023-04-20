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
@Schema(name = "CreateIndicatorFunc 对象", title = "创建指标功能")
public class CreateIndicatorFuncRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标类别分布式ID")
    private String indicatorCategoryId;

    @Schema(title = "功能名称")
    private String name;

    @Schema(title = "操作提示")
    private String operationTip;

    @Schema(title = "对话提示")
    private String dialogTip;


}
