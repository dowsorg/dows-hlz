package org.dows.hep.api.base.indicator.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsCalculateCompetitiveScoreRsResponse implements Serializable {
  @Schema(title = "小组竞争性得分")
  Map<String,GroupCompetitiveScoreRsResponse> mapGroupScores;

  @Schema(title = "小组竞争性得分列表")
  List<GroupCompetitiveScoreRsResponse> groupCompetitiveScoreRsResponseList;
}
