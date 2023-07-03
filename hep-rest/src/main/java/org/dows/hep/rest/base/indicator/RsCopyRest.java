package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.RsCopyExperimentRequestRs;
import org.dows.hep.api.base.indicator.request.RsCopyPersonIndicatorRequestRs;
import org.dows.hep.biz.base.indicator.RsCopyBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "实验复制数据", description = "实验复制数据")
public class RsCopyRest {
  private final RsCopyBiz rsCopyBiz;

  @Operation(summary = "复制功能点到实验")
  @PostMapping("v1/experimentIndicator/indicatorFunc/rsCopy")
  public void rsCopyIndicatorFunc(RsCopyExperimentRequestRs rsCopyExperimentRequestRs) {
    rsCopyBiz.rsCopyIndicatorFunc(rsCopyExperimentRequestRs);
  }

  @Operation(summary = "复制人物指标以及人物指标到公式到实验")
  @PostMapping("v1/experimentIndicator/personIndicator/rsCopy")
  public void rsCopyPersonIndicator(RsCopyPersonIndicatorRequestRs rsCopyPersonIndicatorRequestRs) {
    rsCopyBiz.rsCopyPersonIndicator(rsCopyPersonIndicatorRequestRs);
  }
}
