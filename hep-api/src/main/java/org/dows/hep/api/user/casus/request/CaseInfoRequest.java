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
@Schema(name = "CaseInfo 对象", title = "提示信息")
public class CaseInfoRequest{
    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "[descr:背景|guide:帮助中心|scoresPrompt:评分提示]")
    private String type;


}
