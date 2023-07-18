package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.enums.EnumStatus;
import org.dows.hep.entity.CrowdsInstanceEntity;
import org.dows.hep.entity.ExperimentRiskModelRsEntity;
import org.dows.hep.entity.RiskModelEntity;
import org.dows.hep.service.CrowdsInstanceService;
import org.dows.hep.service.RiskModelService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RsCrowdsBiz {
  private final CrowdsInstanceService crowdsInstanceService;
  private final RiskModelService riskModelService;
  public void populateEnableKCrowdsIdVCrowdsInstanceEntityMap(Map<String, CrowdsInstanceEntity> kCrowdsIdVCrowdsInstanceEntityMap, String appId) {
    if (Objects.isNull(kCrowdsIdVCrowdsInstanceEntityMap)
        || StringUtils.isBlank(appId)
    ) {return;}
    crowdsInstanceService.lambdaQuery()
        .eq(CrowdsInstanceEntity::getAppId, appId)
        .list()
        .forEach(crowdsInstanceEntity -> {
          kCrowdsIdVCrowdsInstanceEntityMap.put(crowdsInstanceEntity.getCrowdsId(), crowdsInstanceEntity);
        });
  }

  public void populateEnableKCrowdsIdVRiskModelEntityListMap(
      Map<String, List<RiskModelEntity>> kCrowdsIdVRiskModelEntityListMap,
      Set<String> crowdsIdSet
  ) {
    if (Objects.isNull(kCrowdsIdVRiskModelEntityListMap)
        || Objects.isNull(crowdsIdSet) || crowdsIdSet.isEmpty()
    ) {return;}
    riskModelService.lambdaQuery()
        .eq(RiskModelEntity::getStatus, EnumStatus.ENABLE.getCode())
        .in(RiskModelEntity::getCrowdsCategoryId, crowdsIdSet)
        .list()
        .forEach(riskModelEntity -> {
          String crowdsCategoryId = riskModelEntity.getCrowdsCategoryId();
          List<RiskModelEntity> riskModelEntityList = kCrowdsIdVRiskModelEntityListMap.get(crowdsCategoryId);
          if (Objects.isNull(riskModelEntityList)) {
            riskModelEntityList = new ArrayList<>();
          }
          riskModelEntityList.add(riskModelEntity);
          kCrowdsIdVRiskModelEntityListMap.put(crowdsCategoryId, riskModelEntityList);
        });
  }
}
