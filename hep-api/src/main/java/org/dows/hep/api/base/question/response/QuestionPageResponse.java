package org.dows.hep.api.base.question.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/4/23 11:23
 */
@Data
@NoArgsConstructor
@Schema(name = "QuestionPageResponse 对象", title = "问题分页Response")
public class QuestionPageResponse {
    @Schema(title = "问题ID")
    private String questionInstanceId;

    @Schema(title = "题目内容")
    private String questionTitle;

    @Schema(title = "一级类别ID")
    private String questionCategId;

    @Schema(title = "一级类别名称")
    private String questionCategName;

    @Schema(title = "题型")
    private String questionType;

    @Schema(title = "当前状态")
    private Integer enabled;
}
