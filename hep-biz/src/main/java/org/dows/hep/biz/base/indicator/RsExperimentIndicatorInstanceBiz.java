package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;
import org.dows.hep.service.ExperimentIndicatorInstanceRsService;
import org.dows.hep.service.ExperimentIndicatorValRsService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsExperimentIndicatorInstanceBiz {
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;

  public void populateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMapByExperimentPersonIdSet(
      Map<String, Map<String, String>> kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
      Set<String> experimentPersonIdSet) {
    if (Objects.isNull(kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap)) {
      return ;
    }
    if (Objects.nonNull(experimentPersonIdSet) && !experimentPersonIdSet.isEmpty()) {
      experimentIndicatorInstanceRsService.lambdaQuery()
          .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonIdSet)
          .list()
          .forEach(experimentIndicatorInstanceRsEntity -> {
            String experimentPersonId = experimentIndicatorInstanceRsEntity.getExperimentPersonId();
            String indicatorInstanceId = experimentIndicatorInstanceRsEntity.getIndicatorInstanceId();
            String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
            Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.get(experimentPersonId);
            if (Objects.isNull(kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap)) {
              kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap = new HashMap<>();
            }
            kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.put(indicatorInstanceId, experimentIndicatorInstanceId);
            kExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.put(experimentPersonId, kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap);
          });
    }
  }

  public void populateKExperimentIndicatorInstanceIdVExperimentIndicatorValMap(
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      Integer period,
      Set<String> experimentIndicatorInstanceIdSet
  ) {
    if (Objects.isNull(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)) {
      return;
    }
    if (Objects.isNull(period)) {
      return;
    }
    if (Objects.isNull(experimentIndicatorInstanceIdSet) || experimentIndicatorInstanceIdSet.isEmpty()) {
      return;
    }
    experimentIndicatorValRsService.lambdaQuery()
        .eq(ExperimentIndicatorValRsEntity::getPeriods, period)
        .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
        .list()
        .forEach(experimentIndicatorValRsEntity -> {
          String experimentIndicatorInstanceId = experimentIndicatorValRsEntity.getIndicatorInstanceId();
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(experimentIndicatorInstanceId, experimentIndicatorValRsEntity);
        });
  }
}
