package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.RsCalculateCompetitiveScoreRsResponse;
import org.dows.hep.api.base.indicator.response.RsCalculateMoneyScoreRsResponse;
import org.dows.hep.biz.base.indicator.RsExperimentCalculateBiz;
import org.dows.hep.biz.risk.RiskBiz;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.dows.hep.vo.report.PersonRiskFactor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验各种数计算", description = "实验各种数计算")
public class RsExperimentCalculateRest {
  private final RsExperimentCalculateBiz rsExperimentCalculateBiz;
  private final ExperimentScoringBiz experimentScoringBiz;
  private final RiskBiz riskBiz;

  @Operation(summary = "获取实验小组干预危险因素")
  @GetMapping("v1/experimentIndicator/personRiskModel/get")
  public List<PersonRiskFactor> get(String experimentInstanceId, String experimentGroupId, Integer period) {
    return riskBiz.get(experimentInstanceId, experimentGroupId, period);
  }

  @Operation(summary = "功能结算点（比如健康指导点击后）调用这个封装好的方法")
  @PostMapping("v1/experimentIndicator/func/reCalculate")
  public void experimentReCalculateFunc(@RequestBody RsExperimentCalculateFuncRequest rsExperimentCalculateFuncRequest) throws ExecutionException, InterruptedException {
    rsExperimentCalculateBiz.experimentReCalculateFunc(rsExperimentCalculateFuncRequest);
  }

  @Operation(summary = "期数翻转与我相关")
  @PostMapping("v1/experimentIndicator/periods/reCalculate")
  public void experimentReCalculatePeriods(@RequestBody RsCalculatePeriodsRequest rsCalculatePeriodsRequest) throws ExecutionException, InterruptedException {
    rsExperimentCalculateBiz.experimentReCalculatePeriods(rsCalculatePeriodsRequest);
  }

  @Operation(summary = "实验-期数重新计算N个人所有指标，如果不传人，就是所有的")
  @PostMapping("v1/experimentIndicator/person/reCalculate")
  public void experimentReCalculatePerson(@RequestBody RsCalculatePersonRequestRs rsCalculatePersonRequestRs) throws ExecutionException, InterruptedException {
    rsExperimentCalculateBiz.experimentReCalculatePerson(rsCalculatePersonRequestRs);
  }

  @Operation(summary = "实验-计算健康指数")
  @PostMapping("v1/experimentIndicator/healthScore/calculate")
  public void experimentRsCalculateHealthScore(@RequestBody ExperimentRsCalculateHealthScoreRequestRs experimentRsCalculateHealthScoreRequestRs) throws ExecutionException, InterruptedException {
    rsExperimentCalculateBiz.experimentRsCalculateHealthScore(experimentRsCalculateHealthScoreRequestRs);
  }

  @Operation(summary = "实验-计算健康指数并且生成报告")
  @PostMapping("v1/experimentIndicator/healthScore/calculateAndCreateReport")
  public void experimentRsCalculateAndCreateReportHealthScore(@RequestBody ExperimentRsCalculateAndCreateReportHealthScoreRequestRs experimentRsCalculateAndCreateReportHealthScoreRequestRs) throws ExecutionException, InterruptedException {
    rsExperimentCalculateBiz.experimentRsCalculateAndCreateReportHealthScore(experimentRsCalculateAndCreateReportHealthScoreRequestRs);
  }

  @Operation(summary = "计算医疗占比")
  @PostMapping("v1/experimentIndicator/moneyScore/calculate")
  public RsCalculateMoneyScoreRsResponse rsCalculateMoneyScore(@RequestBody RsCalculateMoneyScoreRequestRs rsCalculateMoneyScoreRequestRs) {
    return experimentScoringBiz.rsCalculateMoneyScore(rsCalculateMoneyScoreRequestRs);
  }

  @Operation(summary = "计算出实验小组的竞争性得分")
  @PostMapping("v1/experimentIndicator/competitiveScore/calculate")
  public RsCalculateCompetitiveScoreRsResponse rsCalculateCompetitiveScore(@RequestBody RsCalculateCompetitiveScoreRequestRs rsCalculateCompetitiveScoreRequestRs) {
    return experimentScoringBiz.rsCalculateCompetitiveScore(rsCalculateCompetitiveScoreRequestRs);
  }


  @Operation(summary = "计算前设置持续天数当前值")
  @PutMapping("v1/experimentIndicator/duration/put")
  public void experimentSetDuration(@RequestBody RsExperimentSetDurationRequest rsExperimentSetDurationRequest) throws ExecutionException, InterruptedException {
    rsExperimentCalculateBiz.experimentSetDuration(rsExperimentSetDurationRequest);
  }

  @Operation(summary = "期数翻转结束后，要把本期最终结果更新为下一期的值")
  @PutMapping("v1/experimentIndicator/val/put")
  public void experimentSetVal(@RequestBody RsExperimentSetValRequest rsExperimentSetValRequest) throws ExecutionException, InterruptedException {
    rsExperimentCalculateBiz.cfExperimentSetVal(rsExperimentSetValRequest);
  }
}
