package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumRedissonLock;
import org.dows.hep.api.exception.IndicatorViewBaseInfoDescrRefException;
import org.dows.hep.api.exception.IndicatorViewBaseInfoException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.IndicatorViewBaseInfoDescrEntity;
import org.dows.hep.entity.IndicatorViewBaseInfoDescrRefEntity;
import org.dows.hep.service.IndicatorViewBaseInfoDescrRefService;
import org.dows.hep.service.IndicatorViewBaseInfoDescrService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * @author runsix
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IndicatorViewBaseInfoDescrRefBiz {
  @Value("${redisson.lock.lease-time.teacher.indicator-view-base-info-create-delete-update:5000}")
  private Integer leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate;
  private final String indicatorViewBaseInfoFieldIndicatorViewBaseInfoId = "indicatorViewBaseInfoId";
  private final RedissonClient redissonClient;
  private final IndicatorViewBaseInfoDescrRefService indicatorViewBaseInfoDescrRefService;
  private final IndicatorViewBaseInfoDescrService indicatorViewBaseInfoDescrService;

  /**
   * runsix method process
   * 1.delete IndicatorViewBaseInfoDescrRef by indicatorViewBaseInfoDescRefId
  */
  @Transactional(rollbackFor = Exception.class)
  public void deleteIndicatorViewBaseInfoDescrRef(String indicatorViewBaseInfoDescRefId) throws InterruptedException {
    IndicatorViewBaseInfoDescrRefEntity indicatorViewBaseInfoDescrRefEntity = indicatorViewBaseInfoDescrRefService.lambdaQuery()
        .eq(IndicatorViewBaseInfoDescrRefEntity::getIndicatorViewBaseInfoDescRefId, indicatorViewBaseInfoDescRefId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method deleteIndicatorViewBaseInfoDescrRef param indicatorViewBaseInfoDescRefId:{}", indicatorViewBaseInfoDescRefId);
          throw new IndicatorViewBaseInfoDescrRefException(EnumESC.VALIDATE_EXCEPTION);
        });
    String appId = indicatorViewBaseInfoDescrRefEntity.getAppId();
    String indicatorViewBaseInfoDescId = indicatorViewBaseInfoDescrRefEntity.getIndicatorViewBaseInfoDescId();
    IndicatorViewBaseInfoDescrEntity indicatorViewBaseInfoDescrEntity = indicatorViewBaseInfoDescrService.lambdaQuery()
        .eq(IndicatorViewBaseInfoDescrEntity::getAppId, appId)
        .eq(IndicatorViewBaseInfoDescrEntity::getIndicatorViewBaseInfoDescId, indicatorViewBaseInfoDescId)
        .oneOpt()
        .orElseThrow(() -> {
          log.error("method deleteIndicatorViewBaseInfoDescrRef param indicatorViewBaseInfoDescRefId reference indicatorViewBaseInfoDescId:{} is illegal", indicatorViewBaseInfoDescId);
          throw new IndicatorViewBaseInfoDescrRefException(EnumESC.VALIDATE_EXCEPTION);
        });
    String indicatorViewBaseInfoId = indicatorViewBaseInfoDescrEntity.getIndicatorViewBaseInfoId();
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_BASE_INFO_CREATE_DELETE_UPDATE, indicatorViewBaseInfoFieldIndicatorViewBaseInfoId, indicatorViewBaseInfoId));
    boolean isLocked = lock.tryLock(leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
    }
    try {
      boolean isRemoved = indicatorViewBaseInfoDescrRefService.remove(
          new LambdaQueryWrapper<IndicatorViewBaseInfoDescrRefEntity>()
              .eq(IndicatorViewBaseInfoDescrRefEntity::getAppId, appId)
              .eq(IndicatorViewBaseInfoDescrRefEntity::getIndicatorViewBaseInfoDescRefId, indicatorViewBaseInfoDescRefId)
      );
      if (!isRemoved) {
        log.warn("method deleteIndicatorViewBaseInfoDescrRef param indicatorViewBaseInfoDescRefId:{}", indicatorViewBaseInfoDescRefId);
        throw new IndicatorViewBaseInfoDescrRefException(EnumESC.VALIDATE_EXCEPTION);
      }
    } finally {
      lock.unlock();
    }
  }
}
