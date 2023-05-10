package org.dows.hep.api.base.intervene.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "FoodDishes 对象", title = "菜肴列表")
public class FoodDishesResponse{
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "菜肴id")
    private String foodDishesId;

    @Schema(title = "菜肴名称")
    private String foodDishesName;

    @Schema(title = "一级分类id")
    private String categIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;

    @Schema(title = "父分类id路径")
    private String categIdPath;

    @Schema(title = "父分类名称路径")
    private String categNamePath;

    @Schema(title = "蛋白质每100g")
    private String protein;

    @Schema(title = "碳水每100g")
    private String cho;

    @Schema(title = "脂肪每100g")
    private String fat;

    @Schema(title = "总能量每100g")
    private String energy;

    @Schema(title = "食材含量描述")
    private String materialsDesc;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;


}
