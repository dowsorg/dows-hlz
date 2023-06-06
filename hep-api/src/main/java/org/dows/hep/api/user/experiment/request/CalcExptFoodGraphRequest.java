package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.FoodCookbookDetailVO;
import org.dows.hep.api.base.intervene.vo.FoodDetailVO;
import org.dows.hep.api.core.BaseExptRequest;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/6/3 16:19
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "CalcExptFoodGraph 对象", title = "计算能量占比、膳食宝塔")
public class CalcExptFoodGraphRequest extends BaseExptRequest {
    @Schema(title = "计算类型 0-默认 1-只计算营养统计 2-只计算膳食宝塔")
    private Integer calcType;

    @Schema(title = "食材、菜肴列表json")
    private List<FoodCookbookDetailVO> details;
}
