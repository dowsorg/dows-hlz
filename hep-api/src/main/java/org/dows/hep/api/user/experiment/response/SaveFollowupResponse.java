package org.dows.hep.api.user.experiment.response;

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
@Schema(name = "SaveFollowup 对象", title = "随访记录id")
public class SaveFollowupResponse{
    @Schema(title = "机构操作id")
    private String operateOrgFuncId;


}
