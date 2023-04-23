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
@Schema(name = "GetOrgViewReport 对象", title = "检查报告信息")
public class GetOrgViewReportResponse{
    @Schema(title = "操作时间")
    private java.time.LocalDateTime operateTime;

    @Schema(title = "输入json")
    private String inputJson;

    @Schema(title = "报告详情")
    private String resultJson;


}
