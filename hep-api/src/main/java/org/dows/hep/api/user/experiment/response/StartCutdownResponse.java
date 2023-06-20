package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dows.hep.api.enums.EnumWebSocketType;

/**
 * @author jx
 * @date 2023/6/20 11:39
 */
@Data
public class StartCutdownResponse {
    EnumWebSocketType type;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "每期时长")
    private Long periodInterval;

    @Schema(title = "实验模式")
    private String modelDescr;
}
