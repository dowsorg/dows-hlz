package org.dows.hep.api.event.source;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author fhb
 * @version 1.0
 * @description 实验方案设计提交事件-事件源
 * @date 2023/6/20 0:00
 **/
@Data
@Builder
@AllArgsConstructor
@Schema(name = "ExptSchemeSubmittedEventSource 对象", title = "实验方案设计提交事件-事件源")
public class ExptSchemeSubmittedEventSource {
    @Schema(title = "需要同步信息的账号")
    private List<String> accountIds;
    private String experimentId;
}
