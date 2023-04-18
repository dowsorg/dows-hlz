package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "CreateGroup 对象", title = "创建团队")
public class CreateGroupRequest{
    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "小组序号")
    private String groupNo;

    @Schema(title = "小组名称")
    private String groupName;

    @Schema(title = "队长ID")
    private String accountId;


}
