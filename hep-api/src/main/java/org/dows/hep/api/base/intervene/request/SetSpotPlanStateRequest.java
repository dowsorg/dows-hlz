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
@Schema(name = "SetSpotPlanState 对象", title = "启用、禁用运动方案")
public class SetSpotPlanStateRequest {
    @Schema(title = "运动方案id")
    private String sportPlanId;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;


}
