package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.BatchCreateOrUpdateCaseOrgModuleRequestRs;
import org.dows.hep.api.base.indicator.response.CaseOrgModuleResponseRs;
import org.dows.hep.biz.base.indicator.CaseOrgModuleBiz;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "案例模块实例", description = "案例模块实例")
public class CaseOrgModuleRest {
  private final CaseOrgModuleBiz caseOrgModuleBiz;

  @Operation(summary = "批量新增或修改机构模块")
  @PostMapping("v1/caseIndicator/caseOrgModule/batchCreateOrUpdate")
  public void batchCreateOrUpdate(@RequestBody BatchCreateOrUpdateCaseOrgModuleRequestRs batchCreateOrUpdateCaseOrgModuleRequestRs) {
    caseOrgModuleBiz.batchCreateOrUpdate(batchCreateOrUpdateCaseOrgModuleRequestRs);
  }

  @Operation(summary = "删除机构模块")
  @DeleteMapping("v1/caseIndicator/caseOrgModule/delete")
  public void delete(@RequestParam String caseOrgModuleId) {
    caseOrgModuleBiz.delete(caseOrgModuleId);
  }

  @Operation(summary = "根据机构id获取所有模块")
  @GetMapping("v1/caseIndicator/caseOrgModule/getByCaseOrgId")
  public List<CaseOrgModuleResponseRs> getByCaseOrgId(
      @RequestParam String appId,
      @RequestParam String caseOrgId
  ) {
    return caseOrgModuleBiz.getByCaseOrgId(appId, caseOrgId);
  }


}
