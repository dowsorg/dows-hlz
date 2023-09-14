package org.dows.hep.api.user.experiment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/9/6 9:53
 */
@Data
@Accessors(chain = true)
@Schema(title = "健康档案指标变化折线")
public class ExptIndicatorValLine {
    @Schema(title = "指标id")
    private String indicatorId;

    @Schema(title = "指标名称")
    private String indicatorName;

    @Schema(title = "指标单位")
    private String unit;

    @Schema(title = "点位列表")
    private List<ExptIndicatorValPoint> points;
}
