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
@Schema(name = "QuestionSection 对象", title = "问题集Request")
public class QuestionSectionRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "类别Id")
    private String questionSectionCategId;

    @Schema(title = "问题集名称")
    private String name;

    @Schema(title = "问题集提示")
    private String tips;

    @Schema(title = "问题集说明")
    private String descr;

    @Schema(title = "是否必填")
    private Integer required;

    @Schema(title = "状态")
    private Integer enabled;

    @Schema(title = "排序")
    private Integer sequence;

    @Schema(title = "问题集合")
    private String QuestionRequest;

    @Schema(title = "维度集合")
    private String QuestionSectionDimensionRequest;


}
