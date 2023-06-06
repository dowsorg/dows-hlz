package org.dows.hep.biz.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.biz.util.BigDecimalOptional;

/**
 * @author : wuzl
 * @date : 2023/5/6 9:45
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FoodMealTimeStatVO 对象", title = "餐次能量统计")

public class CalcFoodMealTimeStatVO {
    @Schema(title = "进餐时间，1-早|2-早加|3-午|4-午加|5-晚|6-晚加")
    private Integer mealTime;
    @Schema(title = "能量")
    private String energy;

    @Schema(title = "能量占比")
    private String energyRate;

    @JsonIgnore
    @Schema(title = "重量")
    private final BigDecimalOptional weightOptional=BigDecimalOptional.create();

    @JsonIgnore
    @Schema(title = "能量")
    private final BigDecimalOptional energyOptional=BigDecimalOptional.create();


}
