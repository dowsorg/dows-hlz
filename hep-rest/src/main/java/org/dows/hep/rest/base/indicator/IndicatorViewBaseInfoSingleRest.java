package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.IndicatorViewBaseInfoSingleBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "查看指标基本信息单一指标", description = "查看指标基本信息单一指标")
public class IndicatorViewBaseInfoSingleRest {
  private final IndicatorViewBaseInfoSingleBiz indicatorViewBaseInfoSingleBiz;

  @Operation(summary = "删除查看指标基本信息单一指标")
  @DeleteMapping("v1/baseIndicator/indicatorViewBaseInfoSingle/deleteIndicatorViewBaseInfoSingle")
  public void deleteIndicatorViewBaseInfoSingle(@RequestParam String indicatorViewBaseInfoSingleId) throws InterruptedException {
    indicatorViewBaseInfoSingleBiz.deleteIndicatorViewBaseInfoSingle(indicatorViewBaseInfoSingleId);
  }
}
