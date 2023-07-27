package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.CaseIndicatorExpressionItemBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "指标公式细项", description = "指标公式细项")
public class CaseIndicatorExpressionItemRest {
  private final CaseIndicatorExpressionItemBiz caseIndicatorExpressionItemBiz;

  @Operation(summary = "删除指标公式细项")
  @DeleteMapping("v1/caseIndicator/indicatorExpressionItem/delete")
  public void delete(@RequestParam String caseIndicatorExpressionItemId) {
    caseIndicatorExpressionItemBiz.delete(caseIndicatorExpressionItemId);
  }
}
