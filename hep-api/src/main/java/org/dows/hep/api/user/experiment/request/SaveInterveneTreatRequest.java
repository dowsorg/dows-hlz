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
@Schema(name = "SaveInterveneTreat 对象", title = "保存治疗干预记录")
public class SaveInterveneTreatRequest{
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "操作人id")
    private String operateAccountId;

    @Schema(title = "操作人名")
    private String operateAccountName;

    @Schema(title = "实验人物ID")
    private String experimentPsersonId;

    @Schema(title = "案例人名")
    private String caseAccountName;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "操作[干预]类型 3-心理 4-治疗 ")
    private String operateType;

    @Schema(title = "状态完整快照json")
    private String operateContextJson;


}
