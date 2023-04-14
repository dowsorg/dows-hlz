package org.dows.hep.api.base.intervene.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "FoodCookBook 对象", title = "菜谱信息")
public class FoodCookBookResponse {
    @Schema(title = "食谱id")
    private String foodCookbookId;

    @Schema(title = "食谱名称")
    private String foodCookbookName;

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
