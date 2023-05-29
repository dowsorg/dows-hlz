package org.dows.hep.api.base.indicator.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
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
public class CreateOrUpdateIndicatorViewPhysicalExamRequestRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorViewPhysicalExamId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "指标功能ID")
  @ApiModelProperty(required = true)
  private String indicatorFuncId;

  @Schema(title = "体格检查名称")
  @ApiModelProperty(required = true)
  private String name;

  @Schema(title = "体格检查类别")
  @ApiModelProperty(required = true)
  private String indicatorCategoryId;

  @Schema(title = "费用")
  @ApiModelProperty(required = true)
  private Double fee;

  @Schema(title = "关联指标")
  @ApiModelProperty(required = true)
  private String indicatorInstanceId;

  @Schema(title = "结果解析")
  private String resultAnalysis;

  @Schema(title = "0-禁用，1-启用")
  @ApiModelProperty(required = true)
  private Integer status;

}
