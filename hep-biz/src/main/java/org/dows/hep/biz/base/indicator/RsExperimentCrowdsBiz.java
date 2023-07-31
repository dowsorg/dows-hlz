package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.entity.ExperimentCrowdsInstanceRsEntity;
import org.dows.hep.entity.ExperimentRiskModelRsEntity;
import org.dows.hep.service.ExperimentCrowdsInstanceRsService;
import org.dows.hep.service.ExperimentRiskModelRsService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
//@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
public class RsExperimentCrowdsBiz {
  private final ExperimentCrowdsInstanceRsService experimentCrowdsInstanceRsService;
  private final ExperimentRiskModelRsService experimentRiskModelRsService;

  public void populateKExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap(
      Map<String, ExperimentCrowdsInstanceRsEntity> kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap,
      String experimentId
  ) {
    if (Objects.isNull(kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap) || StringUtils.isBlank(experimentId)) {
      return;
    }
    experimentCrowdsInstanceRsService.lambdaQuery()
        .eq(ExperimentCrowdsInstanceRsEntity::getExperimentId, experimentId)
        .list()
        .forEach(experimentCrowdsInstanceRsEntity -> {
          kExperimentCrowdsIdVExperimentCrowdsInstanceRsEntityMap.put(
              experimentCrowdsInstanceRsEntity.getExperimentCrowdsId(), experimentCrowdsInstanceRsEntity
          );
        });
  }

  public void populateKExperimentCrowdsIdVExperimentRiskModelRsEntityListMap(
      Map<String, List<ExperimentRiskModelRsEntity>> kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap,
      Set<String> experimentCrowdsIdSet
  ) {
    if (Objects.isNull(kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap)
        || Objects.isNull(experimentCrowdsIdSet) || experimentCrowdsIdSet.isEmpty()
    ) {
      return;
    }
    experimentRiskModelRsService.lambdaQuery()
        .in(ExperimentRiskModelRsEntity::getCrowdsCategoryId, experimentCrowdsIdSet)
        .list()
        .forEach(experimentRiskModelRsEntity -> {
          String crowdsCategoryId = experimentRiskModelRsEntity.getCrowdsCategoryId();
          List<ExperimentRiskModelRsEntity> experimentRiskModelRsEntityList = kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.get(crowdsCategoryId);
          if (Objects.isNull(experimentRiskModelRsEntityList)) {
            experimentRiskModelRsEntityList = new ArrayList<>();
          }
          experimentRiskModelRsEntityList.add(experimentRiskModelRsEntity);
          kExperimentCrowdsIdVExperimentRiskModelRsEntityListMap.put(crowdsCategoryId, experimentRiskModelRsEntityList);
        });
  }
}
