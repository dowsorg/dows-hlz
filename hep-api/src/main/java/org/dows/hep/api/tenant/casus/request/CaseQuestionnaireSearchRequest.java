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
@Schema(name = "CaseQuestionnaireSearch 对象", title = "关键字聚合")
public class CaseQuestionnaireSearchRequest{
    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "知识体系")
    private String l1CategId;

    @Schema(title = "知识类别")
    private String l2CategId;


}
