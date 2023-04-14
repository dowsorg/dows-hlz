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
@Schema(name = "UpdateIndicatorViewBaseInfo 对象", title = "更改指标基本信息类")
public class UpdateIndicatorViewBaseInfoRequest {
    @Schema(title = "指标基本信息类名称")
    private String name;

    @Schema(title = "指标描述表列表")
    private String IndicatorViewBaseInfoDesc;

    @Schema(title = "指标监测表列表")
    private String IndicatorViewBaseInfoMonitor;

    @Schema(title = "单一指标列表")
    private String IndicatorViewBaseInfoSingle;


}
