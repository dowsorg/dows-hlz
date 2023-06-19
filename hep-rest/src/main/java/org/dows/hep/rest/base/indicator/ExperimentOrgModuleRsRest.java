package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.ExperimentOrgModuleRsResponse;
import org.dows.hep.biz.base.indicator.ExperimentOrgModuleBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验机构功能模块", description = "实验机构功能模块")
public class ExperimentOrgModuleRsRest {
  private final ExperimentOrgModuleBiz experimentOrgModuleBiz;

  @Operation(summary = "根据实验机构id获取信息")
  @GetMapping("v1/experimentOrgModule/experimentOrgModule/get")
  public List<ExperimentOrgModuleRsResponse> getByExperimentOrgIdAndExperimentPersonId(
      @RequestParam String experimentOrgId
      ) {
    return experimentOrgModuleBiz.getByExperimentOrgIdAndExperimentPersonId(experimentOrgId);
  }
}
