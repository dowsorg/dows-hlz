package org.dows.hep.api.base.indicator.response;

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
public class ExperimentIndicatorInstanceRsResponse implements Serializable {
  @Schema(title = "指标名称")
  private String indicatorName;

  @Schema(title = "指标值")
  private String currentVal;

  @Schema(title = "单位")
  private String unit;

  public static ExperimentIndicatorInstanceRsResponse getExperimentIndicatorInstanceRsResponse(
      String indicatorName, String currentVal, String unit) {
    return ExperimentIndicatorInstanceRsResponse
        .builder()
        .indicatorName(indicatorName)
        .currentVal(currentVal)
        .unit(unit)
        .build();
  }
}
