package org.dows.hep.biz.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.entity.FoodDishesNutrientEntity;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/5/21 17:43
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CalcFoodDishResult 对象", title = "菜肴计算结果")
public class CalcFoodDishesResult {
    @Schema(title = "计算结果")
    private List<FoodDishesNutrientEntity> nutrients;

    @Schema(title = "能量占比统计")
    private List<CalcFoodStatVO> statEnergy;


}
