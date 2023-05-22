package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrUpdateIndicatorViewBaseInfoMonitorContentRefRequestRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorViewBaseInfoMonitorContentRefId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  private String indicatorViewBaseInfoMonitorContentId;

  @Schema(title = "指标ID")
  private String indicatorInstanceId;

  @Schema(title = "展示顺序")
  private Integer seq;
}
