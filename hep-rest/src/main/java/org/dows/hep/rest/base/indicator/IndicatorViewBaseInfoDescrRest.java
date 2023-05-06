package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.IndicatorViewBaseInfoDescrBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "查看指标基本信息描述表类", description = "查看指标基本信息描述表类")
public class IndicatorViewBaseInfoDescrRest {
  private final IndicatorViewBaseInfoDescrBiz indicatorViewBaseInfoDescrBiz;

  @Operation(summary = "删除指标基本信息描述表类")
  @DeleteMapping("v1/baseIndicator/indicatorViewBaseInfoDescr/deleteIndicatorViewBaseInfoDescr")
  public void deleteIndicatorViewBaseInfoDescr(@RequestParam String indicatorViewBaseInfoDescId) throws InterruptedException {
    indicatorViewBaseInfoDescrBiz.deleteIndicatorViewBaseInfoDescr(indicatorViewBaseInfoDescId);
  }
}
