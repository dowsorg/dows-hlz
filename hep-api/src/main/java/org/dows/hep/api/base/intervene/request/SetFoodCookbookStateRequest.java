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
@Schema(name = "SetFoodCookbookState 对象", title = "启用、禁用菜谱")
public class SetFoodCookbookStateRequest {
    @Schema(title = "菜肴id")
    private String foodCookbookId;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;


}
