package org.dows.hep.api.base.intervene.request;

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
@Schema(name = "SetSurveyState 对象", title = "启用、禁用问卷")
public class SetSurveyStateRequest{
    @Schema(title = "问卷id")
    private String surveyId;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;


}
