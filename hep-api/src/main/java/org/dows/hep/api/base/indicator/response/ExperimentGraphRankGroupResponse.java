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
public class ExperimentGraphRankGroupResponse implements Serializable {
  @Schema(title = "实验小组ID")
  private String experimentGroupId;

  @Schema(title = "实验小组序号")
  private String experimentGroupNo;

  @Schema(title = "实验小组别名")
  private String experimentGroupAlias;

  @Schema(title = "实验小组名称")
  private String experimentGroupName;

  @Schema(title = "知识考点分数")
  private String percentKnowledgeScore;

  @Schema(title = "健康指数分数")
  private String percentHealthIndexScore;

  @Schema(title = "医疗占比分数")
  private String percentTreatmentPercentScore;

  @Schema(title = "总分")
  private String totalScore;

  @Schema(title = "知识考点分数百分比")
  private String percentKnowledge;

  @Schema(title = "健康指数分数百分比")
  private String percentHealthIndex;

  @Schema(title = "医疗占比分数百分比")
  private String percentTreatmentPercent;

  @Schema(title = "排名")
  private String rankNo;
}
