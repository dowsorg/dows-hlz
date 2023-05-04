package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.enums.EnumESC;
import org.dows.hep.biz.enums.EnumRedissonLock;
import org.dows.hep.biz.exception.IndicatorViewBaseInfoException;
import org.dows.hep.biz.exception.IndicatorViewBaseInfoMonitorException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.IndicatorViewBaseInfoMonitorContentEntity;
import org.dows.hep.entity.IndicatorViewBaseInfoMonitorContentRefEntity;
import org.dows.hep.entity.IndicatorViewBaseInfoMonitorEntity;
import org.dows.hep.service.IndicatorViewBaseInfoMonitorContentRefService;
import org.dows.hep.service.IndicatorViewBaseInfoMonitorContentService;
import org.dows.hep.service.IndicatorViewBaseInfoMonitorService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IndicatorViewBaseInfoMonitorBiz {
  @Value("${redisson.lock.lease-time.teacher.indicator-view-base-info-create-delete-update:5000}")
  private Integer leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate;
  private final String indicatorViewBaseInfoFieldIndicatorViewBaseInfoId = "indicatorViewBaseInfoId";
  private final RedissonClient redissonClient;
  private final IndicatorViewBaseInfoMonitorService indicatorViewBaseInfoMonitorService;
  private final IndicatorViewBaseInfoMonitorContentService indicatorViewBaseInfoMonitorContentService;
  private final IndicatorViewBaseInfoMonitorContentRefService indicatorViewBaseInfoMonitorContentRefService;

  /**
   * runsix method process
   * 1.delete IndicatorViewBaseInfoMonitor by indicatorViewBaseInfoMonitorId
   * 2.delete IndicatorViewBaseInfoMonitorContent by indicatorViewBaseInfoMonitorId
   * 3.delete IndicatorViewBaseInfoMonitorContentRef by indicatorViewBaseInfoMonitorContentId
  */
  @Transactional(rollbackFor = Exception.class)
  public void deleteIndicatorViewBaseInfoMonitor(String indicatorViewBaseInfoMonitorId) throws InterruptedException {
    IndicatorViewBaseInfoMonitorEntity indicatorViewBaseInfoMonitorEntity = indicatorViewBaseInfoMonitorService.lambdaQuery()
        .eq(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method deleteIndicatorViewBaseInfoMonitor param indicatorViewBaseInfoMonitorId:{} is illegal", indicatorViewBaseInfoMonitorId);
          throw new IndicatorViewBaseInfoMonitorException(EnumESC.VALIDATE_EXCEPTION);
        });
    String appId = indicatorViewBaseInfoMonitorEntity.getAppId();
    String indicatorViewBaseInfoId = indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoId();
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_BASE_INFO_CREATE_DELETE_UPDATE, indicatorViewBaseInfoFieldIndicatorViewBaseInfoId, indicatorViewBaseInfoId));
    boolean isLocked = lock.tryLock(leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
    }
    try {
      boolean isRemoved = indicatorViewBaseInfoMonitorService.remove(
          new LambdaQueryWrapper<IndicatorViewBaseInfoMonitorEntity>()
              .eq(IndicatorViewBaseInfoMonitorEntity::getAppId, appId)
              .eq(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorId)
      );
      if (!isRemoved) {
        log.warn("method deleteIndicatorViewBaseInfoMonitor param indicatorViewBaseInfoMonitorId:{} is illegal", indicatorViewBaseInfoMonitorId);
        throw new IndicatorViewBaseInfoMonitorException(EnumESC.VALIDATE_EXCEPTION);
      }
      List<String> indicatorViewBaseInfoMonitorContentIdList = indicatorViewBaseInfoMonitorContentService.lambdaQuery()
          .eq(IndicatorViewBaseInfoMonitorContentEntity::getAppId, appId)
          .eq(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorId)
          .list()
          .stream()
          .map(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorContentId)
          .collect(Collectors.toList());
      indicatorViewBaseInfoMonitorContentService.remove(
          new LambdaQueryWrapper<IndicatorViewBaseInfoMonitorContentEntity>()
              .eq(IndicatorViewBaseInfoMonitorContentEntity::getAppId, appId)
              .eq(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorId)
      );
      if (!indicatorViewBaseInfoMonitorContentIdList.isEmpty()) {
        indicatorViewBaseInfoMonitorContentRefService.remove(
            new LambdaQueryWrapper<IndicatorViewBaseInfoMonitorContentRefEntity>()
                .eq(IndicatorViewBaseInfoMonitorContentRefEntity::getAppId, appId)
                .in(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorViewBaseInfoMonitorContentRefId, indicatorViewBaseInfoMonitorContentIdList)
        );
      }
    } finally {
      lock.unlock();
    }
  }
}
