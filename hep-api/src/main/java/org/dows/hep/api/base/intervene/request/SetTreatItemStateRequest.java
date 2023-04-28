package org.dows.hep.api.base.intervene.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : wuzl
 * @date : 2023/4/24 17:55
 */
@Data
@NoArgsConstructor
@Schema(name = "SetTreatItemState 对象", title = "启用、禁用 干预项目")
public class SetTreatItemStateRequest {
    @Schema(title = "干预项目id")
    private String treatItemId;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;
}
