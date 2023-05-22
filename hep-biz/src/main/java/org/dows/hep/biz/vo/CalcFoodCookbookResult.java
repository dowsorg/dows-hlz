package org.dows.hep.biz.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.entity.FoodCookbookNutrientEntity;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/5/21 17:44
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CalcFoodCookbookResult 对象", title = "食谱计算结果")
public class CalcFoodCookbookResult {

    @Schema(title = "计算结果")
    private List<FoodCookbookNutrientEntity> nutrients;

    @Schema(title = "能量占比统计")
    private List<CalcFoodStatVO> statEnergy;
}
