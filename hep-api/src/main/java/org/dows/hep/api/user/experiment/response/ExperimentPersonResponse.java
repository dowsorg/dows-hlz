package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/5/10 10:28
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentPerson 对象", title = "实验人物")
public class ExperimentPersonResponse {
    @Schema(title = "数据库ID")
    private String id;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "实验实例ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验机构ID")
    private String experimentOrgId;

    @Schema(title = "实验机构名称")
    private String experimentOrgName;

    @Schema(title = "实验人物ID")
    private String experimentAccountId;

    @Schema(title = "实验人物名称")
    private String experimentAccountName;

    @Schema(title = "期数")
    private Integer periods;
}
