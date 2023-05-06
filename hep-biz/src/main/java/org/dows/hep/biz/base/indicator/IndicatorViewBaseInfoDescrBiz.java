package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumRedissonLock;
import org.dows.hep.api.exception.IndicatorViewBaseInfoDescrException;
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
public class IndicatorViewBaseInfoDescrBiz {
  @Value("${redisson.lock.lease-time.teacher.indicator-view-base-info-create-delete-update:5000}")
  private Integer leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate;
  private final String indicatorViewBaseInfoFieldIndicatorViewBaseInfoId = "indicatorViewBaseInfoId";
  private final RedissonClient redissonClient;
  private final IndicatorViewBaseInfoDescrService indicatorViewBaseInfoDescrService;
  private final IndicatorViewBaseInfoDescrRefService indicatorViewBaseInfoDescrRefService;

  /**
   * runsix method process
   * 1.delete IndicatorViewBaseInfoDescr by indicatorViewBaseInfoDescId
   * 2.delete IndicatorViewBaseInfoDescrRefEntity by indicatorViewBaseInfoDescId
  */
  @Transactional(rollbackFor = Exception.class)
  public void deleteIndicatorViewBaseInfoDescr(String indicatorViewBaseInfoDescId) throws InterruptedException {
      IndicatorViewBaseInfoDescrEntity indicatorViewBaseInfoDescrEntity = indicatorViewBaseInfoDescrService.lambdaQuery()
          .eq(IndicatorViewBaseInfoDescrEntity::getIndicatorViewBaseInfoDescId, indicatorViewBaseInfoDescId)
          .oneOpt()
          .orElseThrow(() -> {
            log.warn("method deleteIndicatorViewBaseInfoDescr param indicatorViewBaseInfoDescId:{} is illegal", indicatorViewBaseInfoDescId);
            throw new IndicatorViewBaseInfoDescrException(EnumESC.VALIDATE_EXCEPTION);
          });
      String appId = indicatorViewBaseInfoDescrEntity.getAppId();
      String indicatorViewBaseInfoId = indicatorViewBaseInfoDescrEntity.getIndicatorViewBaseInfoId();
      RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_BASE_INFO_CREATE_DELETE_UPDATE, indicatorViewBaseInfoFieldIndicatorViewBaseInfoId, indicatorViewBaseInfoId));
      boolean isLocked = lock.tryLock(leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate, TimeUnit.MILLISECONDS);
      if (!isLocked) {
        throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
      }
      try {
        boolean isRemoved = indicatorViewBaseInfoDescrService.remove(
            new LambdaQueryWrapper<IndicatorViewBaseInfoDescrEntity>()
                .eq(IndicatorViewBaseInfoDescrEntity::getIndicatorViewBaseInfoDescId, indicatorViewBaseInfoDescId)
        );
        if (!isRemoved) {
          log.warn("method deleteIndicatorViewBaseInfoDescr param indicatorViewBaseInfoDescId:{} is illegal", indicatorViewBaseInfoDescId);
          throw new IndicatorViewBaseInfoDescrException(EnumESC.VALIDATE_EXCEPTION);
        }
        indicatorViewBaseInfoDescrRefService.remove(
            new LambdaQueryWrapper<IndicatorViewBaseInfoDescrRefEntity>()
                .eq(IndicatorViewBaseInfoDescrRefEntity::getIndicatorViewBaseInfoDescId, indicatorViewBaseInfoDescId)
        );
      } finally {
        lock.unlock();
      }
    }
}
