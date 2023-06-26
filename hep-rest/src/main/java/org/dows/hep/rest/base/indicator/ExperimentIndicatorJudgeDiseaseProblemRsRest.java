package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.FirstDiseaseProblemTabRsResponse;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorJudgeDiseaseProblemRsBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验查看指标辅助检查类", description = "实验查看指标辅助检查类")
public class ExperimentIndicatorJudgeDiseaseProblemRsRest {
  private final ExperimentIndicatorJudgeDiseaseProblemRsBiz experimentIndicatorJudgeDiseaseProblemRsBiz;

  @Operation(summary = "根据功能点id查询出所有的查看指标-辅助检查")
  @GetMapping("v1/userExperiment/diseaseProblem/get")
  public List<FirstDiseaseProblemTabRsResponse> get(
      @RequestParam String indicatorFuncId
  ) {
    return experimentIndicatorJudgeDiseaseProblemRsBiz.get(indicatorFuncId);
  }
}
