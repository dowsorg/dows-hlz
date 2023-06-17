package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(name = "ExperimentInitializeRequest 对象", title = "初始化实验")
public class ExperimentInitializeRequest {
    @Schema(title = "实验实例ID")
    private String experimentInstanceId;

    @Schema(title = "案例实例ID")
    private String caseInstanceId;
}
