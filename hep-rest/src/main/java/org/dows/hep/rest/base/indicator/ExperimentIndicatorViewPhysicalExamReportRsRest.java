package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.ExperimentPhysicalExamCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentPhysicalExamReportResponseRs;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorViewPhysicalExamReportRsBiz;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验查看指标体格检查检查报告类", description = "实验查看指标体格检查类")
public class ExperimentIndicatorViewPhysicalExamReportRsRest {
  private final ExperimentIndicatorViewPhysicalExamReportRsBiz experimentIndicatorViewPhysicalExamReportRsBiz;

  @Operation(summary = "实验人物和在这个机构功能点下的体格检查报告")
  @PostMapping("v1/userExperiment/physicalExamReport/check")
  public void physicalExamCheck(@RequestBody ExperimentPhysicalExamCheckRequestRs experimentPhysicalExamCheckRequestRs) {
    experimentIndicatorViewPhysicalExamReportRsBiz.physicalExamCheck(experimentPhysicalExamCheckRequestRs);
  }

  @Operation(summary = "实验人物和在这个机构功能点下的体格检查报告")
  @PostMapping("v2/userExperiment/physicalExamReport/check")
  public void v2PhysicalExamCheck(@RequestBody ExperimentPhysicalExamCheckRequestRs experimentPhysicalExamCheckRequestRs) throws ExecutionException, InterruptedException {
    experimentIndicatorViewPhysicalExamReportRsBiz.v2PhysicalExamCheck(experimentPhysicalExamCheckRequestRs);
  }

  @Operation(summary = "根据实验人物id和功能点id查找体格报告")
  @GetMapping("v1/userExperiment/physicalExamReport/get")
  public List<ExperimentPhysicalExamReportResponseRs> get(
      @RequestParam String appId,
      @RequestParam String experimentId,
      @RequestParam String indicatorFuncId,
      @RequestParam String experimentPersonId,
      @RequestParam String experimentOrgId) {
    return experimentIndicatorViewPhysicalExamReportRsBiz.get(appId, experimentId, indicatorFuncId, experimentPersonId, experimentOrgId);
  }
}
