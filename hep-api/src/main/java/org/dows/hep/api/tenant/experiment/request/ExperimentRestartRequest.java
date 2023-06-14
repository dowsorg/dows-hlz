package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Data
@Schema(name = "ExperimentRestartRequest 对象", title = "/暂停实验")
public class ExperimentRestartRequest {
    @NotBlank(message = "实验实列ID不允许为空")
    @Schema(title = "实验实列ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private String experimentInstanceId;

    @NotBlank(message = "状态不可为空")
    @Schema(title = "是否暂停状态(true,false)",requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean paused;
}
