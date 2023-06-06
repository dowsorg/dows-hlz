package org.dows.hep.api.base.intervene.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SetEventState 对象", title = "启用、禁用菜谱")
public class SetEventStateRequest{
    @Schema(title = "应用ID")
    private String appId;
    @Schema(title = "事件id")
    private String eventId;

    @Schema(title = "状态 1-启用 0-停用")
    private Integer state;


}
