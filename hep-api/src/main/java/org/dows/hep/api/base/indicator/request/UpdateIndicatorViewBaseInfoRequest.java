package org.dows.hep.api.base.indicator.request;

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
@Schema(name = "UpdateIndicatorViewBaseInfo 对象", title = "更改指标基本信息类")
public class UpdateIndicatorViewBaseInfoRequest{
    @Schema(title = "指标基本信息类名称")
    private String name;

    @Schema(title = "指标描述表列表")
    private String indicatorViewBaseInfoDesc;

    @Schema(title = "指标监测表列表")
    private String indicatorViewBaseInfoMonitor;

    @Schema(title = "单一指标列表")
    private String indicatorViewBaseInfoSingle;


}
