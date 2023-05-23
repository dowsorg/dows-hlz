package org.dows.hep.api.base.indicator.request;

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
    private String indicatorViewBaseInfoId;
    @Schema(title = "应用ID")
    private String appId;
    @Schema(title = "指标功能ID")
    private String indicatorFuncId;
    @Schema(title = "指标基本信息类名称")
    private String name;
    private List<CreateOrUpdateIndicatorViewBaseInfoDescrRs> createOrUpdateIndicatorViewBaseInfoDescrRsList;
    private List<CreateOrUpdateIndicatorViewBaseInfoMonitorRs> createOrUpdateIndicatorViewBaseInfoMonitorRsList;
    private List<CreateOrUpdateIndicatorViewBaseInfoSingleRs> createOrUpdateIndicatorViewBaseInfoSingleRsList;
}