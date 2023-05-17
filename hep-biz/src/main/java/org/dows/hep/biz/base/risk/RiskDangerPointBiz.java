package org.dows.hep.biz.base.risk;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceResponseRs;
import org.dows.hep.api.base.risk.response.RiskDangerPointResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.exception.RiskDangerPointException;
import org.dows.hep.entity.RiskDangerPointEntity;
import org.dows.hep.service.RiskDangerPointService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskDangerPointBiz {
  private final RiskDangerPointService riskDangerPointService;
  public static RiskDangerPointResponseRs riskDangerPoint2ResponseRs(
      RiskDangerPointEntity riskDangerPointEntity,
      IndicatorInstanceResponseRs indicatorInstanceResponseRs
      ) {
    if (Objects.isNull(riskDangerPointEntity)) {
      return null;
    }
    return RiskDangerPointResponseRs
        .builder()
        .id(riskDangerPointEntity.getId())
        .riskDangerPointId(riskDangerPointEntity.getRiskDangerPointId())
        .appId(riskDangerPointEntity.getAppId())
        .riskDeathModelId(riskDangerPointEntity.getRiskDeathModelId())
        .indicatorInstanceResponseRs(indicatorInstanceResponseRs)
        .expression(riskDangerPointEntity.getExpression())
        .dt(riskDangerPointEntity.getDt())
        .build();
  }


  @Transactional(rollbackFor = Exception.class)
  public void deleteRs(String riskDangerPointId) {
    if (StringUtils.isBlank(riskDangerPointId)) {
      log.warn("method RiskDangerPointBiz.deleteRs param riskDangerPointId is blank");
      throw new RiskDangerPointException(EnumESC.VALIDATE_EXCEPTION);
    }
    boolean isRemove = riskDangerPointService.remove(
        new LambdaQueryWrapper<RiskDangerPointEntity>()
            .eq(RiskDangerPointEntity::getRiskDangerPointId, riskDangerPointId)
    );
    if (!isRemove) {
      log.warn("method RiskDangerPointBiz.deleteRs param riskDangerPointId:{} is illegal", riskDangerPointId);
      throw new RiskDangerPointException(EnumESC.VALIDATE_EXCEPTION);
    }
  }
}
