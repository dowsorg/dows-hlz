package org.dows.hep.api.tenant.casus.request;

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
@Schema(name = "CaseQuestionnaireSearch 对象", title = "关键字聚合")
public class CaseQuestionnaireSearchRequest {
    @Schema(title = "页数")
    private Integer pageNo;

    @Schema(title = "页大小")
    private Integer pageSize;


}
