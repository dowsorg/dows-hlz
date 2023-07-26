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
public class RsDatabaseCalculateRest {
  private final RsDatabaseCalculateBiz rsDatabaseCalculateBiz;

  @Operation(summary = "数据库-计算指标的健康指数")
  @PostMapping("v1/databaseIndicator/healthScore/calculate")
  public void databaseRsCalculateHealthScore(@RequestBody DatabaseRsCalculateHealthScoreRequestRs databaseRsCalculateHealthScoreRequestRs) throws ExecutionException, InterruptedException {
    rsDatabaseCalculateBiz.databaseRsCalculateHealthScore(databaseRsCalculateHealthScoreRequestRs);
  }
}
