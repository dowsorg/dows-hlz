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
@Schema(name = "CaseSchemePage 对象", title = "关键字聚合")
public class CaseSchemePageRequest {
    @Schema(title = "页数")
    private Integer pageNo;

    @Schema(title = "页大小")
    private Integer pageSize;

    @Schema(title = "方案名称")
    private String schemeName;

    @Schema(title = "类别ID")
    private String categId;


}
