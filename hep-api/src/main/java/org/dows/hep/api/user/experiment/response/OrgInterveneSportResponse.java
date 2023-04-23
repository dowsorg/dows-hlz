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
@Schema(name = "OrgInterveneSport 对象", title = "运动方案")
public class OrgInterveneSportResponse{
    @Schema(title = "机构操作id")
    private String operateOrgFuncId;

    @Schema(title = "运动方案详情")
    private String resultJson;


}
