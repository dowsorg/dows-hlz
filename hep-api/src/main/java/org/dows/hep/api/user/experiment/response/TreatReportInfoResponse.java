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
@Schema(name = "TreatReportInfo 对象", title = "诊疗报告")
public class TreatReportInfoResponse{
    @Schema(title = "实验操作流程id")
    private String operateFlowId;

    @Schema(title = "操作时间")
    private java.time.LocalDateTime operateTime;

    @Schema(title = "报告详情")
    private String resultJson;


}
