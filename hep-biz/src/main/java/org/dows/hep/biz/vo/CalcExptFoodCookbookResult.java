package org.dows.hep.biz.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.FoodCookbookDetailVO;
import org.dows.hep.api.base.intervene.vo.FoodStatVO;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/5/18 11:35
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CalcExptFoodDetailVO 对象", title = "食物明细")
public class CalcExptFoodCookbookResult  {

    @Schema(title = "序列化版本")
    private Integer jsonVer=1;
    @Schema(title = "总能量")
    private String energy;
    @Schema(title = "食材、菜肴列表json")
    private List<FoodCookbookDetailVO> details;

    @Schema(title = "营养统计")
    private List<FoodStatVO> statEnergy;

    @Schema(title = "膳食结构")
    private List<FoodStatVO>  statCateg;

    @Schema(title = "餐次能量统计")
    private List<CalcFoodMealTimeStatVO> statMealEnergy;
}
