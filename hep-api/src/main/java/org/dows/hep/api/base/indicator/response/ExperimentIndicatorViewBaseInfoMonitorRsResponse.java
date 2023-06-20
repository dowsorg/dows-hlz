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
public class ExperimentIndicatorViewBaseInfoMonitorRsResponse implements Serializable {
  @Schema(title = "指标基本信息-监测表名称")
  private String name;

  @Schema(title = "指标基本信息-监测表监测内容列表")
  private List<ExperimentIndicatorViewBaseInfoMonitorContentRsResponse> experimentIndicatorViewBaseInfoMonitorContentRsResponseList;
}
