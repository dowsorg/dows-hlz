package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.service.ExperimentIndicatorInstanceRsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsExperimentIndicatorInstanceBiz {
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;

  public Map<String, Map<String, String>> getKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMapByExperimentPersonIdList(List<String> experimentPersonIdList) {
    Map<String, Map<String, String>> kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
    if (Objects.nonNull(experimentPersonIdList) && !experimentPersonIdList.isEmpty()) {

    }
    return kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap;
  }
}
