package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jx
 * @date 2023/6/6 14:27
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentGroup 对象", title = "实验小组信息")
public class ExperimentIndicatorResponse {
    @Schema(title = "指标ID")
    private String experimentIndicatorInstanceId;

    @Schema(title = "指标名称")
    private String experimentIndicatorInstanceName;

    @Schema(title = "指标名称")
    private String experimentIndicatorCurrentVal;

    @Schema(title = "指标单位")
    private String unit;

    @Schema(title = "指标类别")
    private String type;

    @Schema(title = "内容")
    private String content;

    @Schema(title = "指标类别")
    private List<ExperimentIndicatorResponse> indicatorList;
}
