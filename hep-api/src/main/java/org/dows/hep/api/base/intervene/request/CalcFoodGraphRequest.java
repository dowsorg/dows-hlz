package org.dows.hep.api.base.intervene.request;

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
@Schema(name = "CalcFoodGraph 对象", title = "计算能量占比、膳食宝塔")
public class CalcFoodGraphRequest {
    @Schema(title = "计算类型 0-默认 1-只计算能量占比 2-只计算膳食宝塔")
    private Integer calcType;

    @Schema(title = "食材,菜肴重量列表json")
    private String details;


}
