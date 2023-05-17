package org.dows.hep.api.base.risk.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskDeathModelResponseRs implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "数据库ID")
  private Long id;

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

  @Schema(title = "时间戳")
  private Date dt;

  @Schema(title = "危险分数列表")
  private List<RiskDangerPointResponseRs> riskDangerPointResponseRsList;
}
