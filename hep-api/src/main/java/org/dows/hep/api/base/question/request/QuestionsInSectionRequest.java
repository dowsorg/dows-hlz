package org.dows.hep.api.base.question.request;

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
@Schema(name = "QuestionsInSection 对象", title = "问卷中条件查询")
public class QuestionsInSectionRequest {
    @Schema(title = "appId")
    private String appId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "类别Id")
    private String questionInstanceCategId;

    @Schema(title = "题目答题类型[单选|多选|判断|主观|材料]")
    private String questionType;

    @Schema(title = "题目")
    private String questionName;


}
