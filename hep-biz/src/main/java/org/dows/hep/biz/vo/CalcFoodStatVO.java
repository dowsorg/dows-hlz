package org.dows.hep.biz.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.FoodStatVO;
import org.dows.hep.biz.util.BigDecimalOptional;

/**
 * @author : wuzl
 * @date : 2023/5/18 11:44
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "CalcFoodStatVO 对象", title = "计算饮食统计")
public class CalcFoodStatVO  extends FoodStatVO {

    @JsonIgnore
    @Schema(title = "重量")
    private final BigDecimalOptional weightOptional=BigDecimalOptional.create();

    @JsonIgnore
    @Schema(title = "能量")
    private final BigDecimalOptional energyOptional=BigDecimalOptional.create();
}
