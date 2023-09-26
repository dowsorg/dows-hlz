package org.dows.hep.api.base.indicator.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.user.experiment.vo.HealthIndexScoreVO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author runsix
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupCompetitiveScoreRsResponse implements Serializable {
  @Schema(title = "小组id")
  private String experimentGroupId;

  @Schema(title = "小组竞争性健康指数得分")
  private BigDecimal groupCompetitiveScore;

  @Schema(title = "小组案例得分列表")
  private List<HealthIndexScoreVO> personScores;
}
