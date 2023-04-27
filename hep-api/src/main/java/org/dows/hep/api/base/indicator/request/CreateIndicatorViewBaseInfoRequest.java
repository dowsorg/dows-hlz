package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@Builder
@Schema(name = "CreateIndicatorViewBaseInfo 对象", title = "创建指标基本信息类")
public class CreateIndicatorViewBaseInfoRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标基本信息类名称")
    private String name;

    @Schema(title = "指标描述表列表")
    private String indicatorViewBaseInfoDesc;

    @Schema(title = "指标监测表列表")
    private String indicatorViewBaseInfoMonitor;

    @Schema(title = "单一指标列表")
    private String indicatorViewBaseInfoSingle;


}
