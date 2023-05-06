package org.dows.hep.rest.base.indicator;

import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.base.indicator.IndicatorViewMonitorFollowupFollowupContentBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author runsix
 */
@RestController
@RequiredArgsConstructor
public class IndicatorViewMonitorFollowupFollowupContentRest {
  private final IndicatorViewMonitorFollowupFollowupContentBiz indicatorViewMonitorFollowupFollowupContentBiz;

  @DeleteMapping("v1/baseIndicator/indicatorViewMonitorFollowupFollowupContent/delete")
  public void delete(@RequestParam String indicatorViewMonitorFollowupFollowupContentId) throws InterruptedException {
    indicatorViewMonitorFollowupFollowupContentBiz.delete(indicatorViewMonitorFollowupFollowupContentId);
  }

  @DeleteMapping("v1/baseIndicator/indicatorViewMonitorFollowupFollowupContent/batchDelete")
  public void delete(@RequestParam List<String> indicatorViewMonitorFollowupFollowupContentIdList) throws InterruptedException {
    indicatorViewMonitorFollowupFollowupContentBiz.batchDelete(indicatorViewMonitorFollowupFollowupContentIdList);
  }
}
