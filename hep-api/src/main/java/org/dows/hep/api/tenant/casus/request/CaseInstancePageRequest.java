package org.dows.hep.api.tenant.casus.request;

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
@Schema(name = "CaseInstancePage 对象", title = "分页请求Request")
public class CaseInstancePageRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例名称")
    private String caseName;


}
