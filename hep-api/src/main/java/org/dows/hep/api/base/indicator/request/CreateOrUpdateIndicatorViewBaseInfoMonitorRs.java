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
public class CreateOrUpdateIndicatorViewBaseInfoMonitorRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorViewBaseInfoMonitorId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  private String indicatorViewBaseInfoId;

  @Schema(title = "指标基本信息监测表名称")
  private String name;

  @Schema(title = "展示顺序")
  private Integer seq;

  private List<CreateOrUpdateIndicatorViewBaseInfoMonitorContentRequestRs> createOrUpdateIndicatorViewBaseInfoMonitorContentRequestRsList;
}
