package org.dows.hep.api.user.experiment.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/7/13 9:23
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentIndicatorInstanceRequest 对象", title = "实验指标实例")
public class ExperimentIndicatorInstanceRequest {
    @Schema(title = "实验实例ID")
    @ApiModelProperty(required = true)
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;

    @Schema(title = "实验机构ID")
    @ApiModelProperty(required = true)
    private String experimentOrgId;
}
