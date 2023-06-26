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
public class ExperimentIndicatorJudgeHealthGuidanceRsResponse implements Serializable {
  @Schema(title = "分布式ID")
  private String experimentIndicatorJudgeHealthGuidanceId;

  @Schema(title = "分布式ID")
  private String indicatorJudgeHealthGuidanceId;

  @Schema(title = "实验id")
  private String experimentId;

  @Schema(title = "案例id")
  private String caseId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "功能点id")
  private String indicatorFuncId;

  @Schema(title = "疾病问题名称")
  private String name;

  @Schema(title = "分数")
  private BigDecimal point;

  @Schema(title = "结果说明")
  private String resultExplain;

  @Schema(title = "0-禁用，1-启用")
  private Integer status;

  @Schema(title = "目录id列表")
  private String indicatorCategoryIdArray;

  @Schema(title = "目录名称列表")
  private String indicatorCategoryNameArray;
}
