package org.dows.hep.api.base.intervene.response;

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
@NoArgsConstructor
@Accessors(chain = true)
@Schema(name = "FoodMaterial 对象", title = "食材列表")
public class FoodMaterialResponse{

    @Schema(title = "数据库id")
    private Long id;
    @Schema(title = "食材id")
    private String foodMaterialId;

    @Schema(title = "食材名称")
    private String foodMaterialName;

    @Schema(title = "一级分类id")
    private String categIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;

    @Schema(title = "蛋白质每100g")
    private String protein;

    @Schema(title = "碳水每100g")
    private String cho;

    @Schema(title = "脂肪每100g")
    private String fat;

    @Schema(title = "总能量每100g")
    private String energy;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;




}
