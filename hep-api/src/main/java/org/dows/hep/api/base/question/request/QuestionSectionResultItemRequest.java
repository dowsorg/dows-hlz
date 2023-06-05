package org.dows.hep.api.base.question.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "QuestionSectionResultItemRequest 对象", title = "答题记录 ItemRequest")
public class QuestionSectionResultItemRequest {
    @Schema(title = "记录项ID")
    private String questionSectionResultItemId;

    @Schema(title = "问题ID")
    private String questionInstanceId;

    @Schema(title = "答案值ID[JSON]")
    private String answerId;

    @Schema(title = "答题值[JSON]")
    private String answerValue;
}
