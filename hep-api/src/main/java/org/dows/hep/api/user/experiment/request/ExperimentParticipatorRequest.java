package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.account.response.AccountInstanceResponse;
import java.util.List;

/**
 * @author jx
 * @date 2023/5/8 9:27
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentParticipator 对象", title = "实验参与者")
public class ExperimentParticipatorRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验参与者ID")
    private String experimentParticipatorId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "小组序号")
    private String groupNo;

    @Schema(title = "组名")
    private String groupName;

    @Schema(title = "小组别名")
    private String groupAlias;

    @Schema(title = "案例机构ID")
    private String caseOrgIds;

    @Schema(title = "案例机构名称")
    private String caseOrgNames;

    @Schema(title = "实验状态[默认未开始状态0~6步]")
    private Integer state;

    @Schema(title = "实验成员")
    private List<AccountInstanceResponse> participators;
}
