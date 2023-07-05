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
public class ExperimentAllPeriodTotalRankResponse implements Serializable {
  @Schema(title = "所有期数总分列表")
  private List<ExperimentTotalRankGroupItemResponse> experimentTotalRankGroupItemResponseList;
}
