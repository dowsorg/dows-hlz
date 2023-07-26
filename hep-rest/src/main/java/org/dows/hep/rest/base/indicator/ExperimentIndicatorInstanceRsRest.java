package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.base.indicator.response.GroupAverageHealthPointResponse;
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

  @Operation(summary = "查询人物初始化资金")
  @GetMapping("v1/experimentIndicator/money/get")
  public String getMoneyDef(
      @RequestParam String experimentPersonId
  ) {
    return experimentIndicatorInstanceRsBiz.getMoneyDef(experimentPersonId);
  }

  @Operation(summary = "实验人物金额变化")
  @PostMapping("v1/experimentIndicator/money/change")
  public void changeMoney(@RequestBody RsChangeMoneyRequest rsChangeMoneyRequest) {
    experimentIndicatorInstanceRsBiz.changeMoney(rsChangeMoneyRequest);
  }

  @Operation(summary = "实验体检人次统计")
  @PostMapping("v1/experimentIndicator/ageRate/stat")
  public List<EchartsDataResonse> statAgeRate(@RequestBody ExperimentIndicatorInstanceRequest experimentIndicatorInstanceRequest) {
    return experimentIndicatorInstanceRsBiz.statAgeRate(experimentIndicatorInstanceRequest);
  }

  @Operation(summary = "实验人物性别统计")
  @PostMapping("v1/experimentIndicator/genderRate/stat")
  public List<EchartsDataResonse> statGenderRate(@RequestBody ExperimentIndicatorInstanceRequest experimentIndicatorInstanceRequest) {
    return experimentIndicatorInstanceRsBiz.statGenderRate(experimentIndicatorInstanceRequest);
  }

  @Operation(summary = "实验某个小组的平均健康指数")
  @GetMapping("v1/experimentIndicator/healthPoint/average")
  public GroupAverageHealthPointResponse groupAverageHealth(
      @RequestParam String experimentGroupId,
      @RequestParam Integer periods
      ) {
    return experimentIndicatorInstanceRsBiz.groupAverageHealth(experimentGroupId, periods);
  }
}