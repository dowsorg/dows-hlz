package org.dows.hep.api.tenant.casus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "CaseInstanceCopyRequest 对象", title = "案例复制Request")
public class CaseInstanceCopyRequest {
    @Schema(title = "原案例ID")
    private String oriCaseInstanceId;

    @Schema(title = "目标案例名称")
    private String targetCaseInstanceName;
}
