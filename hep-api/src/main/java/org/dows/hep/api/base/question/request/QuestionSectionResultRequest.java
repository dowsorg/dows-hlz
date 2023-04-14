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
@Schema(name = "QuestionSectionResult 对象", title = "答案Request")
public class QuestionSectionResultRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "答题记录ID")
    private String questionSectionResultId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "答案记录Item")
    private String QuestionSectionResultItem;


}
