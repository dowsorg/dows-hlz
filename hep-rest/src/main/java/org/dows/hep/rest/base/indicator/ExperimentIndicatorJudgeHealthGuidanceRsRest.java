package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.FirstHealthGuidanceTabRsResponse;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorJudgeHealthGuidanceRsBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验判断操作-健康指导类", description = "实验判断操作-健康指导类")
public class ExperimentIndicatorJudgeHealthGuidanceRsRest {
  private final ExperimentIndicatorJudgeHealthGuidanceRsBiz experimentIndicatorJudgeHealthGuidanceRsBiz;

  @Operation(summary = "根据功能点id查询出所有的判断操作-健康指导")
  @GetMapping("v1/userExperiment/healthGuidance/get")
  public List<FirstHealthGuidanceTabRsResponse> get(
      @RequestParam String indicatorFuncId
  ) {
    return experimentIndicatorJudgeHealthGuidanceRsBiz.get(indicatorFuncId);
  }
}
