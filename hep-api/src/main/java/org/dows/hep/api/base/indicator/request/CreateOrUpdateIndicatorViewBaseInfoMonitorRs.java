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
public class CreateOrUpdateIndicatorViewBaseInfoMonitorRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorViewBaseInfoMonitorId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  @ApiModelProperty(required = true)
  private String indicatorViewBaseInfoId;

  @Schema(title = "指标基本信息监测表名称")
  @ApiModelProperty(required = true)
  private String name;

  @Schema(title = "展示顺序")
  @ApiModelProperty(required = true)
  private Integer seq;

  @ApiModelProperty(required = true, value = "查看指标-监测随访的监测随访内容列表")
  private List<CreateOrUpdateIndicatorViewBaseInfoMonitorContentRequestRs> createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRsList;
}
