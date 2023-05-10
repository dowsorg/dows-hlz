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
@Schema(name = "DelFoodMaterial 对象", title = "删除食材")
public class DelFoodMaterialRequest{
    @Schema(title = "分布式食材id列表")
    private List<String> ids;


}
