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
@Schema(name = "FindOrgJudgeItems 对象", title = "查询条件")
public class FindOrgJudgeItemsRequest{
    @Schema(title = "机构功能类型 11-健康问题 12-健康指导")
    private Integer funcType;

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

    @Schema(title = "是否获取最近保存状态 0-获取 1-不获取")
    private Integer actFlag;


}
