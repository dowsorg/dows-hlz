package org.dows.hep.api.base.indicator.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author runsix
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperimentTotalRankItemResponse implements Serializable {
  @Schema(title = "实验小组ID")
  private String experimentGroupId;

  @Schema(title = "实验小组组数")
  private String experimentGroupNo;

  @Schema(title = "实验小组名称")
  private String experimentGroupName;

  @Schema(title = "总分")
  private String allPeriodsTotalScore;

  @Schema(title = "排名")
  private Integer rankingIndex;

  @Schema(title = "每期总分列表")
  private List<ExperimentTotalRankGroupItemResponse> experimentTotalRankGroupItemResponseList;
}
