package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.IndicatorViewBaseInfoMonitorBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "查看指标基本信息指标监测表", description = "查看指标基本信息指标监测表")
public class IndicatorViewBaseInfoMonitorRest {
  private final IndicatorViewBaseInfoMonitorBiz indicatorViewBaseInfoMonitorBiz;

  @Operation(summary = "删除查看指标基本信息指标监测表")
  @DeleteMapping("v1/baseIndicator/indicatorViewBaseInfoMonitor/deleteIndicatorViewBaseInfoMonitor")
  public void deleteIndicatorViewBaseInfoMonitor(@RequestParam String indicatorViewBaseInfoMonitorId) throws InterruptedException {
    indicatorViewBaseInfoMonitorBiz.deleteIndicatorViewBaseInfoMonitor(indicatorViewBaseInfoMonitorId);
  }
}
