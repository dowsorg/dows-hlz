package org.dows.hep.api.base.question.request;

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
@Schema(name = "QuestionnaireMergeElements 对象", title = "试卷合并因素")
public class QuestionnaireMergeElementsRequest{
    @Schema(title = "问题集[试卷]ids")
    private String questionSectionIds;

    @Schema(title = "appId")
    private String appId;


}
