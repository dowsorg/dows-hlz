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
@Schema(name = "SaveOrgView 对象", title = "检查报告id")
public class SaveOrgViewResponse{
    @Schema(title = "机构操作id")
    private String operateOrgFuncId;


}
