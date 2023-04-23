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
@Schema(name = "OrgInterveneFood 对象", title = "操作记录")
public class OrgInterveneFoodResponse{
    @Schema(title = "机构操作id")
    private String operateOrgFuncId;

    @Schema(title = "食谱详情,包含能量占比，膳食结构")
    private String resultJson;


}
