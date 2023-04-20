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
@Schema(name = "AllocationQuestionnaireSearch 对象", title = "案例答题搜索Request")
public class AllocationQuestionnaireSearchRequest{
    @Schema(title = "答题者账号ID")
    private String accountId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "期数")
    private String periods;


}
