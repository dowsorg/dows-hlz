package org.dows.hep.api.tenant.casus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/5/18 14:10
 */
@Data
@NoArgsConstructor
public class CaseQuestionnaireDelItemRequest {

    @Schema(title = "试卷ID")
    private String questionSectionId;

    @Schema(title = "itemId")
    private String questionSectionItemId;
}
