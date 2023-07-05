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
public class ExperimentTotalRankItemResponse implements Serializable {
  @Schema(title = "期数")
  private Integer periods;

  @Schema(title = "每一期的详情列表")
  private List<ExperimentTotalRankGroupItemResponse> experimentTotalRankGroupItemResponseList;
}
