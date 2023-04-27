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
@Schema(name = "UpdateIndicatorFunc 对象", title = "更新指标功能")
public class UpdateIndicatorFuncRequest{
    @Schema(title = "指标功能分布式ID")
    private String indicatorFuncId;

    @Schema(title = "功能名称")
    private String name;

    @Schema(title = "操作提示")
    private String operationTip;

    @Schema(title = "对话提示")
    private String dialogTip;


}
