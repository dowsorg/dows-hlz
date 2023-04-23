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
@Schema(name = "SaveOrgJudge 对象", title = "检查报告")
public class SaveOrgJudgeResponse{
    @Schema(title = "机构操作id")
    private String operateOrgFuncId;

    @Schema(title = "报告详情,12-健康指导时返回体检报告")
    private String resultJson;


}
