package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@Schema(name = "CreateIndicatorFunc 对象", title = "创建指标功能")
@Builder
public class CreateIndicatorFuncRequest implements Serializable {
    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;

    @Schema(title = "指标功能父类别分布式ID")
    @ApiModelProperty(required = true, value = "此功能点的父类别id，比如查看指标下的功能点，那么这个父类别id就是查看指标这个类别的的分布式id")
    private String pid;

    @Schema(title = "具体功能类型分布式ID")
    @ApiModelProperty(required = true, value = "此功能点的指标类别id，比如查看指标的基本信息这个功能点，那么此id为查看指标的基本信息的这个类别的分布式id")
    private String indicatorCategoryId;

    @Schema(title = "功能名称")
    @ApiModelProperty(required = true)
    private String name;

    @Schema(title = "操作提示")
    private String operationTip;

    @Schema(title = "对话提示")
    private String dialogTip;


}
