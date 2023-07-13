package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.user.experiment.request.ExperimentIndicatorInstanceRequest;
import org.dows.hep.api.user.experiment.response.EchartsDataResonse;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

  @Operation(summary = "实验人物金额变化")
  @PostMapping("v1/experimentIndicator/money/change")
  public void changeMoney(@RequestBody RsChangeMoneyRequest rsChangeMoneyRequest) {
    experimentIndicatorInstanceRsBiz.changeMoney(rsChangeMoneyRequest);
  }

  @Operation(summary = "实验人物年龄段统计")
  @PostMapping("v1/experimentIndicator/ageRate/stat")
  public List<EchartsDataResonse> statAgeRate(@RequestBody ExperimentIndicatorInstanceRequest experimentIndicatorInstanceRequest) {
    return experimentIndicatorInstanceRsBiz.statAgeRate(experimentIndicatorInstanceRequest);
  }
}