package org.dows.hep.api.tenant.experiment.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.util.Date;

@Data
@Schema(name = "ExperimentRestartRequest 对象", title = "/暂停实验")
public class ExperimentRestartRequest {
    @NotBlank(message = "应用ID不允许为空")
    @Schema(title = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appId;

    @NotBlank(message = "实验实列ID不允许为空")
    @Schema(title = "实验实列ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String experimentInstanceId;

    @NotBlank(message = "实验期数不允许为空")
    @Schema(title = "实验期数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer periods;

    @NotBlank(message = "状态不可为空")
    @Schema(title = "是否暂停状态(true,false)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean paused;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @NotBlank(message = "暂停或开始的当前时间不可为空")
    @Schema(title = "暂停或开始的当前时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date currentTime;
}
