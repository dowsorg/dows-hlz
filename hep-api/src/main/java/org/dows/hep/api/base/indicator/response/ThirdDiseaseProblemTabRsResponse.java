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
public class ThirdDiseaseProblemTabRsResponse implements Serializable {
  @Schema(title = "第三层目录id")
  private String indicatorCategoryId;

  @Schema(title = "第三层目录名称")
  private String indicatorCategoryName;

  @Schema(title = "查看指标-四级类-无报告-辅助检查列表")
  private List<ExperimentIndicatorJudgeDiseaseProblemRsResponse> experimentIndicatorJudgeDiseaseProblemRsResponseList;
}
