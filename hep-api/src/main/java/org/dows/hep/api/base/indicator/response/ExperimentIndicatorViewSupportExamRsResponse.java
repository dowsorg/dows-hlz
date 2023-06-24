package org.dows.hep.api.base.indicator.response;

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
public class ExperimentIndicatorViewSupportExamRsResponse implements Serializable {
  @Schema(title = "分布式ID")
  private String experimentIndicatorViewSupportExamId;

  @Schema(title = "分布式ID")
  private String indicatorViewSupportExamId;

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

  @Schema(title = "关联指标")
  private String indicatorInstanceId;

  @Schema(title = "结果解析")
  private String resultAnalysis;

  @Schema(title = "0-禁用，1-启用")
  private Integer status;

  @Schema(title = "目录id列表")
  private String indicatorCategoryIdArray;

  @Schema(title = "目录名称列表")
  private String indicatorCategoryNameArray;
}
