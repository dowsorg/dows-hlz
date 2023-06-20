package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@Schema(name = "ExperimentQuestionnaireRequest 对象", title = "实验知识答题")
public class ExperimentQuestionnaireRequest {
    @Schema(title = "实验知识答题ID")
    private String experimentQuestionnaireId;

    @Schema(title = "答题卡")
    private List<ExperimentQuestionnaireItemRequest> itemList;
}
