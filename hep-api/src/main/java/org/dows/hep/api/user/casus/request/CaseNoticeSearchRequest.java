package org.dows.hep.api.user.casus.request;

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
@Schema(name = "CaseNoticeSearch 对象", title = "案例公告搜索")
public class CaseNoticeSearchRequest{
    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "期数")
    private String periods;


}
