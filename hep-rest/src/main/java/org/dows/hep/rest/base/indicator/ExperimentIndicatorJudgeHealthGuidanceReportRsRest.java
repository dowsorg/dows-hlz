package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.ExperimentHealthGuidanceCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentHealthGuidanceReportResponseRs;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorJudgeHealthGuidanceReportRsBiz;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验判断操作-健康指导报告类", description = "实验判断操作-健康指导报告类")
public class ExperimentIndicatorJudgeHealthGuidanceReportRsRest {
  private final ExperimentIndicatorJudgeHealthGuidanceReportRsBiz experimentIndicatorJudgeHealthGuidanceReportRsBiz;

  /* runsix:TODO 这是一个结束操作，报告后期加 */
  @Operation(summary = "实验人物和在这个机构功能点下的健康指导报告")
  @PostMapping("v1/userExperiment/healthGuidanceReport/check")
  public void healthGuidanceCheck(@RequestBody ExperimentHealthGuidanceCheckRequestRs experimentHealthGuidanceCheckRequestRs) {
    experimentIndicatorJudgeHealthGuidanceReportRsBiz.healthGuidanceCheck(experimentHealthGuidanceCheckRequestRs);
  }

  @Operation(summary = "根据实验人物id和功能点id查找这次挂号的上一次健康指导报告")
  @GetMapping("v1/userExperiment/healthGuidanceReport/get")
  public List<ExperimentHealthGuidanceReportResponseRs> get(
      @RequestParam String appId,
      @RequestParam String experimentId,
      @RequestParam String indicatorFuncId,
      @RequestParam String experimentPersonId,
      @RequestParam String experimentOrgId) {
    return experimentIndicatorJudgeHealthGuidanceReportRsBiz.get(appId, experimentId, indicatorFuncId, experimentPersonId, experimentOrgId);
  }
}
