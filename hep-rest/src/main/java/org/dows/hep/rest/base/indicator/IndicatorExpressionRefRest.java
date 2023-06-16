package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.IndicatorExpressionRefBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "指标公式绑定关系", description = "指标公式绑定关系")
public class IndicatorExpressionRefRest {
  private final IndicatorExpressionRefBiz indicatorExpressionRefBiz;

  @Operation(summary = "删除")
  @DeleteMapping("v1/baseIndicator/indicatorExpressionRef/delete")
  public void delete(@RequestParam String indicatorExpressionRefId) {
    indicatorExpressionRefBiz.delete(indicatorExpressionRefId);
  }
}
