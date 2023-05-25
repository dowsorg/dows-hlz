package org.dows.hep.api.base.question.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "QuestionSectionItemResponse 对象", title = "问题集Response")
public class QuestionSectionItemResponse {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "itemID")
    private String questionSectionItemId;

    @Schema(title = "状态")
    private Integer enabled;

    @Schema(title = "是否必填")
    private Integer required;

    @Schema(title = "排序")
    private Integer sequence;

    @Schema(title = "问题")
    private QuestionResponse question;
}
