package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

  public void populateKExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap(
      Map<String, List<ExperimentIndicatorInstanceRsEntity>> kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap,
      Set<String> experimentPersonIdSet,
      String experimentId
  ) {
    if (Objects.isNull(kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap)
        || Objects.isNull(experimentPersonIdSet) || experimentPersonIdSet.isEmpty()
        || StringUtils.isBlank(experimentId)
    ) {return;}
    experimentIndicatorInstanceRsService.lambdaQuery()
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentId, experimentId)
        .in(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonIdSet)
        .list()
        .forEach(experimentIndicatorInstanceRsEntity -> {
          String experimentPersonId = experimentIndicatorInstanceRsEntity.getExperimentPersonId();
          List<ExperimentIndicatorInstanceRsEntity> experimentIndicatorInstanceRsEntityList = kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap.get(experimentPersonId);
          if (Objects.isNull(experimentIndicatorInstanceRsEntityList)) {
            experimentIndicatorInstanceRsEntityList = new ArrayList<>();
          }
          experimentIndicatorInstanceRsEntityList.add(experimentIndicatorInstanceRsEntity);
          kExperimentPersonIdVExperimentIndicatorInstanceRsEntityListMap.put(experimentPersonId, experimentIndicatorInstanceRsEntityList);
        });
  }

  public void populateKExperimentPersonIdVKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap(
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



  public void populateKIndicatorInstanceIdVExperimentIndicatorInstanceIdMap(
      Map<String, String> kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap,
      String experimentPersonId,
      Set<String> indicatorInstanceIdSet
      ) {
    if (Objects.isNull(kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap)) {
      return ;
    }
    if (StringUtils.isBlank(experimentPersonId)) {
      return;
    }
    experimentIndicatorInstanceRsService.lambdaQuery()
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
        .in(Objects.nonNull(indicatorInstanceIdSet) && !indicatorInstanceIdSet.isEmpty(), ExperimentIndicatorInstanceRsEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
        .list()
        .forEach(experimentIndicatorInstanceRsEntity -> {
          kIndicatorInstanceIdVExperimentIndicatorInstanceIdMap.put(
              experimentIndicatorInstanceRsEntity.getIndicatorInstanceId(), experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId()
          );
        });
  }

  public void populateKIndicatorInstanceIdVExperimentIndicatorInstanceMap(
      Map<String, ExperimentIndicatorInstanceRsEntity> kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap,
      String experimentPersonId,
      Set<String> indicatorInstanceIdSet
  ) {
    if (Objects.isNull(kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap)
        || StringUtils.isBlank(experimentPersonId)
    ) {return;}
    experimentIndicatorInstanceRsService.lambdaQuery()
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
        .in(Objects.nonNull(indicatorInstanceIdSet) && !indicatorInstanceIdSet.isEmpty(), ExperimentIndicatorInstanceRsEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
        .list()
        .forEach(experimentIndicatorInstanceRsEntity -> {
          kIndicatorInstanceIdVExperimentIndicatorInstanceRsEntityMap.put(
              experimentIndicatorInstanceRsEntity.getIndicatorInstanceId(), experimentIndicatorInstanceRsEntity
          );
        });
  }

  public void populateKExperimentIndicatorInstanceIdVExperimentIndicatorValMap(
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      Integer period,
      Set<String> experimentIndicatorInstanceIdSet
  ) {
    if (Objects.isNull(kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap)
        || Objects.isNull(period)
        || Objects.isNull(experimentIndicatorInstanceIdSet) || experimentIndicatorInstanceIdSet.isEmpty()
    ) {return;}
    experimentIndicatorValRsService.lambdaQuery()
        .eq(ExperimentIndicatorValRsEntity::getPeriods, period)
        .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
        .list()
        .forEach(experimentIndicatorValRsEntity -> {
          String experimentIndicatorInstanceId = experimentIndicatorValRsEntity.getIndicatorInstanceId();
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(experimentIndicatorInstanceId, experimentIndicatorValRsEntity);
        });
  }

  public void populateKExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap(
      Map<String, ExperimentIndicatorValRsEntity> kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap,
      Integer periods,
      Set<String> experimentIndicatorInstanceIdSet) {
    if (Objects.isNull(experimentIndicatorInstanceIdSet) || experimentIndicatorInstanceIdSet.isEmpty()
        || Objects.isNull(periods)
    ) {return;}
    experimentIndicatorValRsService.lambdaQuery()
        .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
        .in(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceIdSet)
        .list()
        .forEach(experimentIndicatorValRsEntity -> {
          kExperimentIndicatorInstanceIdVExperimentIndicatorValRsEntityMap.put(
              experimentIndicatorValRsEntity.getIndicatorInstanceId(), experimentIndicatorValRsEntity
          );
        });
  }
}
