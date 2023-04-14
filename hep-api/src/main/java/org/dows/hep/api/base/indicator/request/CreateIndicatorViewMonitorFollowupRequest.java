package org.dows.hep.api.base.indicator.request;

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
@Schema(name = "CreateIndicatorViewMonitorFollowup 对象", title = "创建查看指标监测随访类")
public class CreateIndicatorViewMonitorFollowupRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标监测随访类表名称")
    private String name;

    @Schema(title = "监测随访表类别")
    private String type;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;

    @Schema(title = "查看指标监测随访内容列表")
    private String createIndicatorViewMonitorFollowupContentList;


}
