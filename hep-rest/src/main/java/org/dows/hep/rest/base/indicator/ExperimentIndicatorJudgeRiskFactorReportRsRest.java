package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.ExperimentRiskFactorCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentRiskFactorReportResponseRs;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorJudgeRiskFactorReportRsBiz;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验判断操作-危险因素报告类", description = "实验判断操作-危险因素报告类")
public class ExperimentIndicatorJudgeRiskFactorReportRsRest {
  private final ExperimentIndicatorJudgeRiskFactorReportRsBiz experimentIndicatorJudgeRiskFactorReportRsBiz;

  /* runsix:TODO 这是一个结束操作，报告后期加 */
  @Operation(summary = "实验人物和在这个机构功能点下的危险因素报告")
  @PostMapping("v1/userExperiment/riskFactorReport/check")
  public void riskFactorCheck(@RequestBody ExperimentRiskFactorCheckRequestRs experimentRiskFactorCheckRequestRs) {
    experimentIndicatorJudgeRiskFactorReportRsBiz.riskFactorCheck(experimentRiskFactorCheckRequestRs);
  }

  @Operation(summary = "根据实验人物id和功能点id查找这次挂号的上一次危险因素报告")
  @GetMapping("v1/userExperiment/riskFactorReport/get")
  public List<ExperimentRiskFactorReportResponseRs> get(
      @RequestParam String appId,
      @RequestParam String experimentId,
      @RequestParam String indicatorFuncId,
      @RequestParam String experimentPersonId,
      @RequestParam String experimentOrgId) {
    return experimentIndicatorJudgeRiskFactorReportRsBiz.get(appId, experimentId, indicatorFuncId, experimentPersonId, experimentOrgId);
  }
}
