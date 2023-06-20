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
public class ExperimentIndicatorViewBaseInfoRsResponse implements Serializable {
  private List<ExperimentIndicatorViewBaseInfoDescrRsResponse> experimentIndicatorViewBaseInfoDescrRsResponseList;
  private List<ExperimentIndicatorViewBaseInfoMonitorRsResponse> experimentIndicatorViewBaseInfoMonitorRsResponseList;
  private List<ExperimentIndicatorViewBaseInfoSingleRsResponse> experimentIndicatorViewBaseInfoSingleRsResponseList;
}
