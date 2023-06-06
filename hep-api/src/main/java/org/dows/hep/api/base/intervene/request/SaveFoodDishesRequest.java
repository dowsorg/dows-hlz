package org.dows.hep.api.base.intervene.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.intervene.vo.FoodMaterialVO;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SaveFoodDishes 对象", title = "菜肴信息")
public class SaveFoodDishesRequest{
    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;

    @Schema(title = "数据库id，新增时为空")
    private Long id;

    @Schema(title = "菜肴id，新增时为空")
    private String foodDishesId;

    @Schema(title = "菜肴名称")
    @ApiModelProperty(required = true)
    private String foodDishesName;

    @Schema(title = "当前分类id")
    @ApiModelProperty(required = true)
    private String interveneCategId;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "食材列表json")
    private List<FoodMaterialVO> materials;


}
