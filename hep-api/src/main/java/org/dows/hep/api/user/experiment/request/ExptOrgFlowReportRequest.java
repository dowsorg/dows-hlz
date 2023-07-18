package org.dows.hep.api.user.experiment.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.core.ExptOrgFuncRequest;

/**
 * @author : wuzl
 * @date : 2023/7/18 11:12
 */

@Data
@NoArgsConstructor
@Schema(name = "ExptOrgFlowReportRequest 对象", title = "机构挂号结束报告")
public class ExptOrgFlowReportRequest extends ExptOrgFuncRequest {

    @Schema(title = "挂号流水id")
    @ApiModelProperty(required = true)
    private String operateFlowId;

}
