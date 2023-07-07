package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.biz.user.experiment.ExperimentTimerBiz;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.ExperimentIndicatorValRsEntity;
import org.dows.hep.service.ExperimentIndicatorInstanceRsService;
import org.dows.hep.service.ExperimentIndicatorValRsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
  private final ExperimentTimerBiz experimentTimerBiz;
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

  @Transactional(rollbackFor = Exception.class)
  public void changeMoney(RsChangeMoneyRequest rsChangeMoneyRequest) {
    String appId = rsChangeMoneyRequest.getAppId();
    String experimentId = rsChangeMoneyRequest.getExperimentId();
    String experimentPersonId = rsChangeMoneyRequest.getExperimentPersonId();
    Integer periods = rsChangeMoneyRequest.getPeriods();
    Double moneyChange = rsChangeMoneyRequest.getMoneyChange();
    if (Objects.isNull(periods)) {
      ExperimentPeriodsResonse experimentPeriods = experimentTimerBiz.getExperimentPeriods(appId, experimentId);
      if (Objects.nonNull(experimentPeriods) && Objects.nonNull(experimentPeriods.getCurrentPeriod())) {
        periods = experimentPeriods.getCurrentPeriod();
      } else {
        periods = 1;
      }
    }
    ExperimentIndicatorInstanceRsEntity experimentIndicatorInstanceRsEntity = experimentIndicatorInstanceRsService.lambdaQuery()
        .eq(ExperimentIndicatorInstanceRsEntity::getExperimentPersonId, experimentPersonId)
        .eq(ExperimentIndicatorInstanceRsEntity::getType, EnumIndicatorType.MONEY.getType())
        .one();
    String experimentIndicatorInstanceId = experimentIndicatorInstanceRsEntity.getExperimentIndicatorInstanceId();
    ExperimentIndicatorValRsEntity experimentIndicatorValRsEntity = experimentIndicatorValRsService.lambdaQuery()
        .eq(ExperimentIndicatorValRsEntity::getIndicatorInstanceId, experimentIndicatorInstanceId)
        .eq(ExperimentIndicatorValRsEntity::getPeriods, periods)
        .one();
    String min = experimentIndicatorValRsEntity.getMin();
    String max = experimentIndicatorValRsEntity.getMax();
    String moneyCurrentVal = experimentIndicatorValRsEntity.getCurrentVal();
    BigDecimal newMoneyCurrentVal = BigDecimal.valueOf(Double.parseDouble(moneyCurrentVal)).add(BigDecimal.valueOf(moneyChange));
    if (newMoneyCurrentVal.compareTo(BigDecimal.valueOf(Double.parseDouble(min))) < 0) {
      newMoneyCurrentVal = BigDecimal.valueOf(Double.parseDouble(min));
    } else if (newMoneyCurrentVal.compareTo(BigDecimal.valueOf(Double.parseDouble(max))) > 0) {
      newMoneyCurrentVal = BigDecimal.valueOf(Double.parseDouble(max));
    }
    experimentIndicatorValRsEntity.setCurrentVal(newMoneyCurrentVal.setScale(2, RoundingMode.DOWN).toString());
    experimentIndicatorValRsService.saveOrUpdate(experimentIndicatorValRsEntity);
  }
}
