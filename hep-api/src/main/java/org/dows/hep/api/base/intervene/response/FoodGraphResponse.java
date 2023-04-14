package org.dows.hep.api.base.intervene.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "FoodGraph 对象", title = "能量占比、膳食宝塔")
public class FoodGraphResponse {
    @Schema(title = "能量占比json")
    private String statEnergy;

    @Schema(title = "膳食结构json")
    private String statCateg;


}
