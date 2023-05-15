package org.dows.hep.api.base.indicator.request;

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
public class BatchCreateOrUpdateHealthManagementGoalDTO implements Serializable {
  @Schema(title = "判断指标健管目标分布式ID")
  private String indicatorJudgeHealthManagementGoalId;
  @Schema(title = "指标实例ID")
  private String indicatorInstanceId;
  @Schema(title = "分数")
  private BigDecimal point;
  @Schema(title = "公式")
  private String expression;
}
