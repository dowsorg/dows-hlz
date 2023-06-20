package org.dows.hep.api.event.source;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author fhb
 * @version 1.0
 * @description 实验初始化事件-事件源
 * @date 2023/6/20 0:16
 **/
@Data
@Builder
@AllArgsConstructor
@Schema(name = "ExptInitEventSource 对象", title = "实验初始化事件-事件源")
public class ExptInitEventSource {
    @Schema(title = "实验实例ID")
    private String experimentInstanceId;

    @Schema(title = "案例实例ID")
    private String caseInstanceId;
}
