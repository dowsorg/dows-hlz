package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.service.ExperimentPersonService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsExperimentPersonBiz {
  private final ExperimentPersonService experimentPersonService;

  public void populateExperimentPersonIdSet(
      Set<String> experimentPersonIdSet,
      String experimentId
  ) {
    if (Objects.isNull(experimentPersonIdSet)
        || StringUtils.isBlank(experimentId)
    ) {return;}
    experimentPersonService.lambdaQuery()
        .eq(ExperimentPersonEntity::getExperimentInstanceId, experimentId)
        .list()
        .forEach(experimentPersonEntity -> {
          experimentPersonIdSet.add(experimentPersonEntity.getExperimentPersonId());
        });
  }
}
