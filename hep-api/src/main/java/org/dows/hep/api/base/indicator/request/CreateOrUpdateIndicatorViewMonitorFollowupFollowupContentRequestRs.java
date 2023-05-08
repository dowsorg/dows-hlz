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
public class CreateOrUpdateIndicatorViewMonitorFollowupFollowupContentRequestRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorViewMonitorFollowupFollowupContentId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "查看指标监测随访类ID")
  private String indicatorViewMonitorFollowupId;

  @Schema(title = "随访内容名称")
  private String name;

  @Schema(title = "展示顺序")
  private Integer seq;

  @Schema(title = "关联指标列表")
  private List<CreateOrUpdateIndicatorViewMonitorFollowupContentRefRequestRs> createOrUpdateIndicatorViewMonitorFollowupContentRefRequestRsList;
}
