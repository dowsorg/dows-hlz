package org.dows.hep.api.base.indicator.response;

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
public class ExperimentMonitorFollowupRsResponse implements Serializable {
  @Schema(title = "监测随访计划（这个人在本期有没有，没有就是null）")
  private ExperimentIndicatorViewMonitorFollowupPlanRsResponse  experimentIndicatorViewMonitorFollowupPlanRsResponse;

  @Schema(title = "监测随访计划列表（所有的监测随访计划表）")
  private List<ExperimentIndicatorViewMonitorFollowupRsResponse> experimentIndicatorViewMonitorFollowupRsResponseList;

  @Schema(title = "监测随访计划报告（如果有，显示上一期的）")
  private ExperimentIndicatorViewMonitorFollowupReportRsResponse experimentIndicatorViewMonitorFollowupReportRsResponse;
}
