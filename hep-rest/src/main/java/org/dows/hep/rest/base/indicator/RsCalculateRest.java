package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.RsCalculateHealthPointRequestRs;
import org.dows.hep.api.base.indicator.request.RsCalculateMoneyPointRequestRs;
import org.dows.hep.biz.base.indicator.RsCalculateBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验各种数计算", description = "实验各种数计算")
public class RsCalculateRest {
  private final RsCalculateBiz rsCalculateBiz;

  @Operation(summary = "计算健康指数")
  @PostMapping("v1/experimentIndicator/healthPoint/calculate")
  public void rsCalculateHealthPoint(RsCalculateHealthPointRequestRs rsCalculateHealthPointRequestRs) {
    rsCalculateBiz.rsCalculateHealthPoint(rsCalculateHealthPointRequestRs);
  }

  @Operation(summary = "计算医疗占比")
  @PostMapping("v1/experimentIndicator/moneyPoint/calculate")
  public void rsCalculateMoneyPoint(RsCalculateMoneyPointRequestRs rsCalculateMoneyPointRequestRs) {
    rsCalculateBiz.rsCalculateMoneyPoint(rsCalculateMoneyPointRequestRs);
  }
}
