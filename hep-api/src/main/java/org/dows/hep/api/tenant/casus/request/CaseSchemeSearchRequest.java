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
@Schema(name = "CaseSchemeSearch 对象", title = "案例方案搜索")
public class CaseSchemeSearchRequest {
    @Schema(title = "类别ID")
    private String categId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "方案名称")
    private String schemeName;


}
