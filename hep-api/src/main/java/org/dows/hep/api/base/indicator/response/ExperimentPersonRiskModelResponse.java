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
public class ExperimentPersonRiskModelResponse implements Serializable {
  @Schema(title = "死亡原因")
  private String name;

  @Schema(title = "死亡概率")
  private Integer riskDeathProbability;

  @Schema(title = "组合危险分数")
  private Double composeRiskScore;

  @Schema(title = "存在死亡危险")
  private Double existDeathRiskScore;

  @Schema(title = "健康危险因素列表")
  private List<ExperimentPersonHealthRiskFactorRsResponse> experimentPersonHealthRiskFactorRsResponseList;
}
