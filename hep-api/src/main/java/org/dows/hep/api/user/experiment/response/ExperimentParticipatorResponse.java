package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.framework.api.uim.AccountInfo;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "ExperimentGroup 对象", title = "实验小组信息")
public class ExperimentParticipatorResponse {
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

    @Schema(title = "实验状态[默认未开始状态0~6步]")
    private Integer state;

    @Schema(title = "实验成员")
    private List<AccountInfo> participators;

}
