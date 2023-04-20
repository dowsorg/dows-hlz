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
@Schema(name = "AllocationSchemeSearch 对象", title = "方案分配搜索")
public class AllocationSchemeSearchRequest{
    @Schema(title = "答题者账号ID")
    private String accountId;

    @Schema(title = "案例ID")
    private String caseInstanceId;


}
