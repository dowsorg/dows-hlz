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
@Schema(name = "OrgPersonBasic 对象", title = "基本信息")
public class OrgPersonBasicResponse{
    @Schema(title = "基本信息")
    private String resultJson;


}
