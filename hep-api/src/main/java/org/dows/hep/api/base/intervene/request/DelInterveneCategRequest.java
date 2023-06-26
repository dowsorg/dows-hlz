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
@Schema(name = "DelInterveneCateg 对象", title = "删除类别")
public class DelInterveneCategRequest {
    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;

    @Schema(title = "根类别标识，food.material-食材类别；sport.item-运动项目类别  treat.item:指标功能点id -自定义治疗项目...")
    @ApiModelProperty(required = true)
    private String family;

    @Schema(title = "分布式id列表")
    @ApiModelProperty(required = true)
    private List<String> ids;

}
