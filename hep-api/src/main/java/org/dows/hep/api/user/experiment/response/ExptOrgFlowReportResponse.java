package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeVO;

import java.util.Date;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/17 19:34
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "ExptOrgFlowReportResponse 对象", title = "机构挂号报告")

public class ExptOrgFlowReportResponse {
    @Schema(title = "挂号流水号")
    private String operateFlowId;

    @Schema(title = "操作时间")
    private Date operateTime;

    @Schema(title = "操作所在游戏内天数")
    private Integer operateGameDay;

    @Schema(title = "报告名称(抬头)")
    public String reportName;

    @Schema(title = "节点列表")
    public List<ExptOrgReportNodeVO> nodes;
}
