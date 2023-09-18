package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.annotation.Resubmit;
import org.dows.hep.api.base.indicator.request.ExperimentMonitorFollowupCheckRequestRs;
import org.dows.hep.api.base.indicator.response.ExperimentMonitorFollowupRsResponse;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorViewMonitorFollowupReportRsBiz;
import org.dows.hep.biz.eval.FollowupBiz;
import org.springframework.web.bind.annotation.*;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验查看指标监测随访类", description = "实验查看指标监测随访类")
public class ExperimentIndicatorViewMonitorFollowupReportRsRest {
  private final ExperimentIndicatorViewMonitorFollowupReportRsBiz experimentIndicatorViewMonitorFollowupReportRsBiz;

  private final FollowupBiz followupBiz;

  @Resubmit(duration = 3)
  @Operation(summary = "进行随访")
  @PostMapping("v1/userExperiment/monitorFollowup/check")
  public void monitorFollowupCheck(@RequestBody ExperimentMonitorFollowupCheckRequestRs experimentMonitorFollowupCheckRequestRs) {
    //experimentIndicatorViewMonitorFollowupReportRsBiz.monitorFollowupCheck(experimentMonitorFollowupCheckRequestRs);
    followupBiz.monitorFollowupCheck(experimentMonitorFollowupCheckRequestRs);
  }


  @Operation(summary = "根据功能点id查询出所有的查看指标-监测随访")
  @GetMapping("v1/userExperiment/monitorFollowup/get")
  public ExperimentMonitorFollowupRsResponse get(
      @RequestParam String indicatorFuncId,
      @RequestParam String experimentPersonId,
      @RequestParam Integer periods
      ) {
    //return experimentIndicatorViewMonitorFollowupReportRsBiz.get(indicatorFuncId, experimentPersonId, periods);
    return followupBiz.get(indicatorFuncId, experimentPersonId, periods);
  }

}
