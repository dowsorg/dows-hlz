package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.BatchBindReasonIdRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateIndicatorExpressionRequestRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.biz.base.indicator.IndicatorExpressionBiz;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
  public String createOrUpdate(@RequestBody CreateOrUpdateIndicatorExpressionRequestRs createOrUpdateIndicatorExpressionRequestRs) throws InterruptedException {
    return indicatorExpressionBiz.createOrUpdate(createOrUpdateIndicatorExpressionRequestRs);
  }

  @Operation(summary = "V2创建指标公式")
  @PostMapping("v2/baseIndicator/indicatorExpression/createOrUpdateRs")
  public String v2CreateOrUpdate(@RequestBody CreateOrUpdateIndicatorExpressionRequestRs createOrUpdateIndicatorExpressionRequestRs) throws InterruptedException, ExecutionException {
    return indicatorExpressionBiz.v2CreateOrUpdate(createOrUpdateIndicatorExpressionRequestRs);
  }

  @Operation(summary = "批量绑定公式与产生公式原因")
  @PostMapping("v1/baseIndicator/indicatorExpression/batchBindReasonId")
  public void batchBindReasonId(@RequestBody BatchBindReasonIdRequestRs batchBindReasonIdRequestRs) {
    indicatorExpressionBiz.batchBindReasonId(batchBindReasonIdRequestRs);
  }

  @Operation(summary = "根据公式id查询出所有")
  @GetMapping("v1/baseIndicator/indicatorExpression/get")
  public IndicatorExpressionResponseRs get(@RequestParam String indicatorExpressionId) {
    return indicatorExpressionBiz.get(indicatorExpressionId);
  }
}
