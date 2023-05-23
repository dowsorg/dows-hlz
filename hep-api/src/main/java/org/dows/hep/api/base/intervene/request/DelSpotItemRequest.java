package org.dows.hep.api.base.intervene.request;

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
@Schema(name = "DelSpotItem 对象", title = "删除运动项目")
public class DelSpotItemRequest{

    @Schema(title = "应用ID")
    private String appId;
    @Schema(title = "分布式id列表")
    private List<String> ids;


}
