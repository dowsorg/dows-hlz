package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.FirstHealthProblemTabRsResponse;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorJudgeHealthProblemRsBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验判断操作-健康问题类", description = "实验判断操作-健康问题类")
public class ExperimentIndicatorJudgeHealthProblemRsRest {
  private final ExperimentIndicatorJudgeHealthProblemRsBiz experimentIndicatorJudgeHealthProblemRsBiz;

  @Operation(summary = "根据功能点id查询出所有的判断操作-健康问题")
  @GetMapping("v1/userExperiment/healthProblem/get")
  public List<FirstHealthProblemTabRsResponse> get(
      @RequestParam String indicatorFuncId
  ) {
    return experimentIndicatorJudgeHealthProblemRsBiz.get(indicatorFuncId);
  }
}
