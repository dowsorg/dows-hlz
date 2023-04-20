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
@Schema(name = "AddActor 对象", title = "角色扮演")
public class AddActorRequest{
    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "扮演关联ID[分析角色|机构角色]")
    private String actorId;

    @Schema(title = "分配角色[分析角色名|机构角色名]")
    private String actorName;

    @Schema(title = "扮演类型[0:问题，1:机构]")
    private String actorType;

    @Schema(title = "小组别名")
    private String groupAlias;

    @Schema(title = "组员账号ID")
    private String accountId;

    @Schema(title = "组员账号名")
    private String accountName;

    @Schema(title = "参与者类型[0:教师，1:学生，2:组长]")
    private Integer participatorType;


}
