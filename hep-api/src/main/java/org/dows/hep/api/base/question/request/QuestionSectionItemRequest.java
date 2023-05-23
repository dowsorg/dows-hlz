package org.dows.hep.api.base.question.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author fhb
 * @description
 * @date 2023/4/24 19:37
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "QuestionSectionItemRequest 对象", title = "问题集 item Request")
public class QuestionSectionItemRequest {
    @Schema(title = "itemID")
    private String questionSectionItemId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "状态")
    private Integer enabled;

    @Schema(title = "是否必填")
    private Integer required;

    @Schema(title = "排序")
    private Integer sequence;

    @Schema(title = "问题")
    private QuestionRequest questionRequest;

}
