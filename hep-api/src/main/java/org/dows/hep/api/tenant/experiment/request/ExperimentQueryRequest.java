package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "ExperimentQueryRequest 对象", title = "查询实验对象")
public class ExperimentQueryRequest {
    @Schema(title = "实验名称")
    private String experimentName;
    @Schema(title = "方案名称")
    private String caseNaem;
}
