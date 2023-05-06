package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumRedissonLock;
import org.dows.hep.api.exception.IndicatorViewBaseInfoException;
import org.dows.hep.api.exception.IndicatorViewBaseInfoSingleException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.IndicatorViewBaseInfoSingleEntity;
import org.dows.hep.service.IndicatorViewBaseInfoSingleService;
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
@RequiredArgsConstructor
@Slf4j
public class IndicatorViewBaseInfoSingleBiz {
  @Value("${redisson.lock.lease-time.teacher.indicator-view-base-info-create-delete-update:5000}")
  private Integer leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate;
  private final String indicatorViewBaseInfoFieldIndicatorViewBaseInfoId = "indicatorViewBaseInfoId";
  private final RedissonClient redissonClient;
  private final IndicatorViewBaseInfoSingleService indicatorViewBaseInfoSingleService;
  @Transactional(rollbackFor = Exception.class)
  public void deleteIndicatorViewBaseInfoSingle(String indicatorViewBaseInfoSingleId) throws InterruptedException {
    IndicatorViewBaseInfoSingleEntity indicatorViewBaseInfoSingleEntity = indicatorViewBaseInfoSingleService.lambdaQuery()
        .eq(IndicatorViewBaseInfoSingleEntity::getIndicatorViewBaseInfoSingleId, indicatorViewBaseInfoSingleId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method deleteIndicatorViewBaseInfoMonitorContentRef param indicatorViewBaseInfoSingleId:{} is illegal", indicatorViewBaseInfoSingleId);
          throw new IndicatorViewBaseInfoSingleException(EnumESC.VALIDATE_EXCEPTION);
        });
    String appId = indicatorViewBaseInfoSingleEntity.getAppId();
    String indicatorViewBaseInfoId = indicatorViewBaseInfoSingleEntity.getIndicatorViewBaseInfoId();
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_BASE_INFO_CREATE_DELETE_UPDATE, indicatorViewBaseInfoFieldIndicatorViewBaseInfoId, indicatorViewBaseInfoId));
    boolean isLocked = lock.tryLock(leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
    }
    try {
      boolean isRemoved = indicatorViewBaseInfoSingleService.remove(
          new LambdaQueryWrapper<IndicatorViewBaseInfoSingleEntity>()
              .eq(IndicatorViewBaseInfoSingleEntity::getIndicatorViewBaseInfoSingleId, indicatorViewBaseInfoSingleId)
      );
      if (!isRemoved) {
        log.warn("method deleteIndicatorViewBaseInfoMonitorContentRef param indicatorViewBaseInfoSingleId:{} is illegal", indicatorViewBaseInfoSingleId);
        throw new IndicatorViewBaseInfoSingleException(EnumESC.VALIDATE_EXCEPTION);
      }
    } finally {
      lock.unlock();
    }
  }
}
