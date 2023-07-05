package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验指标", description = "实验指标")
public class ExperimentIndicatorInstanceRsRest {
  private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;

  @Operation(summary = "查询健康指数")
  @GetMapping("v1/experimentIndicator/healthPoint/get")
  public String getHealthPoint(
      @RequestParam String experimentPersonId,
      @RequestParam(required = false, defaultValue = "1") Integer periods
      ) {
    return experimentIndicatorInstanceRsBiz.getHealthPoint(periods, experimentPersonId);
  }
}
