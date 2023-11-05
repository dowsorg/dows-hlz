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
public class ExperimentSupportExamReportResponseRs implements Serializable {

  @Schema(title = "类别id")
  private String categId;
  @Schema(title = "类别名称")
  private String categName;
  
  @Schema(title = "辅助检查名称")
  private String name;

  @Schema(title = "项目价格")
  private BigDecimal fee;

  @Schema(title = "指标值")
  private String currentVal;

  @Schema(title = "指标单位")
  private String unit;

  @Schema(title = "结果解读（这里是指标的结果值，并非辅助检查配置的）")
  private String resultExplain;

  @Schema(title = "第一层目录id")
  private String indicatorCategoryId;

  @Schema(title = "第一层目录名称")
  private String indicatorCategoryName;
}
