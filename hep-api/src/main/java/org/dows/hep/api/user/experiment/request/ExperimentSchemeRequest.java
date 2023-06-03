package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.request.QuestionSectionResultRequest;

/**
 * @author fhb
 * @description
 * @date 2023/6/3 18:54
 */
@Data
@RequiredArgsConstructor
@Schema(name = "ExperimentSchemeRequest 对象", title = "实验方案设计")
public class ExperimentSchemeRequest {
    @Schema(title = "实验方案设计ID")
    private String experimentSchemeId;

    @Schema(title = "答题卡")
    private QuestionSectionResultRequest questionSectionResultRequest;
}
