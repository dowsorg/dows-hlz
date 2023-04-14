package org.dows.hep.api.base.indicator.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "IndicatorViewMonitorFollowup 对象", title = "查看指标监测随访类")
public class IndicatorViewMonitorFollowupResponse {
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "查看指标监测随访类分布式ID")
    private String IndicatorViewMonitorFollowupId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;

    @Schema(title = "指标监测随访类表名称")
    private String name;

    @Schema(title = "监测随访表类别")
    private String type;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
