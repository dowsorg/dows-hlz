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
@Schema(name = "SaveSurveyReport 对象", title = "保存问卷报告设置")
public class SaveSurveyReportRequest{
    @Schema(title = "应用ID")
    private String appId;
    @Schema(title = "分布式id")
    private String surveyId;

    @Schema(title = "报告列表json")
    private String reports;


}
