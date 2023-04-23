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
@Schema(name = "OrgReport 对象", title = "机构报告")
public class OrgReportResponse{
    @Schema(title = "实验操作流程id")
    private String operateFlowId;

    @Schema(title = "操作时间")
    private java.time.LocalDateTime operateTime;

    @Schema(title = "操作描述")
    private String operateDescr;

    @Schema(title = "标签")
    private String label;


}
