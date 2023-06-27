package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.ExperimentHealthProblemCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentHealthProblemReportResponseRs;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorJudgeHealthProblemReportRsBiz;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验判断操作-健康问题报告类", description = "实验判断操作-健康问题报告类")
public class ExperimentIndicatorJudgeHealthProblemReportRsRest {
  private final ExperimentIndicatorJudgeHealthProblemReportRsBiz experimentIndicatorJudgeHealthProblemReportRsBiz;

  @Operation(summary = "实验人物和在这个机构功能点下的健康问题报告")
  @PostMapping("v1/userExperiment/healthProblemReport/check")
  public void healthProblemCheck(@RequestBody ExperimentHealthProblemCheckRequestRs experimentHealthProblemCheckRequestRs) {
    experimentIndicatorJudgeHealthProblemReportRsBiz.healthProblemCheck(experimentHealthProblemCheckRequestRs);
  }

  @Operation(summary = "根据实验人物id和功能点id查找这次挂号的上一次健康问题报告")
  @GetMapping("v1/userExperiment/healthProblemReport/get")
  public List<ExperimentHealthProblemReportResponseRs> get(
      @RequestParam String appId,
      @RequestParam String experimentId,
      @RequestParam String indicatorFuncId,
      @RequestParam String experimentPersonId,
      @RequestParam String experimentOrgId) {
    return experimentIndicatorJudgeHealthProblemReportRsBiz.get(appId, experimentId, indicatorFuncId, experimentPersonId, experimentOrgId);
  }
}
