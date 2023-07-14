package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.RsCalculateCompetitiveScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsCalculateHealthScoreRequestRs;
import org.dows.hep.api.base.indicator.request.RsCalculateMoneyScoreRequestRs;
import org.dows.hep.api.base.indicator.response.RsCalculateCompetitiveScoreRsResponse;
import org.dows.hep.api.base.indicator.response.RsCalculateMoneyScoreRsResponse;
import org.dows.hep.biz.base.indicator.RsCalculateBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验各种数计算", description = "实验各种数计算")
public class RsCalculateRest {
  private final RsCalculateBiz rsCalculateBiz;

  @Operation(summary = "计算健康指数")
  @PostMapping("v1/experimentIndicator/healthScore/calculate")
  public void rsCalculateHealthScore(RsCalculateHealthScoreRequestRs rsCalculateHealthScoreRequestRs) throws ExecutionException, InterruptedException {
    rsCalculateBiz.rsCalculateHealthScore(rsCalculateHealthScoreRequestRs);
  }

  @Operation(summary = "计算医疗占比")
  @PostMapping("v1/experimentIndicator/moneyScore/calculate")
  public RsCalculateMoneyScoreRsResponse rsCalculateMoneyScore(RsCalculateMoneyScoreRequestRs rsCalculateMoneyScoreRequestRs) {
    return rsCalculateBiz.rsCalculateMoneyScore(rsCalculateMoneyScoreRequestRs);
  }

  @Operation(summary = "计算出实验小组的竞争性得分")
  @PostMapping("v1/experimentIndicator/competitiveScore/calculate")
  public RsCalculateCompetitiveScoreRsResponse rsCalculateCompetitiveScore(RsCalculateCompetitiveScoreRequestRs rsCalculateCompetitiveScoreRequestRs) {
    return rsCalculateBiz.rsCalculateCompetitiveScore(rsCalculateCompetitiveScoreRequestRs);
  }
}
