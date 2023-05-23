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
@Schema(name = "DelSurvey 对象", title = "删除问卷")
public class DelSurveyRequest{

    @Schema(title = "应用ID")
    private String appId;
    @Schema(title = "分布式id列表")
    private String ids;


}
