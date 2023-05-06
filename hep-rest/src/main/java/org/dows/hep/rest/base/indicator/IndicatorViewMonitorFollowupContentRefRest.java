package org.dows.hep.rest.base.indicator;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.Acceleration;
import org.dows.hep.biz.base.indicator.IndicatorViewMonitorFollowupContentRefBiz;
import org.dows.hep.service.IndicatorViewMonitorFollowupContentRefService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
public class IndicatorViewMonitorFollowupContentRefRest {
  private final IndicatorViewMonitorFollowupContentRefBiz indicatorViewMonitorFollowupContentRefBiz;

  @DeleteMapping("v1/baseIndicator/indicatorViewMonitorFollowupContentRef/delete")
  public void delete(@RequestParam String indicatorViewMonitorFollowupContentRefId) throws InterruptedException {
    indicatorViewMonitorFollowupContentRefBiz.delete(indicatorViewMonitorFollowupContentRefId);
  }
}
