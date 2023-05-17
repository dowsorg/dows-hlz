package org.dows.hep.rest.base.risk;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.risk.RiskDangerPointBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "危险分数", description = "危险分数")
public class RiskDangerPointRest {
  private final RiskDangerPointBiz riskDangerPointBiz;

  @Operation(summary = "Rs删除危险分数")
  @DeleteMapping("v1/baseRisk/riskDangerPoint/deleteRs")
  public void deleteRs(@RequestParam String riskDangerPointId) {
    riskDangerPointBiz.deleteRs(riskDangerPointId);
  }
}
