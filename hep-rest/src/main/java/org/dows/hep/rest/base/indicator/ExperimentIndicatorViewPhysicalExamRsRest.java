package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.FirstPhysicalExamTabRsResponse;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorViewPhysicalExamRsBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验查看指标体格检查类", description = "实验查看指标体格检查类")
public class ExperimentIndicatorViewPhysicalExamRsRest {
  private final ExperimentIndicatorViewPhysicalExamRsBiz experimentIndicatorViewPhysicalExamRsBiz;

  @Operation(summary = "根据功能点id查询出所有的查看指标-体格检查")
  @GetMapping("v1/userExperiment/physicalExam/get")
  public List<FirstPhysicalExamTabRsResponse> get(@RequestParam String indicatorFuncId) {
    return experimentIndicatorViewPhysicalExamRsBiz.get(indicatorFuncId);
  }
}
