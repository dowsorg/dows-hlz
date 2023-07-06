package org.dows.hep.api.base.indicator.response;

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
public class GroupMoneyScoreRsResponse implements Serializable {
  @Schema(title = "小组id")
  private String experimentGroupId;

  @Schema(title = "小组医疗占比分")
  private BigDecimal groupMoneyScore;
}
