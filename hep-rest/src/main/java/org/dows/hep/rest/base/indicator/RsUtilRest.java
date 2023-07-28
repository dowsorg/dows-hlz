package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.biz.base.indicator.RsUtilBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class RsUtilRest {
  public final RsUtilBiz rsUtilBiz;

  @Operation(summary = "根据实验id获取实验沙盘设置")
  @GetMapping("v1/experimentIndicator/sandSetting/get")
  public ExperimentSetting.SandSetting getByExperimentId(String experimentId) {
    return rsUtilBiz.getByExperimentId(experimentId);
  }
}
