package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(name = "GetExperimentGroupCaptainRequest 对象", title = "获取实验小组组长")
public class GetExperimentGroupCaptainRequest {

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;
    @Schema(title = "实验小组ID")
    private String experimentGroupId;
    @Schema(title = "主体ID")
    private String accountId;

}
