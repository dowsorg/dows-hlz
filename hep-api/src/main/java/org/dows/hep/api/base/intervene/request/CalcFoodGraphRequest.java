package org.dows.hep.api.base.intervene.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.FoodDetailVO;

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
@Schema(name = "CalcFoodGraph 对象", title = "计算能量占比、膳食宝塔")
public class CalcFoodGraphRequest{
    @Schema(title = "应用ID")
    private String appId;
    @Schema(title = "计算类型 0-默认 1-只计算能量占比 2-只计算膳食宝塔")
    private Integer calcType;

    @Schema(title = "食材,菜肴重量列表json")
    private List<FoodDetailVO> details;




}
