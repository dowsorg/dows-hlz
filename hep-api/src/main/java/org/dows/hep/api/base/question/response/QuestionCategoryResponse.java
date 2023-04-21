package org.dows.hep.api.base.question.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/4/20 9:59
 */
@Data
@NoArgsConstructor
@Schema(name = "QuestionCategoryResponse 对象", title = "问题类目 Response")
public class QuestionCategoryResponse {
    @Schema(title = "类别ID")
    private String questionCategId;

    @Schema(title = "类别父id")
    private String questionCategPid;

    @Schema(title = "类别组")
    private String questionCategGroup;

    @Schema(title = "类别名")
    private String questionCategName;
}
