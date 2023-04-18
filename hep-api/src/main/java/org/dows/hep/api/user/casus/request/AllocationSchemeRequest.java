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
@Schema(name = "AllocationScheme 对象", title = "分配方案请求")
public class AllocationSchemeRequest{
    @Schema(title = "答题者账号ID")
    private String accountId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "方案ID")
    private String caseSchemeId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "问题ids")
    private String questionInstanceIds;


}
