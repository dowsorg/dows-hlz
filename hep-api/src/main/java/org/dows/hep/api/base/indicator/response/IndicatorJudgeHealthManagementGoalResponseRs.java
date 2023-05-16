package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicatorJudgeHealthManagementGoalResponseRs implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "判断指标健管目标分布式ID")
  private String indicatorJudgeHealthManagementGoalId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "指标功能ID")
  private String indicatorFuncId;

  @Schema(title = "指标实例")
  private IndicatorInstanceResponseRs indicatorInstanceResponseRs;

  @Schema(title = "分数")
  private Double point;

  @Schema(title = "公式")
  private String expression;

  @Schema(title = "时间戳")
  private Date dt;
}
