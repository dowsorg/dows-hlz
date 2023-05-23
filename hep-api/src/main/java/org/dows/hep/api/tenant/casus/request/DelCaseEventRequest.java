package org.dows.hep.api.tenant.casus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "DelCaseEvent 对象", title = "删除事件")
public class DelCaseEventRequest{

    @Schema(title = "应用ID")
    private String appId;
    @Schema(title = "分布式id列表")
    private List<String> ids;


}
