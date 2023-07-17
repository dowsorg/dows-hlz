package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.BatchBindReasonIdRequestRs;
import org.dows.hep.api.base.indicator.request.CaseBatchBindReasonIdRequestRs;
import org.dows.hep.api.base.indicator.request.CaseCreateOrUpdateIndicatorExpressionRequestRs;
import org.dows.hep.api.base.indicator.response.CaseIndicatorExpressionResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.biz.base.indicator.CaseIndicatorExpressionBiz;
import org.springframework.web.bind.annotation.*;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "指标公式", description = "指标公式")
public class CaseIndicatorExpressionRest {
  private final CaseIndicatorExpressionBiz caseIndicatorExpressionBiz;

  @Operation(summary = "创建指标公式")
  @PostMapping("v2/caseIndicator/caseIndicatorExpression/createOrUpdateRs")
  public String v2CreateOrUpdate(@RequestBody CaseCreateOrUpdateIndicatorExpressionRequestRs caseCreateOrUpdateIndicatorExpressionRequestRs) throws InterruptedException {
    return caseIndicatorExpressionBiz.v2CreateOrUpdate(caseCreateOrUpdateIndicatorExpressionRequestRs);
  }

  @Operation(summary = "批量绑定公式与产生公式原因")
  @PostMapping("v1/caseIndicator/indicatorExpression/batchBindReasonId")
  public void batchBindReasonId(@RequestBody CaseBatchBindReasonIdRequestRs caseBatchBindReasonIdRequestRs) {
    caseIndicatorExpressionBiz.batchBindReasonId(caseBatchBindReasonIdRequestRs);
  }

  @Operation(summary = "根据公式id查询出所有")
  @GetMapping("v1/caseIndicator/indicatorExpression/get")
  public CaseIndicatorExpressionResponseRs get(@RequestParam String caseIndicatorExpressionId) {
    return caseIndicatorExpressionBiz.get(caseIndicatorExpressionId);
  }
}
