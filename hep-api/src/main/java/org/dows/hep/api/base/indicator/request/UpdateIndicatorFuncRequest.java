package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "UpdateIndicatorFunc 对象", title = "更新指标功能")
public class UpdateIndicatorFuncRequest implements Serializable {
    @Schema(title = "指标功能分布式ID")
    @ApiModelProperty(required = true, value = "指标功能的分布式id，在修改的时候必备")
    private String indicatorFuncId;

    @Schema(title = "功能名称")
    @ApiModelProperty(required = true)
    private String name;

    @Schema(title = "操作提示")
    private String operationTip;

    @Schema(title = "对话提示")
    private String dialogTip;


}
