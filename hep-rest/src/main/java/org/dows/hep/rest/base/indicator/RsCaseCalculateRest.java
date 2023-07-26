package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.RsCalculateCompetitiveScoreRsResponse;
import org.dows.hep.api.base.indicator.response.RsCalculateMoneyScoreRsResponse;
import org.dows.hep.biz.base.indicator.RsCaseCalculateBiz;
import org.dows.hep.biz.base.indicator.RsDatabaseCalculateBiz;
import org.dows.hep.biz.base.indicator.RsExperimentCalculateBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验各种数计算", description = "实验各种数计算")
public class RsCaseCalculateRest {
  private final RsCaseCalculateBiz rsCaseCalculateBiz;

  @Operation(summary = "案例-重新计算一个人所有指标")
  @PostMapping("v1/caseIndicator/onePerson/reCalculate")
  public void caseReCalculateOnePerson(@RequestBody ReCalculateOnePersonRequestRs reCalculateOnePersonRequestRs) throws ExecutionException, InterruptedException {
    rsCaseCalculateBiz.caseReCalculateOnePerson(reCalculateOnePersonRequestRs);
  }

  @Operation(summary = "案例-计算一个人健康指数")
  @PostMapping("v1/caseIndicator/healthScore/calculate")
  public void caseRsCalculateHealthScore(@RequestBody CaseRsCalculateHealthScoreRequestRs caseRsCalculateHealthScoreRequestRs) throws ExecutionException, InterruptedException {
    rsCaseCalculateBiz.caseRsCalculateHealthScore(caseRsCalculateHealthScoreRequestRs);
  }
}
