package org.dows.hep.api.base.question.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(name = "QuestionDelItemRequest 对象", title = "删除问卷ItemRequest")
public class QuestionSectionDelItemRequest {
    @Schema(title = "问卷ID")
    private String questionSectionId;

    @Schema(title = "问卷ItemId")
    private List<String> questionSectionItemIds;

}
