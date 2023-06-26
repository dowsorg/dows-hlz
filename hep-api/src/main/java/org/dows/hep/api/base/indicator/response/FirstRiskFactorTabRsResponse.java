package org.dows.hep.api.base.indicator.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirstRiskFactorTabRsResponse implements Serializable {
  @Schema(title = "第一层目录id")
  private String indicatorCategoryId;

  @Schema(title = "第一层目录名称")
  private String indicatorCategoryName;

  @Schema(title = "危险因素列表")
  private List<ExperimentIndicatorJudgeRiskFactorRsResponse> children;
}
