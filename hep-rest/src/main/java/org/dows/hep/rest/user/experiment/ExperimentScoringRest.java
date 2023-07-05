package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.ExperimentRankResponse;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "实验分数", description = "实验分数")
public class ExperimentScoringRest {
  private final ExperimentScoringBiz experimentScoringBiz;

  @Operation(summary = "实验排行榜")
  @GetMapping("v1/userExperiment/experimentScore/getRank")
  public ExperimentRankResponse getRank(@RequestParam String experimentId) {
    return experimentScoringBiz.getRank(experimentId);
  }
}
