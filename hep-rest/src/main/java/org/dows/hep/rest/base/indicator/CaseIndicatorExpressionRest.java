package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CaseCreateOrUpdateIndicatorExpressionRequestRs;
import org.dows.hep.biz.base.indicator.CaseIndicatorExpressionBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "指标公式", description = "指标公式")
public class CaseIndicatorExpressionRest {
  private final CaseIndicatorExpressionBiz caseIndicatorExpressionBiz;

  @Operation(summary = "创建指标公式")
  @PostMapping("v1/caseIndicator/caseIndicatorExpression/createOrUpdateRs")
  public String createOrUpdate(@RequestBody CaseCreateOrUpdateIndicatorExpressionRequestRs caseCreateOrUpdateIndicatorExpressionRequestRs) throws InterruptedException {
    return caseIndicatorExpressionBiz.createOrUpdate(caseCreateOrUpdateIndicatorExpressionRequestRs);
  }
}
