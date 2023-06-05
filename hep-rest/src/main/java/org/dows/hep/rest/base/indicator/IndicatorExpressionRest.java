package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorExpressionRequestRs;
import org.dows.hep.biz.base.indicator.IndicatorExpressionBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "指标公式", description = "指标公式")
public class IndicatorExpressionRest {
  private final IndicatorExpressionBiz indicatorExpressionBiz;

  @Operation(summary = "创建指标公式")
  @PostMapping("v1/baseIndicator/indicatorExpression/createOrUpdateRs")
  public void createOrUpdate(@RequestBody CreateOrUpdateIndicatorExpressionRequestRs createOrUpdateIndicatorExpressionRequestRs) throws InterruptedException {
    indicatorExpressionBiz.createOrUpdate(createOrUpdateIndicatorExpressionRequestRs);
  }
}
