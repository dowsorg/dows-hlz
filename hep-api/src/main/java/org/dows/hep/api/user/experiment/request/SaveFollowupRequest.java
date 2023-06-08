package org.dows.hep.api.user.experiment.request;

import cn.hutool.core.date.DateTime;
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
@Schema(name = "SaveFollowup 对象", title = "监测随访")
public class SaveFollowupRequest{
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "指标功能点ID")
    private String indicatorFuncId;

    @Schema(title = "实验查看指标监测随访分布式ID")
    private String experimentViewMonitorFollowupId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "实验机构ID")
    private String experimentOrgId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "机构功能ID")
    private String caseOrgFunctionId;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "操作人id")
    private String operateAccountId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "操作人名")
    private String operateAccountName;

    @Schema(title = "实验截止时间")
    private DateTime experimentDeadline;

    @Schema(title = "随访间隔天数")
    private Integer dueDays;
}
