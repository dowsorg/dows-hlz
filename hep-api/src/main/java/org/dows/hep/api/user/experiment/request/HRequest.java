package org.dows.hep.api.user.experiment.request;

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
@Schema(name = "H 对象", title = "")
public class HRequest{
    @Schema(title = "案例事件id")
    private String caseEventId;

    @Schema(title = "事件选项id")
    private String caseEventActionId;


}
