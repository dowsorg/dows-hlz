package org.dows.hep.api.user.experiment.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/5/10 10:19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExperimentPerson 对象", title = "实验人物")
public class ExperimentPersonRequest {
    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "实验实例ID")
    @ApiModelProperty(required = true)
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验机构ID")
    @ApiModelProperty(required = true)
    private String experimentOrgId;

    @Schema(title = "实验机构名称")
    private String experimentOrgName;

    @Schema(title = "实验人物ID")
    private String experimentAccountId;

    @Schema(title = "实验人物名称")
    private String experimentAccountName;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "页码")
    @ApiModelProperty(required = true)
    private Integer pageNo;

    @Schema(title = "页数")
    @ApiModelProperty(required = true)
    private Integer pageSize;
}
