package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.CaseOrgModuleFuncRefBiz;
import org.springframework.web.bind.annotation.*;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "案例模块实例", description = "案例模块实例")
public class CaseOrgModuleFuncRefRest {
  private final CaseOrgModuleFuncRefBiz caseOrgModuleFuncRefBiz;

  @Operation(summary = "单个删除机构模块的")
  @DeleteMapping("v1/caseIndicator/caseOrgModuleFuncRef/delete")
  public void delete(@RequestParam String caseOrgModuleFuncRefId) {
    caseOrgModuleFuncRefBiz.delete(caseOrgModuleFuncRefId);
  }
}
