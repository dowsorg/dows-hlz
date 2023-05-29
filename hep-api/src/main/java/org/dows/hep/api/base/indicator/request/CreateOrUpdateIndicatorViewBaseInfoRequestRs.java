package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrUpdateIndicatorViewBaseInfoRequestRs implements Serializable {
    @Schema(title = "分布式ID")
    @ApiModelProperty(required = false, value = "查看指标-基本信息分布式id")
    private String indicatorViewBaseInfoId;

    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;

    @Schema(title = "指标功能ID")
    @ApiModelProperty(required = true)
    private String indicatorFuncId;

    @Schema(title = "指标基本信息类名称")
    @ApiModelProperty(required = false, value = "防止产品以后需求的冗余字段，实际不起作用")
    private String name;

    @ApiModelProperty(required = true, value = "基本信息-指标描述表列表")
    private List<CreateOrUpdateIndicatorViewBaseInfoDescrRs> createOrUpdateIndicatorViewBaseInfoDescrRsList;

    @ApiModelProperty(required = true, value = "基本信息-指标监测表列表")
    private List<CreateOrUpdateIndicatorViewBaseInfoMonitorRs> createOrUpdateIndicatorViewBaseInfoMonitorRsList;

    @ApiModelProperty(required = true, value = "基本信息-单个指标列表")
    private List<CreateOrUpdateIndicatorViewBaseInfoSingleRs> createOrUpdateIndicatorViewBaseInfoSingleRsList;
}