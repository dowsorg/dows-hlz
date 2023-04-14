package org.dows.hep.api.base.intervene.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "Survey 对象", title = "问卷列表")
public class SurveyResponse {
    @Schema(title = "分布式id")
    private String surveyId;

    @Schema(title = "问卷名称")
    private String surveyName;

    @Schema(title = "一级分类id")
    private String categIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;


}
