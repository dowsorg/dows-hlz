package org.dows.hep.api.base.question.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/4/19 15:43
 */
@Data
@NoArgsConstructor
@Schema(name = "QuestionOptionRequest 对象", title = "问题选项Request")
public class QuestionOptionWithAnswerRequest {
    @Schema(title = "问题选项ID")
    private String questionOptionsId;

    @Schema(title = "选项标题")
    private String optionTitle;

    @Schema(title = "选项值")
    private String optionValue;

    @Schema(title = "是否是正确答案[0:错误，1:正确]")
    private Boolean right;
}
