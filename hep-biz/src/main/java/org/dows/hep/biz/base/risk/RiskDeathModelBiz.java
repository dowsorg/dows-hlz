package org.dows.hep.biz.base.risk;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.dows.hep.api.base.risk.response.RiskDangerPointResponseRs;
import org.dows.hep.api.base.risk.response.RiskDeathModelResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.RiskDeathModelException;
import org.dows.hep.entity.RiskDangerPointEntity;
import org.dows.hep.entity.RiskDeathModelEntity;
import org.dows.hep.entity.RiskModelEntity;
import org.dows.hep.service.RiskDangerPointService;
import org.dows.hep.service.RiskDeathModelService;
import org.dows.hep.service.RiskModelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskDeathModelBiz {
  private final RiskModelService riskModelService;
  private final RiskDeathModelService riskDeathModelService;
  private final RiskDangerPointService riskDangerPointService;
  public static RiskDeathModelResponseRs riskDeathModel2ResponseRs(
      RiskDeathModelEntity riskDeathModelEntity,
      List<RiskDangerPointResponseRs> riskDangerPointResponseRsList
      ) {
    if (Objects.isNull(riskDeathModelEntity)) {
      return null;
    }
    return RiskDeathModelResponseRs
        .builder()
        .id(riskDeathModelEntity.getId())
        .riskDeathModelId(riskDeathModelEntity.getRiskDeathModelId())
        .appId(riskDeathModelEntity.getAppId())
        .riskModelId(riskDeathModelEntity.getRiskModelId())
        .riskDeathReasonName(riskDeathModelEntity.getRiskDeathReasonName())
        .riskDeathProbability(riskDeathModelEntity.getRiskDeathProbability())
        .dt(riskDeathModelEntity.getDt())
        .riskDangerPointResponseRsList(riskDangerPointResponseRsList)
        .build();
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteRs(String riskDeathModelId) {
    if (StringUtils.isBlank(riskDeathModelId)) {
      log.warn("method RiskDeathModelBiz.deleteRs param riskDeathModelId is empty");
      throw new RiskDeathModelException(EnumESC.VALIDATE_EXCEPTION);
    }
    RiskDeathModelEntity riskDeathModelEntity = riskDeathModelService.lambdaQuery()
        .eq(RiskDeathModelEntity::getRiskDeathModelId, riskDeathModelId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method RiskDeathModelBiz.deleteRs param riskDeathModelId:{} is illegal", riskDeathModelId);
          throw new RiskDeathModelException(EnumESC.VALIDATE_EXCEPTION);
        });
    String riskModelId = riskDeathModelEntity.getRiskModelId();
    boolean isRemove = riskDeathModelService.remove(
        new LambdaQueryWrapper<RiskDeathModelEntity>()
            .eq(RiskDeathModelEntity::getRiskDeathModelId, riskDeathModelId)
    );
    if (!isRemove) {
      log.warn("method RiskDeathModelBiz.deleteRs param riskDeathModelId:{} is illegal", riskDeathModelId);
      throw new RiskDeathModelException(EnumESC.VALIDATE_EXCEPTION);
    }
    riskDangerPointService.remove(
        new LambdaQueryWrapper<RiskDangerPointEntity>()
            .eq(RiskDangerPointEntity::getRiskDeathModelId, riskDeathModelId)
    );
    RiskModelEntity riskModelEntity = riskModelService.lambdaQuery()
        .eq(RiskModelEntity::getRiskModelId, riskModelId)
        .one();
    if (Objects.nonNull(riskModelEntity)) {
      int riskModelRiskDeathProbability = riskDeathModelService.lambdaQuery()
          .eq(RiskDeathModelEntity::getRiskModelId, riskModelId)
          .list()
          .stream()
          .mapToInt(RiskDeathModelEntity::getRiskDeathProbability)
          .sum();
      riskModelEntity.setRiskDeathProbability(riskModelRiskDeathProbability);
      riskModelService.updateById(riskModelEntity);
    }
  }
}
