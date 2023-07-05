package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;
import org.dows.hep.service.ExperimentIndicatorInstanceRsService;
import org.dows.hep.service.ExperimentIndicatorValRsService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentIndicatorInstanceRsBiz {
  private final ExperimentIndicatorInstanceRsService experimentIndicatorInstanceRsService;
  private final ExperimentIndicatorValRsService experimentIndicatorValRsService;
  public String getHealthPoint(Integer periods, String experimentPersonId) {
    String healthPoint = "1";
    ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = experimentIndicatorInstanceRsService.lambdaQuery()
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
        .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.HEALTH_POINT.getType())
        .one();
    String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
    ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = experimentIndicatorValRsService.lambdaQuery()
        .eq(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceId)
        .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
        .one();
    if (Objects.nonNull(experimentIndicatorValRsEntity)) {
      healthPoint = experimentIndicatorValRsEntity.getCurrentVal();
    }
    return healthPoint;
  }
}
