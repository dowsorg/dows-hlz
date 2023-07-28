package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.RsCopyCrowdsAndRiskModelRequestRs;
import org.dows.hep.api.base.indicator.request.RsCopyIndicatorFuncRequestRs;
import org.dows.hep.api.base.indicator.request.RsCopyPersonIndicatorRequestRs;
import org.dows.hep.biz.base.indicator.RsCopyBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

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
  public void rsCopyIndicatorFunc(RsCopyIndicatorFuncRequestRs rsCopyIndicatorFuncRequestRs) {
    rsCopyBiz.rsCopyIndicatorFunc(rsCopyIndicatorFuncRequestRs);
  }

  @Operation(summary = "复制人物指标以及人物指标的公式到实验")
  @PostMapping("v1/experimentIndicator/personIndicator/rsCopy")
  public void rsCopyPersonIndicator(RsCopyPersonIndicatorRequestRs rsCopyPersonIndicatorRequestRs) throws ExecutionException, InterruptedException {
    rsCopyBiz.rsCopyPersonIndicator(rsCopyPersonIndicatorRequestRs);
  }

  @Operation(summary = "复制人群类型以及死亡原因以及公式到实验")
  @PostMapping("v1/experimentIndicator/crowdsAndRiskModel/rsCopy")
  public void rsCopyCrowdsAndRiskModel(RsCopyCrowdsAndRiskModelRequestRs rsCopyCrowdsAndRiskModelRequestRs) throws ExecutionException, InterruptedException {
    rsCopyBiz.rsCopyCrowdsAndRiskModel(rsCopyCrowdsAndRiskModelRequestRs);
  }
}
