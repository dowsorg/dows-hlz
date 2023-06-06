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
@Schema(name = "DelFoodCookbook 对象", title = "删除菜谱")
public class DelFoodCookbookRequest{

    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;
    @Schema(title = "分布式id列表")
    @ApiModelProperty(required = true)
    private List<String> ids;


}
