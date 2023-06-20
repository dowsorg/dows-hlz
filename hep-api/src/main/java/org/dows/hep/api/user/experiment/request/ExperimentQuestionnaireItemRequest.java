package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/6/7 14:30
 */
@Data
@RequiredArgsConstructor
@Schema(name = "ExperimentQuestionnaireItemRequest 对象", title = "实验方案设计Item")
public class ExperimentQuestionnaireItemRequest {
    @Schema(title = "实验方案设计ID")
    private String experimentSchemeItemId;

    @Schema(title = "作答答案-选项id逗号连接")
    private String questionResult;

    @Schema(title = "子")
    private List<ExperimentQuestionnaireItemRequest> children;
}
