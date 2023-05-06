package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.IndicatorViewBaseInfoMonitorContentBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "查看指标基本信息指标监测内容", description = "查看指标基本信息指标监测内容")
public class IndicatorViewBaseInfoMonitorContentRest {
  private final IndicatorViewBaseInfoMonitorContentBiz indicatorViewBaseInfoMonitorContentBiz;

  @Operation(summary = "删除查看指标基本信息指标监测内容")
  @DeleteMapping("v1/baseIndicator/indicatorViewBaseInfoMonitorContent/deleteIndicatorViewBaseInfoMonitorContent")
  public void deleteIndicatorViewBaseInfoMonitorContent(@RequestParam String indicatorViewBaseInfoMonitorContentId) throws InterruptedException {
    indicatorViewBaseInfoMonitorContentBiz.deleteIndicatorViewBaseInfoMonitorContent(indicatorViewBaseInfoMonitorContentId);
  }
}
