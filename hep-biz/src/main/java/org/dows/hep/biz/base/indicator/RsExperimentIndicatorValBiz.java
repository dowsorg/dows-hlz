package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;
import org.dows.hep.service.ExperimentIndicatorInstanceRsService;
import org.dows.hep.service.ExperimentIndicatorValRsService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsExperimentIndicatorValBiz {
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;



  public void populateOnePersonKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      String experimentPersonId,
      Integer curPeriods
      ) {


    if (Objects.isNull(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
        || StringUtils.isBlank(experimentPersonId)
        || Objects.isNull(curPeriods)
    ) {return;}
    Set<String> experimentIndicatorInstanceIdSet = experimentIndicatorInstanceRsService.lambdaQuery()
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
        .list()
        .stream()
        .map(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId)
        .collect(Collectors.toSet());
    if (experimentIndicatorInstanceIdSet.isEmpty()) {return;}
    experimentIndicatorValRsService.lambdaQuery()
        .eq(ExperimentIndicatorValRsEntity::getPeriods, curPeriods)
        .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
        .list()
        .forEach(experimentIndicatorValRsEntity -> {
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity);
        });
  }
  public void populateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap, Set<String> experimentIndicatorInstanceIdSet, Integer curPeriods) {
    if (Objects.isNull(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
        || Objects.isNull(experimentIndicatorInstanceIdSet) || experimentIndicatorInstanceIdSet.isEmpty()
        || Objects.isNull(curPeriods)
    ) {return;}
    experimentIndicatorValRsService.lambdaQuery()
        .eq(ExperimentIndicatorValRsEntity::getPeriods, curPeriods)
        .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
        .list()
        .forEach(experimentIndicatorValRsEntity -> {
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity);
        });
  }
}
