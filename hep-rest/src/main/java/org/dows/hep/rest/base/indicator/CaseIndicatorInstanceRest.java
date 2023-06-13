package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CaseCreateCopyToPersonRequestRs;
import org.dows.hep.biz.base.indicator.CaseIndicatorInstanceBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "案例指标实例", description = "案例指标实例")
public class CaseIndicatorInstanceRest {
  private final CaseIndicatorInstanceBiz caseIndicatorInstanceBiz;

  @Operation(summary = "复制数据库指标管理给人物")
  @PostMapping("v1/caseIndicator/indicatorInstance/copy")
  public void copyToPerson(@RequestBody CaseCreateCopyToPersonRequestRs caseCreateCopyToPersonRequestRs) {
    caseIndicatorInstanceBiz.copyToPerson(caseCreateCopyToPersonRequestRs);
  }
}
