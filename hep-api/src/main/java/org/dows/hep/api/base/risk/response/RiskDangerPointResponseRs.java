package org.dows.hep.api.base.risk.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceResponseRs;

import java.io.Serializable;
import java.util.Date;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskDangerPointResponseRs implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "数据库ID")
  private Long id;

  @Schema(title = "分布式ID")
  private String riskDangerPointId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "死亡模型ID")
  private String riskDeathModelId;

  @Schema(title = "分布式ID")
  private IndicatorInstanceResponseRs indicatorInstanceResponseRs;

  @Schema(title = "公式")
  private String expression;

  @Schema(title = "时间戳")
  private Date dt;
}
