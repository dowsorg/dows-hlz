package org.dows.hep.api.user.experiment.response;

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
@Schema(name = "SaveOrgInterveneTreat 对象", title = "操作记录")
public class SaveOrgInterveneTreatResponse{
    @Schema(title = "机构操作id")
    private String operateOrgFuncId;

    @Schema(title = "报告详情,24-保存治疗方案时返回诊疗报告")
    private String resultJson;


}
