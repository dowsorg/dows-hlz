package org.dows.hep.api.base.intervene.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.intervene.vo.FoodNutrientVO;
import org.dows.hep.api.base.intervene.vo.IndicatorExpressionVO;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SaveFoodMaterial 对象", title = "食材信息")
public class SaveFoodMaterialRequest{
    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;

    @Schema(title = "数据库id,新增时为空")
    private Long id;
    @Schema(title = "食材id,新增时为空")
    private String foodMaterialId;

    @Schema(title = "食材名称")
    @ApiModelProperty(required = true)
    private String foodMaterialName;


    @Schema(title = "分类id")
    @ApiModelProperty(required = true)
    private String interveneCategId;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

/*    @Schema(title = "关联指标列表json，已废弃")
    private List<InterveneIndicatorVO> indicators;*/

    @Schema(title = "营养成分列表json")
    private List<FoodNutrientVO> nutrients;

    @Schema(title = "指标公式列表(公式source=3)")
    private List<IndicatorExpressionVO> expresssions;


}
