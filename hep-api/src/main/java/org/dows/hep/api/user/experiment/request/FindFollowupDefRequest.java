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
@Schema(name = "FindFollowupDef 对象", title = "查询条件")
public class FindFollowupDefRequest{
    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "机构功能ID")
    private String caseOrgFunctionId;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "指标功能ID")
    private String indicatorFuncId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "页码")
    private Integer pageNo;

    @Schema(title = "页数")
    private Integer pageSize;
}
