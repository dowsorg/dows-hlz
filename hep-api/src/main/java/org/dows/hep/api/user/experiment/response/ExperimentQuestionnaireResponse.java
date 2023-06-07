package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/6/3 20:45
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentQuestionnaireResponse 对象", title = "实验知识答题")
public class ExperimentQuestionnaireResponse {
    @Schema(title = "实验知识答题ID")
    private String experimentQuestionnaireId;

    @Schema(title = "案例问卷名")
    private String questionnaireName;

    @Schema(title = "知识答题试卷")
    private List<ExperimentQuestionnaireItemResponse> itemList;
}
