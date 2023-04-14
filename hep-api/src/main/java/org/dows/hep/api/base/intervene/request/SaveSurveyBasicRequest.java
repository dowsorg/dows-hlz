package org.dows.hep.api.base.intervene.request;

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
@Schema(name = "SaveSurveyBasic 对象", title = "保存问卷基本信息")
public class SaveSurveyBasicRequest {
    @Schema(title = "分布式id")
    private String surveyId;

    @Schema(title = "问卷名称")
    private String surveyName;

    @Schema(title = "分类id")
    private String surveyCategId;

    @Schema(title = "分类名称")
    private String categName;

    @Schema(title = "分布id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "问卷说明")
    private String descr;


}
