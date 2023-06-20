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
public class ExperimentIndicatorViewPhysicalExamRsResponse implements Serializable {
  @Schema(title = "分布式ID")
  private String experimentIndicatorViewPhysicalExamId;

  @Schema(title = "分布式ID")
  private String indicatorViewPhysicalExamId;

  @Schema(title = "实验id")
  private String experimentId;

  @Schema(title = "案例id")
  private String caseId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "体格检查名称")
  private String name;

  @Schema(title = "费用")
  private BigDecimal fee;

  @Schema(title = "指标")
  private ExperimentIndicatorInstanceRsResponse experimentIndicatorInstanceRsResponse;

  @Schema(title = "结果解析")
  private String resultAnalysis;

  @Schema(title = "0-禁用，1-启用")
  private Integer status;
}
