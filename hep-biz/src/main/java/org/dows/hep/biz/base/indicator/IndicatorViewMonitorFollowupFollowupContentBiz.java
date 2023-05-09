package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.response.IndicatorViewMonitorFollowupContentRefResponseRs;
import org.dows.hep.api.base.indicator.response.IndicatorViewMonitorFollowupFollowupContentResponseRs;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumRedissonLock;
import org.dows.hep.api.exception.IndicatorInstanceException;
import org.dows.hep.api.exception.IndicatorViewMonitorFollowupFollowupContentException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.IndicatorViewMonitorFollowupContentRefEntity;
import org.dows.hep.entity.IndicatorViewMonitorFollowupFollowupContentEntity;
import org.dows.hep.service.IndicatorViewMonitorFollowupContentRefService;
import org.dows.hep.service.IndicatorViewMonitorFollowupFollowupContentService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorViewMonitorFollowupFollowupContentBiz {
  @Value("${redisson.lock.lease-time.teacher.indicator-view-monitor-followup-create-delete-update:5000}")
  private Integer leaseTimeIndicatorViewMonitorFollowupCreateDeleteUpdate;
  private final String indicatorViewMonitorFollowupFieldIndicatorViewMonitorFollowupId = "indicatorViewMonitorFollowupId";
  private final RedissonClient redissonClient;
  private final IndicatorViewMonitorFollowupFollowupContentService indicatorViewMonitorFollowupFollowupContentService;
  private final IndicatorViewMonitorFollowupContentRefService indicatorViewMonitorFollowupContentRefService;

  @Transactional(rollbackFor = Exception.class)
  public void delete(String indicatorViewMonitorFollowupFollowupContentId) throws InterruptedException {
    IndicatorViewMonitorFollowupFollowupContentEntity indicatorViewMonitorFollowupFollowupContentEntity = indicatorViewMonitorFollowupFollowupContentService.lambdaQuery()
        .eq(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupFollowupContentId, indicatorViewMonitorFollowupFollowupContentId)
        .oneOpt()
        .orElseThrow(() -> {
          log.warn("method IndicatorViewMonitorFollowupFollowupContentBiz.delete param indicatorViewMonitorFollowupFollowupContentId:{} is illegal", indicatorViewMonitorFollowupFieldIndicatorViewMonitorFollowupId);
          throw new IndicatorViewMonitorFollowupFollowupContentException(EnumESC.VALIDATE_EXCEPTION);
        });
    String appId = indicatorViewMonitorFollowupFollowupContentEntity.getAppId();
    String indicatorViewMonitorFollowupId = indicatorViewMonitorFollowupFollowupContentEntity.getIndicatorViewMonitorFollowupId();
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_MONITOR_FOLLOWUP_CREATE_DELETE_UPDATE, indicatorViewMonitorFollowupFieldIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId));
    boolean isLocked = lock.tryLock(leaseTimeIndicatorViewMonitorFollowupCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new IndicatorInstanceException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_MONITOR_FOLLOWUP_LATER);
    }
    try {
      boolean isRemoved = indicatorViewMonitorFollowupFollowupContentService.remove(
          new LambdaQueryWrapper<IndicatorViewMonitorFollowupFollowupContentEntity>()
              .eq(IndicatorViewMonitorFollowupFollowupContentEntity::getAppId, appId)
              .eq(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupFollowupContentId, indicatorViewMonitorFollowupFollowupContentId)
      );
      if (!isRemoved) {
        log.warn("method IndicatorViewMonitorFollowupFollowupContentBiz.delete param indicatorViewMonitorFollowupFollowupContentId:{} is illegal", indicatorViewMonitorFollowupFieldIndicatorViewMonitorFollowupId);
        throw new IndicatorViewMonitorFollowupFollowupContentException(EnumESC.VALIDATE_EXCEPTION);
      }
      indicatorViewMonitorFollowupContentRefService.remove(
          new LambdaQueryWrapper<IndicatorViewMonitorFollowupContentRefEntity>()
              .eq(IndicatorViewMonitorFollowupContentRefEntity::getAppId, appId)
              .eq(IndicatorViewMonitorFollowupContentRefEntity::getIndicatorViewMonitorFollowupFollowupContentId, indicatorViewMonitorFollowupFollowupContentId)
      );
    } finally {
      lock.unlock();
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void batchDelete(List<String> indicatorViewMonitorFollowupFollowupContentIdList) throws InterruptedException {
    if (indicatorViewMonitorFollowupFollowupContentIdList.isEmpty()) {
      log.warn("method IndicatorViewMonitorFollowupFollowupContentBiz.batchDelete param indicatorViewMonitorFollowupFollowupContentIdList is empty");
      throw new IndicatorViewMonitorFollowupFollowupContentException(EnumESC.VALIDATE_EXCEPTION);
    }
    Set<String> dbIndicatorViewMonitorFollowupFollowupContentIdSet = indicatorViewMonitorFollowupFollowupContentService.lambdaQuery()
        .in(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupFollowupContentId, indicatorViewMonitorFollowupFollowupContentIdList)
        .list()
        .stream()
        .map(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupFollowupContentId)
        .collect(Collectors.toSet());
    if (
        indicatorViewMonitorFollowupFollowupContentIdList.stream().anyMatch(indicatorViewMonitorFollowupFollowupContentId -> !dbIndicatorViewMonitorFollowupFollowupContentIdSet.contains(indicatorViewMonitorFollowupFollowupContentId))
    ) {
      log.warn("method IndicatorViewMonitorFollowupFollowupContentBiz.batchDelete param indicatorViewMonitorFollowupFollowupContentIdList:{} is illegal", indicatorViewMonitorFollowupFollowupContentIdList);
      throw new IndicatorViewMonitorFollowupFollowupContentException(EnumESC.VALIDATE_EXCEPTION);
    }
    String indicatorViewMonitorFollowupFollowupContentId = indicatorViewMonitorFollowupFollowupContentIdList.get(0);
    IndicatorViewMonitorFollowupFollowupContentEntity indicatorViewMonitorFollowupFollowupContentEntity = indicatorViewMonitorFollowupFollowupContentService.lambdaQuery()
        .eq(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupFollowupContentId, indicatorViewMonitorFollowupFollowupContentId)
        .one();
    String appId = indicatorViewMonitorFollowupFollowupContentEntity.getAppId();
    String indicatorViewMonitorFollowupId = indicatorViewMonitorFollowupFollowupContentEntity.getIndicatorViewMonitorFollowupId();
    RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_MONITOR_FOLLOWUP_CREATE_DELETE_UPDATE, indicatorViewMonitorFollowupFieldIndicatorViewMonitorFollowupId, indicatorViewMonitorFollowupId));
    boolean isLocked = lock.tryLock(leaseTimeIndicatorViewMonitorFollowupCreateDeleteUpdate, TimeUnit.MILLISECONDS);
    if (!isLocked) {
      throw new IndicatorInstanceException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_MONITOR_FOLLOWUP_LATER);
    }
    try {
      boolean isRemoved = indicatorViewMonitorFollowupFollowupContentService.remove(
          new LambdaQueryWrapper<IndicatorViewMonitorFollowupFollowupContentEntity>()
              .eq(IndicatorViewMonitorFollowupFollowupContentEntity::getAppId, appId)
              .in(IndicatorViewMonitorFollowupFollowupContentEntity::getIndicatorViewMonitorFollowupFollowupContentId, dbIndicatorViewMonitorFollowupFollowupContentIdSet)
      );
      if (!isRemoved) {
        log.warn("method IndicatorViewMonitorFollowupFollowupContentBiz.batchDelete param indicatorViewMonitorFollowupFollowupContentIdList:{} is illegal", indicatorViewMonitorFollowupFollowupContentIdList);
        throw new IndicatorViewMonitorFollowupFollowupContentException(EnumESC.VALIDATE_EXCEPTION);
      }
      indicatorViewMonitorFollowupContentRefService.remove(
          new LambdaQueryWrapper<IndicatorViewMonitorFollowupContentRefEntity>()
              .eq(IndicatorViewMonitorFollowupContentRefEntity::getAppId, appId)
              .in(IndicatorViewMonitorFollowupContentRefEntity::getIndicatorViewMonitorFollowupFollowupContentId, dbIndicatorViewMonitorFollowupFollowupContentIdSet)
      );
    } finally {
      lock.unlock();
    }
  }

  public static IndicatorViewMonitorFollowupFollowupContentResponseRs indicatorViewMonitorFollowupFollowupContent2ResponseRs(
      IndicatorViewMonitorFollowupFollowupContentEntity indicatorViewMonitorFollowupFollowupContentEntity,
      List<IndicatorViewMonitorFollowupContentRefResponseRs> indicatorViewMonitorFollowupContentRefResponseRsList
      ) {
    return IndicatorViewMonitorFollowupFollowupContentResponseRs
        .builder()
        .id(indicatorViewMonitorFollowupFollowupContentEntity.getId())
        .indicatorViewMonitorFollowupFollowupContentId(indicatorViewMonitorFollowupFollowupContentEntity.getIndicatorViewMonitorFollowupFollowupContentId())
        .appId(indicatorViewMonitorFollowupFollowupContentEntity.getAppId())
        .indicatorViewMonitorFollowupId(indicatorViewMonitorFollowupFollowupContentEntity.getIndicatorViewMonitorFollowupId())
        .name(indicatorViewMonitorFollowupFollowupContentEntity.getName())
        .seq(indicatorViewMonitorFollowupFollowupContentEntity.getSeq())
        .indicatorViewMonitorFollowupContentRefResponseRsList(indicatorViewMonitorFollowupContentRefResponseRsList)
        .build();
  }
}
