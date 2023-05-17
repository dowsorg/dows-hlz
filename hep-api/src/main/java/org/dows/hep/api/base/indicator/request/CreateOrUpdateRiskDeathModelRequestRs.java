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
public class CreateOrUpdateRiskDeathModelRequestRs implements Serializable {
  @Schema(title = "死亡模型ID")
  private String riskDeathModelId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  private String riskModelId;

  @Schema(title = "死亡原因名称")
  private String riskDeathReasonName;

  @Schema(title = "死亡概率")
  private Integer riskDeathProbability;

  @Schema(title = "危险分数")
  private List<CreateOrUpdateRiskDangerPointRequestRs> createOrUpdateRiskDangerPointRequestRsList;
}
