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
@Schema(name = "SaveOrgJudgeGoals 对象", title = "保存健管目标")
public class SaveOrgJudgeGoalsRequest{
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

    @Schema(title = "操作人id")
    private String operateAccountId;

    @Schema(title = "操作人名")
    private String operateAccountName;

    @Schema(title = "输入json")
    private String inputJson;


}
