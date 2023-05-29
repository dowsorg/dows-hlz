package org.dows.hep.api.base.question.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author fhb
 * @description
 * @date 2023/5/21 17:41
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "QuestionClonedRequest 对象", title = "问题Request")
public class QuestionClonedRequest {
    @Schema(title = "被克隆的问题ID")
    private String oriQuestionInstanceId;

    @Schema(title = "目标问题标题")
    private String targetQuestionTitle;
}
