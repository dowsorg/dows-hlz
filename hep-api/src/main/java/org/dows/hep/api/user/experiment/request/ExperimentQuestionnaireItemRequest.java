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
    @Schema(title = "item id")
    private String experimentQuestionnaireItemId;

    @Schema(title = "作答答案")
    private List<String> questionResult;

    @Schema(title = "子")
    private List<ExperimentQuestionnaireItemRequest> children;
}
