package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.user.experiment.vo.ExptOrgFlowReportVO;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SaveExptTreat 对象", title = "治疗干预操作结果")
public class SaveExptTreatResponse {

    @Schema(title = "是否操作成功")
    private Boolean success;
    @Schema(title = "机构操作id")
    private String operateOrgFuncId;


    @Schema(title = "报告信息")
    private ExptOrgFlowReportVO reportInfo;


}
