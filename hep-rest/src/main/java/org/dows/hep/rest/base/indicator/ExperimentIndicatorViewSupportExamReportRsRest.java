package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.ExperimentSupportExamCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentSupportExamReportResponseRs;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorViewSupportExamReportRsBiz;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验查看指标辅助检查检查报告类", description = "实验查看指标辅助检查类")
public class ExperimentIndicatorViewSupportExamReportRsRest {
  private final ExperimentIndicatorViewSupportExamReportRsBiz experimentIndicatorViewSupportExamReportRsBiz;

  @Operation(summary = "实验人物和在这个机构功能点下的辅助检查报告")
  @PostMapping("v1/userExperiment/supportExamReport/check")
  public void supportExamCheck(@RequestBody ExperimentSupportExamCheckRequestRs experimentSupportExamCheckRequestRs) {
    experimentIndicatorViewSupportExamReportRsBiz.supportExamCheck(experimentSupportExamCheckRequestRs);
  }

  @Operation(summary = "根据实验人物id和功能点id查找辅助报告")
  @GetMapping("v1/userExperiment/supportExamReport/get")
  public List<ExperimentSupportExamReportResponseRs> get(@RequestParam String appId, @RequestParam String experimentId, @RequestParam String indicatorFuncId, @RequestParam String experimentPersonId, String experimentOrgId) {
    return experimentIndicatorViewSupportExamReportRsBiz.get(appId, experimentId, indicatorFuncId, experimentPersonId, experimentOrgId);
  }
}
