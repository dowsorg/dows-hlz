package org.dows.hep.api.base.intervene.request;

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
@Schema(name = "SaveFoodNutient 对象", title = "营养指标集合")
public class SaveFoodNutientRequest{
    @Schema(title = "营养指标集合")
    private String ids;


}
