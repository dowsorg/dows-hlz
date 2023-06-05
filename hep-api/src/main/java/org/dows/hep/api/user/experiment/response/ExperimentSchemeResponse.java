package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;

/**
 * @author fhb
 * @description
 * @date 2023/5/30 15:53
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentSchemeResponse 对象", title = "实验方案设计")
public class ExperimentSchemeResponse {
    @Schema(title = "实验方案设计ID")
    private String experimentSchemeId;

    @Schema(title = "方案设计试卷")
    private CaseSchemeResponse caseSchemeResponse;
}
