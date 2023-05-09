package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorViewMonitorFollowupContentRefResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumRedissonLock;
import org.dows.hep.api.exception.IndicatorInstanceException;
import org.dows.hep.api.exception.IndicatorViewMonitorFollowupContentRefException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.IndicatorViewMonitorFollowupContentRefEntity;
import org.dows.hep.entity.IndicatorViewMonitorFollowupFollowupContentEntity;
import org.dows.hep.service.IndicatorViewMonitorFollowupContentRefService;
import org.dows.hep.service.IndicatorViewMonitorFollowupFollowupContentService;
import org.dows.sequence.api.IdGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorViewMonitorFollowupContentRefBiz {
  @Value("${redisson.lock.lease-time.teacher.indicator-view-monitor-followup-create-delete-update:5000}")
  private Integer leaseTimeIndicatorViewMonitorFollowupCreateDeleteUpdate;
  private final String indicatorViewMonitorFollowupFieldIndicatorViewMonitorFollowupId = "indicatorViewMonitorFollowupId";
  private final RedissonClient redissonClient;
  private final IndicatorViewMonitorFollowupFollowupContentService indicatorViewMonitorFollowupFollowupContentService;
  private final IndicatorViewMonitorFollowupContentRefService indicatorViewMonitorFollowupContentRefService;

  public static IndicatorViewMonitorFollowupContentRefResponseRs indicatorViewMonitorFollowupContentRef2ResponseRs(
      IndicatorViewMonitorFollowupContentRefEntity indicatorViewMonitorFollowupContentRefEntity,
      IndicatorInstanceResponseRs indicatorInstanceResponseRs
      ) {
    return IndicatorViewMonitorFollowupContentRefResponseRs
        .builder()
        .id(indicatorViewMonitorFollowupContentRefEntity.getId())
        .indicatorViewMonitorFollowupContentRefId(indicatorViewMonitorFollowupContentRefEntity.getIndicatorViewMonitorFollowupContentRefId())
        .appId(indicatorViewMonitorFollowupContentRefEntity.getAppId())
        .indicatorViewMonitorFollowupFollowupContentId(indicatorViewMonitorFollowupContentRefEntity.getIndicatorViewMonitorFollowupFollowupContentId())
        .indicatorInstanceResponseRs(indicatorInstanceResponseRs)
        .seq(indicatorViewMonitorFollowupContentRefEntity.getSeq())
        .build();
  }
  @Transactional(rollbackFor = Exception.class)
  public void delete(String indicatorViewMonitorFollowupContentRefId) throws InterruptedException {
    IndicatorViewMonitorFollowupContentRefEntity indicatorViewMonitorFollowupContentRefEntity = indicatorViewMonitorFollowupContentRefService.lambdaQuery()
        .eq(IndicatorViewMonitorFollowupContentRefEntity::getIndicatorViewMonitorFollowupContentRefId, indicatorViewMonitorFollowupContentRefId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method IndicatorViewMonitorFollowupContentRefBiz.delete param indicatorViewMonitorFollowupContentRefId:{} is illegal", indicatorViewMonitorFollowupContentRefId);
          throw new IndicatorViewMonitorFollowupContentRefException(EnumESC.VALIDATE_EXCEPTION);
        });
    String appId = indicatorViewMonitorFollowupContentRefEntity.getAppId();
    String indicatorViewMonitorFollowupFollowupContentId = indicatorViewMonitorFollowupContentRefEntity.getIndicatorViewMonitorFollowupFollowupContentId();
    IndicatorViewMonitorFollowupFollowupContentEntity indicatorViewMonitorFollowupFollowupContentEntity = indicatorViewMonitorFollowupFollowupContentService.lambdaQuery()
        .eq(IndicatorViewMonitorFollowupFollowupContentEntity::getAppId, appId)
        .eq(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupFollowupContentId, indicatorViewMonitorFollowupFollowupContentId)
        .one();
    String indicatorViewMonitorFollowupId = indicatorViewMonitorFollowupFollowupContentEntity.getIndicatorViewMonitorFollowupId();
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_MONITOR_FOLLOWUP_CREATE_DELETE_UPDATE, indicatorViewMonitorFollowupFieldIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId));
    boolean isLocked = lock.tryLock(leaseTimeIndicatorViewMonitorFollowupCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new IndicatorInstanceException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_MONITOR_FOLLOWUP_LATER);
    }
    try {
      boolean isRemoved = indicatorViewMonitorFollowupContentRefService.remove(
          new LambdaQueryWrapper<IndicatorViewMonitorFollowupContentRefEntity>()
              .eq(IndicatorViewMonitorFollowupContentRefEntity::getAppId, appId)
              .eq(IndicatorViewMonitorFollowupContentRefEntity::getIndicatorViewMonitorFollowupContentRefId, indicatorViewMonitorFollowupContentRefId)
      );
      if (!isRemoved) {
        log.warn("method IndicatorViewMonitorFollowupContentRefBiz.delete param indicatorViewMonitorFollowupContentRefId:{} is illegal", indicatorViewMonitorFollowupContentRefId);
        throw new IndicatorViewMonitorFollowupContentRefException(EnumESC.VALIDATE_EXCEPTION);
      }
    } finally {
      lock.unlock();
    }
  }
}
