package org.dows.hep.api.base.question.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/4/21 17:04
 */
@Data
@NoArgsConstructor
@Schema(name = "QuestionOptionRequest 对象", title = "问题选项Request")
public class QuestionOptionWithAnswerResponse implements Cloneable{
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "问题选项ID")
    private String questionOptionsId;

    @Schema(title = "答案的ID")
    private String questionAnswerId;

    @Schema(title = "选项标题")
    private String optionTitle;

    @Schema(title = "选项值")
    private String optionValue;

    @Schema(title = "是否是正确答案[0:错误，1:正确]")
    private Integer rightAnswer;

    @Override
    public QuestionOptionWithAnswerResponse clone() {
        try {
            return  (QuestionOptionWithAnswerResponse) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
