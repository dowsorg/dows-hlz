package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.intervene.vo.FoodCookbookDetailVO;
import org.dows.hep.api.base.intervene.vo.FoodStatVO;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "ExptFoodCookbook 对象", title = "学生食谱")
public class ExptFoodCookbookResponse {

    @Schema(title = "食材、菜肴列表json")
    private List<FoodCookbookDetailVO> details;

    @Schema(title = "能量占比")
    private List<FoodStatVO> statEnergy;

    @Schema(title = "膳食结构")
    private List<FoodStatVO>  statCateg;


}
