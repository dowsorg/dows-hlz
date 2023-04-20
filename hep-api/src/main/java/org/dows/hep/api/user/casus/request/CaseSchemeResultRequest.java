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
@Schema(name = "CaseSchemeResult 对象", title = "案例方案结果Request")
public class CaseSchemeResultRequest{
    @Schema(title = "方案ID")
    private String caseSchemeId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "答题记录ID")
    private String questionSectionResultId;

    @Schema(title = "答题者账号ID")
    private String accountId;

    @Schema(title = "答题者Name")
    private String accountName;


}
