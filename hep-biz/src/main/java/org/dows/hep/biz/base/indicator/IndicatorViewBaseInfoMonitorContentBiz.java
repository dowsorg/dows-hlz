package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumRedissonLock;
import org.dows.hep.api.exception.IndicatorViewBaseInfoException;
import org.dows.hep.api.exception.IndicatorViewBaseInfoMonitorContentException;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IndicatorViewBaseInfoMonitorContentBiz {
  @Value("${redisson.lock.lease-time.teacher.indicator-view-base-info-create-delete-update:5000}")
  private Integer leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate;
  private final String indicatorViewBaseInfoFieldIndicatorViewBaseInfoId = "indicatorViewBaseInfoId";
  private final RedissonClient redissonClient;
  private final IndicatorViewBaseInfoMonitorService indicatorViewBaseInfoMonitorService;
  private final IndicatorViewBaseInfoMonitorContentService indicatorViewBaseInfoMonitorContentService;
  private final IndicatorViewBaseInfoMonitorContentRefService indicatorViewBaseInfoMonitorContentRefService;

  /**
   * runsix method process
   * 1.delete IndicatorViewBaseInfoMonitorContent by indicatorViewBaseInfoMonitorContentId
   * 2.delete IndicatorViewBaseInfoMonitorContentRef by indicatorViewBaseInfoMonitorContentId
  */
  @Transactional(rollbackFor = Exception.class)
  public void deleteIndicatorViewBaseInfoMonitorContent(String indicatorViewBaseInfoMonitorContentId) throws InterruptedException {
    IndicatorViewBaseInfoMonitorContentEntity indicatorViewBaseInfoMonitorContentEntity = indicatorViewBaseInfoMonitorContentService.lambdaQuery()
        .eq(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method deleteIndicatorViewBaseInfoMonitorContent param indicatorViewBaseInfoMonitorContentId:{} is illegal", indicatorViewBaseInfoMonitorContentId);
          throw new IndicatorViewBaseInfoMonitorContentException(EnumESC.VALIDATE_EXCEPTION);
        });
    String appId = indicatorViewBaseInfoMonitorContentEntity.getAppId();
    String indicatorViewBaseInfoMonitorId = indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorId();
    IndicatorViewBaseInfoMonitorEntity indicatorViewBaseInfoMonitorEntity = indicatorViewBaseInfoMonitorService.lambdaQuery()
        .eq(IndicatorViewBaseInfoMonitorEntity::getAppId, appId)
        .eq(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorId)
        .oneOpt()
        .orElseThrow(() -> {
          log.error("method deleteIndicatorViewBaseInfoMonitorContent param indicatorViewBaseInfoMonitorContentId reference indicatorViewBaseInfoMonitorId:{} is illegal", indicatorViewBaseInfoMonitorId);
          throw new IndicatorViewBaseInfoMonitorContentException(EnumESC.VALIDATE_EXCEPTION);
        });
    String indicatorViewBaseInfoId = indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoId();
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_BASE_INFO_CREATE_DELETE_UPDATE, indicatorViewBaseInfoFieldIndicatorViewBaseInfoId, indicatorViewBaseInfoId));
    boolean isLocked = lock.tryLock(leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
    }
    try {
      boolean isRemoved = indicatorViewBaseInfoMonitorContentService.remove(
          new LambdaQueryWrapper<IndicatorViewBaseInfoMonitorContentEntity>()
              .eq(IndicatorViewBaseInfoMonitorContentEntity::getAppId, appId)
              .eq(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentId)
      );
      if (!isRemoved) {
        log.warn("method deleteIndicatorViewBaseInfoMonitorContent param indicatorViewBaseInfoMonitorContentId:{} is illegal", indicatorViewBaseInfoMonitorContentId);
        throw new IndicatorViewBaseInfoMonitorContentException(EnumESC.VALIDATE_EXCEPTION);
      }
      indicatorViewBaseInfoMonitorContentRefService.remove(
          new LambdaQueryWrapper<IndicatorViewBaseInfoMonitorContentRefEntity>()
              .eq(IndicatorViewBaseInfoMonitorContentRefEntity::getAppId, appId)
              .eq(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentId)
      );
    } finally {
      lock.unlock();
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void batchDeleteIndicatorViewBaseInfoMonitorContent(List<String> indicatorViewBaseInfoMonitorContentIdList) throws InterruptedException {
    if (indicatorViewBaseInfoMonitorContentIdList.isEmpty()) {
      log.warn("method batchDeleteIndicatorViewBaseInfoMonitorContent param indicatorViewBaseInfoMonitorContentIdList is empty");
      throw new IndicatorViewBaseInfoMonitorContentException(EnumESC.VALIDATE_EXCEPTION);
    }
    Set<String> dbIndicatorViewBaseInfoMonitorContentIdSet = indicatorViewBaseInfoMonitorContentService.lambdaQuery()
        .in(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentIdList)
        .list()
        .stream()
        .map(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorContentId)
        .collect(Collectors.toSet());
    if (
        indicatorViewBaseInfoMonitorContentIdList.stream().anyMatch(indicatorViewBaseInfoMonitorContentId -> !dbIndicatorViewBaseInfoMonitorContentIdSet.contains(indicatorViewBaseInfoMonitorContentId))
    ) {
      log.warn("method batchDeleteIndicatorViewBaseInfoMonitorContent param indicatorViewBaseInfoMonitorContentIdList indicatorViewBaseInfoMonitorContentId is illegal");
      throw new IndicatorViewBaseInfoMonitorContentException(EnumESC.VALIDATE_EXCEPTION);
    }
    String indicatorViewBaseInfoMonitorContentId = indicatorViewBaseInfoMonitorContentIdList.get(0);
    String indicatorViewBaseInfoMonitorId = indicatorViewBaseInfoMonitorContentService.lambdaQuery()
        .eq(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentId)
        .one()
        .getIndicatorViewBaseInfoMonitorId();
    IndicatorViewBaseInfoMonitorEntity indicatorViewBaseInfoMonitorEntity = indicatorViewBaseInfoMonitorService.lambdaQuery()
        .eq(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorId)
        .one();
    String appId = indicatorViewBaseInfoMonitorEntity.getAppId();
    String indicatorViewBaseInfoId = indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoId();
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_BASE_INFO_CREATE_DELETE_UPDATE, indicatorViewBaseInfoFieldIndicatorViewBaseInfoId, indicatorViewBaseInfoId));
    boolean isLocked = lock.tryLock(leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
    }
    try {
      boolean isRemoved = indicatorViewBaseInfoMonitorContentService.remove(
          new LambdaQueryWrapper<IndicatorViewBaseInfoMonitorContentEntity>()
              .eq(IndicatorViewBaseInfoMonitorContentEntity::getAppId, appId)
              .in(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorContentId, dbIndicatorViewBaseInfoMonitorContentIdSet)
      );
      if (!isRemoved) {
        log.warn("method batchDeleteIndicatorViewBaseInfoMonitorContent param indicatorViewBaseInfoMonitorContentIdList:{} is illegal", indicatorViewBaseInfoMonitorContentIdList);
        throw new IndicatorViewBaseInfoMonitorContentException(EnumESC.VALIDATE_EXCEPTION);
      }
      indicatorViewBaseInfoMonitorContentRefService.remove(
          new LambdaQueryWrapper<IndicatorViewBaseInfoMonitorContentRefEntity>()
              .eq(IndicatorViewBaseInfoMonitorContentRefEntity::getAppId, appId)
              .in(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorViewBaseInfoMonitorContentId, dbIndicatorViewBaseInfoMonitorContentIdSet)
      );
    } finally {
      lock.unlock();
    }
  }
}
