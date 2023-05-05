package org.dows.hep.api.base.question.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.question.request.QuestionRequest;

@Data
@NoArgsConstructor
@Schema(name = "QuestionSectionItemResponse 对象", title = "问题集Response")
public class QuestionSectionItemResponse {
    @Schema(title = "应用ID")
    private String appId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "itemID")
    private String questionSectionItemId;

    @Schema(title = "状态")
    private Integer enabled;

    @Schema(title = "是否必填")
    private Integer required;

    @Schema(title = "排序")
    private Integer sequence;

    @Schema(title = "问题")
    private QuestionRequest questionRequest;
}
