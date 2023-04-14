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
@Schema(name = "QuestionDimension 对象", title = "问题维度Request")
public class QuestionDimensionRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "问题ID")
    private String questionInstanceId;

    @Schema(title = "问题集维度ids")
    private String questionSectionDimensionIds;


}
