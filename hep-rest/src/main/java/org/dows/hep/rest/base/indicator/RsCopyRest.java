package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CopyViewIndicatorRequestRs;
import org.dows.hep.api.base.indicator.request.JudgeViewIndicatorRequestRs;
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

  @Operation(summary = "复制查看指标到实验")
  @PostMapping("v1/experimentIndicator/viewIndicator/rsCopy")
  public void rsCopyViewIndicator(CopyViewIndicatorRequestRs copyViewIndicatorRequestRs) {
    rsCopyBiz.rsCopyViewIndicator(copyViewIndicatorRequestRs);
  }

  @Operation(summary = "判断操作指标到实验")
  @PostMapping("v1/experimentIndicator/judgeIndicator/rsCopy")
  public void rsCopyJudgeIndicator(JudgeViewIndicatorRequestRs judgeViewIndicatorRequestRs) {
    rsCopyBiz.rsCopyJudgeIndicator(judgeViewIndicatorRequestRs);
  }
}
