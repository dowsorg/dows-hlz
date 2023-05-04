package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.IndicatorViewBaseInfoMonitorContentRefBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "查看指标基本信息指标监测内容关联指标", description = "查看指标基本信息指标监测内容关联指标")
public class IndicatorViewBaseInfoMonitorContentRefRest {
  private final IndicatorViewBaseInfoMonitorContentRefBiz indicatorViewBaseInfoMonitorContentRefBiz;

  @Operation(summary = "删除查看指标基本信息指标监测内容关联指标")
  @DeleteMapping("v1/baseIndicator/indicatorViewBaseInfoMonitorContentRef/deleteIndicatorViewBaseInfoMonitorContentRef")
  public void deleteIndicatorViewBaseInfoMonitorContentRef(@RequestParam String indicatorViewBaseInfoMonitorContentRefId) throws InterruptedException {
    indicatorViewBaseInfoMonitorContentRefBiz.deleteIndicatorViewBaseInfoMonitorContentRef(indicatorViewBaseInfoMonitorContentRefId);
  }
}
