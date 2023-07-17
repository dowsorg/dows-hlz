package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.CaseIndicatorExpressionRefBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "指标公式绑定关系", description = "指标公式绑定关系")
public class CaseIndicatorExpressionRefRest {
  private final CaseIndicatorExpressionRefBiz caseIndicatorExpressionRefBiz;

  @Operation(summary = "删除")
  @DeleteMapping("v1/caseIndicator/indicatorExpressionRef/delete")
  public void delete(@RequestParam String caseIndicatorExpressionRefId) {
    caseIndicatorExpressionRefBiz.delete(caseIndicatorExpressionRefId);
  }
}
