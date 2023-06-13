package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExperimentGroup 对象", title = "实验小组信息")
public class ExperimentGroupResponse{
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

    @Schema(title = "成员数量")
    private Integer memberCount;

    @Schema(title = "最小成员数量")
    private Integer minMemberCount;

    @Schema(title = "最大成员数量")
    private Integer maxMemberCount;

    @Schema(title = "小组状态 [0-新建（待重新命名） 1-编队中 （分配成员角色） 2-编队完成 3-已锁定 4-已解散]")
    private Boolean groupState;

}
