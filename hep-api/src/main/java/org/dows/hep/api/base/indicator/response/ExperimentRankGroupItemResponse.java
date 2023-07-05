package org.dows.hep.api.base.indicator.response;

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
public class ExperimentRankGroupItemResponse implements Serializable {
  @Schema(title = "实验小组名称")
  private String experimentGroupName;

  @Schema(title = "健康指数分数")
  private String healthIndexScore;

  @Schema(title = "知识考点分数")
  private String knowledgeScore;

  @Schema(title = "医疗占比分数")
  private String treatmentPercentScore;

  @Schema(title = "总分")
  private String totalScore;

  @Schema(title = "期数")
  private Integer periods;
}
