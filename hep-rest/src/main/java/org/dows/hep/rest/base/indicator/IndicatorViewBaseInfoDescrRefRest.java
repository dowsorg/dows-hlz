package org.dows.hep.rest.base.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.IndicatorViewBaseInfoDescrRefBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "查看指标基本信息描述表指标关联类", description = "查看指标基本信息描述表指标关联类")
public class IndicatorViewBaseInfoDescrRefRest {
  private final IndicatorViewBaseInfoDescrRefBiz indicatorViewBaseInfoDescrRefBiz;

  @Operation(summary = "删除指标基本信息描述表关联指标")
  @DeleteMapping("v1/baseIndicator/indicatorViewBaseInfoDescrRef/deleteIndicatorViewBaseInfoDescrRef")
  public void deleteIndicatorViewBaseInfoDescrRef(@RequestParam String indicatorViewBaseInfoDescRefId) throws InterruptedException {
    indicatorViewBaseInfoDescrRefBiz.deleteIndicatorViewBaseInfoDescrRef(indicatorViewBaseInfoDescRefId);
  }
}
