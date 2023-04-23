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
@Schema(name = "SetFollowup 对象", title = "保存随访")
public class SetFollowupRequest{
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "机构功能ID")
    private String caseOrgFunctionId;

    @Schema(title = "操作人id")
    private String operateAccountId;

    @Schema(title = "操作人名")
    private String operateAccountName;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "随访表id")
    private String indicatorViewMonitorFollowupId;

    @Schema(title = "游戏内起始天数")
    private Integer startDay;

    @Schema(title = "随访间隔天数")
    private Integer dueDays;


}
