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
@Schema(name = "CaseSchemeSearch 对象", title = "案例方案搜索")
public class CaseSchemeSearchRequest{

    @Schema(title = "appId")
    private String appId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "类别ID")
    private String categId;

    @Schema(title = "关键字")
    private String keyword;

    @Schema(title = "来源")
    private String source;


}
