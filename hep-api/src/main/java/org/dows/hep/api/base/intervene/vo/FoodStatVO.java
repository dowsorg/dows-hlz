package org.dows.hep.api.base.intervene.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/5/6 9:45
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FoodStatVO 对象", title = "饮食统计")

public class FoodStatVO {
    @Schema(title = "主体(营养指标，食材分类)id")
    private String instanceId;

    @Schema(title = "主体名称")
    private String instanceName ;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "重量")
    private String weight;

    @Schema(title = "实际能量")
    private String energy;

    @Schema(title = "推荐量下限")
    private String min;

    @Schema(title = "推荐量上限")
    private String max;


}
