package org.dows.hep.rest.base.risk;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.risk.RiskDangerPointBiz;
import org.dows.hep.biz.base.risk.RiskDeathModelBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "死亡模型", description = "死亡模型")
public class RiskDeathModelRest {
  private final RiskDeathModelBiz riskDeathModelBiz;

  @Operation(summary = "Rs删除死亡模型")
  @DeleteMapping("v1/baseRisk/riskDeathModel/deleteRs")
  public void deleteRs(@RequestParam String riskDeathModelId) {
    riskDeathModelBiz.deleteRs(riskDeathModelId);
  }
}
