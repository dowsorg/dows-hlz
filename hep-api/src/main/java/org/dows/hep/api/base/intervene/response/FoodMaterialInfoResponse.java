package org.dows.hep.api.base.intervene.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.FoodNutrientVO;
import org.dows.hep.api.base.intervene.vo.InterveneIndicatorVO;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "FoodMaterialInfo 对象", title = "食材信息")
public class FoodMaterialInfoResponse{
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id,新增时为空")
    private Long id;


    @Schema(title = "食材id,新增时为空")
    private String foodMaterialId;

    @Schema(title = "食材名称")
    private String foodMaterialName;


    @Schema(title = "当前分类id")
    private String interveneCategId;

    @Schema(title = "当前分类名称")
    private String categName;

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

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "关联指标列表json")
    private List<InterveneIndicatorVO> indicators;

    @Schema(title = "营养成分列表json")
    private List<FoodNutrientVO> nutrients;


}
