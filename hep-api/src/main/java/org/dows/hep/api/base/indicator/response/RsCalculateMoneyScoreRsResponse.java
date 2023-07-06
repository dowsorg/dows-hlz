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
public class RsCalculateMoneyScoreRsResponse implements Serializable {
  @Schema(title = "小组医疗占比得分列表")
  private List<GroupMoneyScoreRsResponse> groupMoneyScoreRsResponseList;
}
