package org.dows.hep.api.base.indicator.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperimentPhysicalExamReportResponseRs implements Serializable {
  @Schema(title = "体格检查名称")
  private String name;

  @Schema(title = "项目价格")
  private BigDecimal fee;

  @Schema(title = "指标值")
  private String currentVal;

  @Schema(title = "指标单位")
  private String unit;

  @Schema(title = "结果解读（这里是指标的结果值，并非体格检查配置的）")
  private String resultExplain;
}
