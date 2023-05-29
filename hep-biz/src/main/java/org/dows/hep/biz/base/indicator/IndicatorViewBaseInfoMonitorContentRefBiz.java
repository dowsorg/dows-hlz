package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumRedissonLock;
import org.dows.hep.api.exception.IndicatorViewBaseInfoDescrRefException;
import org.dows.hep.api.exception.IndicatorViewBaseInfoException;
import org.dows.hep.api.exception.IndicatorViewBaseInfoMonitorContentRefException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author runsix
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IndicatorViewBaseInfoMonitorContentRefBiz {
  @Value("${redisson.lock.lease-time.teacher.indicator-view-base-info-create-delete-update:5000}")
  private Integer leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate;
  private final String indicatorViewBaseInfoFieldIndicatorViewBaseInfoId = "indicatorViewBaseInfoId";
  private final RedissonClient redissonClient;
  private final IndicatorViewBaseInfoMonitorService indicatorViewBaseInfoMonitorService;
  private final IndicatorViewBaseInfoMonitorContentService indicatorViewBaseInfoMonitorContentService;
  private final IndicatorViewBaseInfoMonitorContentRefService indicatorViewBaseInfoMonitorContentRefService;

  /**
   * runsix method process
   * 1.delete IndicatorViewBaseInfoMonitorContentRef by indicatorViewBaseInfoMonitorContentRefId
  */
  @Transactional(rollbackFor = Exception.class)
  public void deleteIndicatorViewBaseInfoMonitorContentRef(String indicatorViewBaseInfoMonitorContentRefId) throws InterruptedException {
    IndicatorViewBaseInfoMonitorContentRefEntity indicatorViewBaseInfoMonitorContentRefEntity = indicatorViewBaseInfoMonitorContentRefService.lambdaQuery()
        .eq(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorViewBaseInfoMonitorContentRefId, indicatorViewBaseInfoMonitorContentRefId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method deleteIndicatorViewBaseInfoMonitorContentRef param indicatorViewBaseInfoMonitorContentRefId:{}", indicatorViewBaseInfoMonitorContentRefId);
          throw new IndicatorViewBaseInfoMonitorContentRefException(EnumESC.VALIDATE_EXCEPTION);
        });
    String appId = indicatorViewBaseInfoMonitorContentRefEntity.getAppId();
    String indicatorViewBaseInfoMonitorContentId = indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorViewBaseInfoMonitorContentId();
    IndicatorViewBaseInfoMonitorContentEntity indicatorViewBaseInfoMonitorContentEntity = indicatorViewBaseInfoMonitorContentService.lambdaQuery()
        .eq(IndicatorViewBaseInfoMonitorContentEntity::getAppId, appId)
        .eq(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentId)
        .oneOpt()
        .orElseThrow(() -> {
          log.error("method deleteIndicatorViewBaseInfoMonitorContentRef param indicatorViewBaseInfoMonitorContentRefId reference indicatorViewBaseInfoMonitorContentId:{} is illegal", indicatorViewBaseInfoMonitorContentId);
          throw new IndicatorViewBaseInfoMonitorContentRefException(EnumESC.VALIDATE_EXCEPTION);
        });
    String indicatorViewBaseInfoMonitorId = indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorId();
    IndicatorViewBaseInfoMonitorEntity indicatorViewBaseInfoMonitorEntity = indicatorViewBaseInfoMonitorService.lambdaQuery()
        .eq(IndicatorViewBaseInfoMonitorEntity::getAppId, appId)
        .eq(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorId)
        .oneOpt()
        .orElseThrow(() -> {
          log.error("method deleteIndicatorViewBaseInfoMonitorContentRef param indicatorViewBaseInfoMonitorContentRefId reference indicatorViewBaseInfoMonitorId:{} is illegal", indicatorViewBaseInfoMonitorId);
          throw new IndicatorViewBaseInfoMonitorContentRefException(EnumESC.VALIDATE_EXCEPTION);
        });
    String indicatorViewBaseInfoId = indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoId();
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_BASE_INFO_CREATE_DELETE_UPDATE, indicatorViewBaseInfoFieldIndicatorViewBaseInfoId, indicatorViewBaseInfoId));
    boolean isLocked = lock.tryLock(leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
    }
    try {
      boolean isRemoved = indicatorViewBaseInfoMonitorContentRefService.remove(
          new LambdaQueryWrapper<IndicatorViewBaseInfoMonitorContentRefEntity>()
              .eq(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorViewBaseInfoMonitorContentRefId, indicatorViewBaseInfoMonitorContentRefId)
      );
      if (!isRemoved) {
        log.warn("method deleteIndicatorViewBaseInfoMonitorContentRef param indicatorViewBaseInfoMonitorContentRefId:{}", indicatorViewBaseInfoMonitorContentRefId);
        throw new IndicatorViewBaseInfoMonitorContentRefException(EnumESC.VALIDATE_EXCEPTION);
      }
    } finally {
      lock.unlock();
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void batchDelete(List<String> indicatorViewBaseInfoMonitorContentRefIdList) throws InterruptedException {
    if (Objects.isNull(indicatorViewBaseInfoMonitorContentRefIdList) || indicatorViewBaseInfoMonitorContentRefIdList.isEmpty()) {
      log.warn("method batchDelete param indicatorViewBaseInfoMonitorContentRefIdList is empty");
      throw new IndicatorViewBaseInfoDescrRefException(EnumESC.VALIDATE_EXCEPTION);
    }
    List<String> dbIndicatorViewBaseInfoMonitorContentRefIdList = new ArrayList<>();
    StringBuffer indicatorViewBaseInfoMonitorContentIdBuffer = new StringBuffer();
    StringBuffer appIdBuffer = new StringBuffer();
    indicatorViewBaseInfoMonitorContentRefService.lambdaQuery()
        .in(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorViewBaseInfoMonitorContentRefId, indicatorViewBaseInfoMonitorContentRefIdList)
        .list()
        .forEach(indicatorViewBaseInfoMonitorContentRefEntity -> {
          dbIndicatorViewBaseInfoMonitorContentRefIdList.add(indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorViewBaseInfoMonitorContentRefId());
          if (indicatorViewBaseInfoMonitorContentIdBuffer.isEmpty()) {
            indicatorViewBaseInfoMonitorContentIdBuffer.append(indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorViewBaseInfoMonitorContentId());
          }
          if (appIdBuffer.isEmpty()) {
            appIdBuffer.append(indicatorViewBaseInfoMonitorContentRefEntity.getAppId());
          }
        });
    String indicatorViewBaseInfoMonitorContentId = indicatorViewBaseInfoMonitorContentIdBuffer.toString();
    String appId = appIdBuffer.toString();
    if (
        indicatorViewBaseInfoMonitorContentRefIdList.stream().anyMatch(indicatorViewBaseInfoMonitorContentRefId -> !dbIndicatorViewBaseInfoMonitorContentRefIdList.contains(indicatorViewBaseInfoMonitorContentRefId))
    ) {
      log.warn("method batchDelete param indicatorViewBaseInfoMonitorContentRefIdList:{} is illegal", indicatorViewBaseInfoMonitorContentRefIdList);
      throw new IndicatorViewBaseInfoDescrRefException(EnumESC.VALIDATE_EXCEPTION);
    }
    IndicatorViewBaseInfoMonitorContentEntity indicatorViewBaseInfoMonitorContentEntity = indicatorViewBaseInfoMonitorContentService.lambdaQuery()
        .eq(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method batchDelete param indicatorViewBaseInfoMonitorContentRefIdList indicatorViewBaseInfoMonitorContentId:{} is illegal", indicatorViewBaseInfoMonitorContentId);
          throw new IndicatorViewBaseInfoDescrRefException(EnumESC.VALIDATE_EXCEPTION);
        });
    String indicatorViewBaseInfoMonitorId = indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorId();
    IndicatorViewBaseInfoMonitorEntity indicatorViewBaseInfoMonitorEntity = indicatorViewBaseInfoMonitorService.lambdaQuery()
        .eq(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method batchDelete param indicatorViewBaseInfoMonitorContentRefIdList indicatorViewBaseInfoMonitorId:{} is illegal", indicatorViewBaseInfoMonitorId);
          throw new IndicatorViewBaseInfoDescrRefException(EnumESC.VALIDATE_EXCEPTION);
        });
    String indicatorViewBaseInfoId = indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoId();
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_BASE_INFO_CREATE_DELETE_UPDATE, indicatorViewBaseInfoFieldIndicatorViewBaseInfoId, indicatorViewBaseInfoId));
    boolean isLocked = lock.tryLock(leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
    }
    try {
      boolean isRemoved = indicatorViewBaseInfoMonitorContentRefService.remove(
          new LambdaQueryWrapper<IndicatorViewBaseInfoMonitorContentRefEntity>()
              .in(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorViewBaseInfoMonitorContentRefId, indicatorViewBaseInfoMonitorContentRefIdList)
      );
      if (!isRemoved) {
        log.warn("method batchDelete param indicatorViewBaseInfoMonitorContentRefIdList:{} is illegal", indicatorViewBaseInfoMonitorContentRefIdList);
        throw new IndicatorViewBaseInfoMonitorContentRefException(EnumESC.VALIDATE_EXCEPTION);
      }
    } finally {
      lock.unlock();
    }
  }
}
