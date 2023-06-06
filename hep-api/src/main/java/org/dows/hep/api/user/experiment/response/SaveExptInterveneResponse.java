package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "SaveExptIntervene 对象", title = "干预操作结果")
public class SaveExptInterveneResponse {

    @Schema(title = "是否操作成功")
    private Boolean success;
    @Schema(title = "机构操作记录id")
    private String operateOrgFuncId;




}
