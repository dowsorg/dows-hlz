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
public class ExperimentIndicatorViewMonitorFollowupFollowupContentRsResponse implements Serializable {
  @Schema(title = "监测随访内容名称")
  private String name;

  @Schema(title = "实验指标响应列表")
  private List<ExperimentIndicatorInstanceRsResponse> experimentIndicatorInstanceRsResponseList;
}
