package org.dows.hep.api.base.question.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "QuestionPageRequest 对象", title = "问题分页Request")
public class QuestionPageRequest {
    @Schema(title = "pageNo")
    private Long pageNo;

    @Schema(title = "pageSize")
    private Long pageSize;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "关键字")
    private String keyword;

    @Schema(title = "题型")
    private String questionType;


}
