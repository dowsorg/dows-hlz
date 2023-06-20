package org.dows.hep.api.event.source;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Schema(name = "ExperimentSchemeSyncResponse 对象", title = "实验方案设计同步请求")
public class ExptSchemeSyncEventSource {
    @Schema(title = "需要同步信息的账号")
    private List<String> accountIds;

    @Schema(title = "需要同步的信息")
    private ExperimentSchemeResponse experimentSchemeResponse;
}
