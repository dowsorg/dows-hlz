package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "OrgReport 对象", title = "机构报告")
public class OrgFlowReportResponse {
    @Schema(title = "实验操作流程id")
    private String operateFlowId;

    @Schema(title = "操作时间")
    private Date operateTime;

    @Schema(title = "操作所在游戏内天数")
    private Integer operateGameDay;
    @Schema(title = "展示标签")
    private String reportLabel;

    @Schema(title = "操作描述")
    private String reportDescr;


}
