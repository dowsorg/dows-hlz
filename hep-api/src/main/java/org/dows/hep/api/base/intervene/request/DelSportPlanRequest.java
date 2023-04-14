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
@Schema(name = "DelSportPlan 对象", title = "删除运动方案")
public class DelSportPlanRequest {
    @Schema(title = "分布式id列表")
    private String ids;


}
