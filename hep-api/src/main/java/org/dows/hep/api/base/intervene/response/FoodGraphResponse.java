package org.dows.hep.api.base.intervene.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.FoodStatVO;

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
@Schema(name = "FoodGraph 对象", title = "能量占比、膳食宝塔")
public class FoodGraphResponse{

    @Schema(title = "计算类型 0-默认 1-只计算能量占比 2-只计算膳食宝塔")
    private Integer calcType;
    @Schema(title = "能量占比json")
    private List<? extends FoodStatVO> statEnergy;

    @Schema(title = "膳食结构json")
    private List<? extends FoodStatVO> statCateg;


}
