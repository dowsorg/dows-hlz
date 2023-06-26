package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.PhysicalExamCheckRequestRs;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorViewPhysicalExamReportRsBiz;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorViewPhysicalExamRsBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验查看指标体格检查检查报告类", description = "实验查看指标体格检查类")
public class ExperimentIndicatorViewPhysicalExamReportRsRest {
  private final ExperimentIndicatorViewPhysicalExamReportRsBiz experimentIndicatorViewPhysicalExamReportRsBiz;

  @Operation(summary = "实验人物和在这个机构功能点下的体格检查报告")
  @PostMapping("v1/userExperiment/physicalExam/check")
  public void physicalExamCheck(@RequestBody PhysicalExamCheckRequestRs physicalExamCheckRequestRs) {
    experimentIndicatorViewPhysicalExamReportRsBiz.physicalExamCheck(physicalExamCheckRequestRs);
  }
}
