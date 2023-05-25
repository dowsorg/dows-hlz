package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrUpdateIndicatorJudgeHealthProblemRequestRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorJudgeHealthProblemId;

  @Schema(title = "应用ID")
  @ApiModelProperty(required = true)
  private String appId;

  @Schema(title = "指标功能ID")
  @ApiModelProperty(required = true)
  private String indicatorFuncId;

  @Schema(title = "健康问题名称")
  private String name;

  @Schema(title = "健康问题类别")
  @ApiModelProperty(required = true)
  private String indicatorCategoryId;

  @Schema(title = "分数")
  @ApiModelProperty(required = true)
  private Double point;

  @Schema(title = "判断规则")
  @ApiModelProperty(required = true)
  private String expression;

  @Schema(title = "结果说明")
  @ApiModelProperty(required = true)
  private String resultExplain;

  @Schema(title = "0-禁用，1-启用")
  @ApiModelProperty(required = true)
  private Integer status;
}
