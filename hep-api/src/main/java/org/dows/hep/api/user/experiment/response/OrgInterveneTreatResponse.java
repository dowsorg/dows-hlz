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
@Schema(name = "OrgInterveneTreat 对象", title = "方案详情")
public class OrgInterveneTreatResponse{
    @Schema(title = "机构操作id")
    private String operateOrgFuncId;

    @Schema(title = "方案详情")
    private String resultJson;


}
