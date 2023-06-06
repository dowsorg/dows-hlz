package org.dows.hep.biz.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.FoodDetailVO;
import org.dows.hep.api.enums.EnumFoodMealTime;
import org.dows.hep.biz.util.BigDecimalOptional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/5/18 11:35
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CalcFoodDetailVO 对象", title = "食物明细")
public class CalcFoodDetailVO extends FoodDetailVO {

    @JsonIgnore
    @Schema(title = "重量")
    private final BigDecimalOptional weightOptional=BigDecimalOptional.create();

    @JsonIgnore
    @Schema(title = "一级类别")
    private String categIdLv1;

    @JsonIgnore
    @Schema(title = "餐次统计")
    private final Map<EnumFoodMealTime,CalcFoodMealTimeStatVO> mapMeals=new HashMap<>();


}
