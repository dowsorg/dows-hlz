package org.dows.hep.api.base.intervene.response;

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
@Schema(name = "FoodDishesInfo 对象", title = "菜肴信息")
public class FoodDishesInfoResponse{
    @Schema(title = "菜肴id")
    private String foodDishesId;

    @Schema(title = "菜肴名称")
    private String foodDishesName;

    @Schema(title = "当前分类id")
    private String interveneCategId;

    @Schema(title = "当前分类名称")
    private String categName;

    @Schema(title = "分布id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "食材列表json")
    private String materials;

    @Schema(title = "能量占比json")
    private String statEnergy;

    @Schema(title = "膳食结构json")
    private String statCateg;


}
