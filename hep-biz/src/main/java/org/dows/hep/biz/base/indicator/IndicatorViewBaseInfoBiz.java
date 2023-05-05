package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumRedissonLock;
import org.dows.hep.biz.exception.IndicatorViewBaseInfoException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
* @description project descr:指标:查看指标基本信息类
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorViewBaseInfoBiz{
    @Value("${redisson.lock.lease-time.teacher.indicator-view-base-info-create-delete-update:5000}")
    private Integer leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate;
    private final String indicatorViewBaseInfoFieldIndicatorViewBaseInfoId = "indicatorViewBaseInfoId";
    private final IdGenerator idGenerator;
    private final RedissonClient redissonClient;
    private final IndicatorViewBaseInfoService indicatorViewBaseInfoService;
    private final IndicatorViewBaseInfoDescrService indicatorViewBaseInfoDescrService;
    private final IndicatorViewBaseInfoDescrRefService indicatorViewBaseInfoDescrRefService;
    private final IndicatorViewBaseInfoMonitorService indicatorViewBaseInfoMonitorService;
    private final IndicatorViewBaseInfoMonitorContentService indicatorViewBaseInfoMonitorContentService;
    private final IndicatorViewBaseInfoMonitorContentRefService indicatorViewBaseInfoMonitorContentRefService;
    private final IndicatorViewBaseInfoSingleService indicatorViewBaseInfoSingleService;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorInstanceService indicatorInstanceService;
    /**
    * @param
    * @return
    * @说明: 创建指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createIndicatorViewBaseInfo(CreateIndicatorViewBaseInfoRequest createIndicatorViewBaseInfo) {
        
    }

    /**
     * runsix method process
     * 1.if indicatorViewBaseInfoId isNotBlank
     *  1.1 delete IndicatorViewBaseInfoDescrEntity by indicatorViewBaseInfoId
     *  1.2 delete IndicatorViewBaseInfoDescrRefEntity by indicatorViewBaseInfoDescId
     *  1.3 delete IndicatorViewBaseInfoMonitorEntity by indicatorViewBaseInfoId
     *  1.4 delete IndicatorViewBaseInfoMonitorContentEntity by indicatorViewBaseInfoMonitorId
     *  1.5 delete IndicatorViewBaseInfoMonitorContentRefEntity by indicatorViewBaseInfoMonitorContentId
     *  1.6 delete IndicatorViewBaseInfoSingleEntity by indicatorViewBaseInfoId
     * 2.save IndicatorViewBaseInfoEntity
     *   2.1 save List<IndicatorViewBaseInfoDescrEntity>
     *     2.1.1 save List<IndicatorViewBaseInfoDescrRefEntity>
     *   2.2 save List<IndicatorViewBaseInfoMonitorEntity>
     *     2.2.1 save List<IndicatorViewBaseInfoMonitorContentEntity>
     *       2.2.1.1 save List<IndicatorViewBaseInfoMonitorContentRefEntity>
     *   2.3 save List<IndicatorViewBaseInfoSingleEntity>
    */
    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateRs(CreateOrUpdateIndicatorViewBaseInfoRequestRs createOrUpdateIndicatorViewBaseInfoRequestRs) throws InterruptedException {
        String appId = createOrUpdateIndicatorViewBaseInfoRequestRs.getAppId();
        String indicatorFuncId = createOrUpdateIndicatorViewBaseInfoRequestRs.getIndicatorFuncId();
        indicatorFuncService.lambdaQuery()
            .eq(IndicatorFuncEntity::getAppId, appId)
            .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method createIndicatorViewBaseInfoRs param createIndicatorViewBaseInfoRs field indicatorFuncId:{} is illegal", indicatorFuncId);
                throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
            });
        String indicatorViewBaseInfoId = createOrUpdateIndicatorViewBaseInfoRequestRs.getIndicatorViewBaseInfoId();
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_BASE_INFO_CREATE_DELETE_UPDATE, indicatorViewBaseInfoFieldIndicatorViewBaseInfoId, indicatorViewBaseInfoId));
        boolean isLocked = lock.tryLock(leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
        }
        try {
            if (StringUtils.isNotBlank(indicatorViewBaseInfoId)) {
                String finalIndicatorViewBaseInfoId3 = indicatorViewBaseInfoId;
                indicatorViewBaseInfoService.lambdaQuery()
                    .eq(IndicatorViewBaseInfoEntity::getAppId,appId)
                    .eq(IndicatorViewBaseInfoEntity::getIndicatorViewBaseInfoId, indicatorViewBaseInfoId)
                    .oneOpt()
                    .orElseThrow(() -> {
                        log.warn("method createOrUpdateRs param createOrUpdateIndicatorViewBaseInfoRequestRs field indicatorViewBaseInfoId:{} is illegal", finalIndicatorViewBaseInfoId3);
                        throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
                    });
                List<String> indicatorViewBaseInfoDescIdList = indicatorViewBaseInfoDescrService.lambdaQuery()
                    .eq(IndicatorViewBaseInfoDescrEntity::getAppId, appId)
                    .eq(IndicatorViewBaseInfoDescrEntity::getIndicatorViewBaseInfoId, indicatorViewBaseInfoId)
                    .list()
                    .stream()
                    .map(IndicatorViewBaseInfoDescrEntity::getIndicatorViewBaseInfoDescId)
                    .collect(Collectors.toList());
                indicatorViewBaseInfoDescrService.remove(
                    new LambdaQueryWrapper<IndicatorViewBaseInfoDescrEntity>()
                        .eq(IndicatorViewBaseInfoDescrEntity::getAppId, appId)
                        .eq(IndicatorViewBaseInfoDescrEntity::getIndicatorViewBaseInfoId, indicatorViewBaseInfoId)
                );
                if (!indicatorViewBaseInfoDescIdList.isEmpty()) {
                    indicatorViewBaseInfoDescrRefService.remove(
                        new LambdaQueryWrapper<IndicatorViewBaseInfoDescrRefEntity>()
                            .eq(IndicatorViewBaseInfoDescrRefEntity::getAppId, appId)
                            .in(IndicatorViewBaseInfoDescrRefEntity::getIndicatorViewBaseInfoDescId, indicatorViewBaseInfoDescIdList)
                    );
                }
                List<String> indicatorViewBaseInfoMonitorIdList = indicatorViewBaseInfoMonitorService.lambdaQuery()
                    .eq(IndicatorViewBaseInfoMonitorEntity::getAppId, appId)
                    .eq(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoId, indicatorViewBaseInfoId)
                    .list()
                    .stream()
                    .map(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoMonitorId)
                    .collect(Collectors.toList());
                indicatorViewBaseInfoMonitorService.remove(
                    new LambdaQueryWrapper<IndicatorViewBaseInfoMonitorEntity>()
                        .eq(IndicatorViewBaseInfoMonitorEntity::getAppId, appId)
                        .eq(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoId, indicatorViewBaseInfoId)
                );
                if (!indicatorViewBaseInfoMonitorIdList.isEmpty()) {
                    List<String> indicatorViewBaseInfoMonitorContentIdList = indicatorViewBaseInfoMonitorContentService.lambdaQuery()
                        .eq(IndicatorViewBaseInfoMonitorContentEntity::getAppId, appId)
                        .in(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorIdList)
                        .list()
                        .stream()
                        .map(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorContentId)
                        .collect(Collectors.toList());
                    if (!indicatorViewBaseInfoMonitorContentIdList.isEmpty()) {
                        indicatorViewBaseInfoMonitorContentRefService.remove(
                            new LambdaQueryWrapper<IndicatorViewBaseInfoMonitorContentRefEntity>()
                                .eq(IndicatorViewBaseInfoMonitorContentRefEntity::getAppId, appId)
                                .in(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentIdList)
                        );
                    }
                }
                indicatorViewBaseInfoSingleService.remove(
                    new LambdaQueryWrapper<IndicatorViewBaseInfoSingleEntity>()
                        .eq(IndicatorViewBaseInfoSingleEntity::getIndicatorViewBaseInfoId, indicatorViewBaseInfoId)
                );
            } else {
                indicatorViewBaseInfoId = idGenerator.nextIdStr();
                indicatorViewBaseInfoService.save(
                    IndicatorViewBaseInfoEntity
                        .builder()
                        .indicatorViewBaseInfoId(indicatorViewBaseInfoId)
                        .appId(appId)
                        .indicatorFuncId(indicatorFuncId)
                        .build()
                );
            }
            List<IndicatorViewBaseInfoDescrEntity> indicatorViewBaseInfoDescrEntityList = new ArrayList<>();
            List<IndicatorViewBaseInfoDescrRefEntity> indicatorViewBaseInfoDescrRefEntityList = new ArrayList<>();
            String finalIndicatorViewBaseInfoId = indicatorViewBaseInfoId;
            createOrUpdateIndicatorViewBaseInfoRequestRs.getCreateIndicatorViewBaseInfoDescrRsList()
                .forEach(createIndicatorViewBaseInfoDescrRs -> {
                    String indicatorViewBaseInfoDescId = idGenerator.nextIdStr();
                    indicatorViewBaseInfoDescrEntityList.add(
                        IndicatorViewBaseInfoDescrEntity
                            .builder()
                            .indicatorViewBaseInfoDescId(indicatorViewBaseInfoDescId)
                            .appId(appId)
                            .indicatorViewBaseInfoId(finalIndicatorViewBaseInfoId)
                            .name(createIndicatorViewBaseInfoDescrRs.getName())
                            .seq(createIndicatorViewBaseInfoDescrRs.getSeq())
                            .build()
                    );
                    AtomicInteger seqAtomicInteger = new AtomicInteger(1);
                    createIndicatorViewBaseInfoDescrRs.getIndicatorInstanceIdList()
                        .forEach(indicatorInstanceId -> indicatorViewBaseInfoDescrRefEntityList.add(
                            IndicatorViewBaseInfoDescrRefEntity
                                .builder()
                                .indicatorViewBaseInfoDescRefId(idGenerator.nextIdStr())
                                .appId(appId)
                                .indicatorViewBaseInfoDescId(indicatorViewBaseInfoDescId)
                                .indicatorInstanceId(indicatorInstanceId)
                                .seq(seqAtomicInteger.getAndIncrement())
                                .build()
                        ));
                });
            indicatorViewBaseInfoDescrService.saveBatch(indicatorViewBaseInfoDescrEntityList);
            indicatorViewBaseInfoDescrRefService.saveBatch(indicatorViewBaseInfoDescrRefEntityList);
            List<IndicatorViewBaseInfoMonitorEntity> indicatorViewBaseInfoMonitorEntityList = new ArrayList<>();
            List<IndicatorViewBaseInfoMonitorContentEntity> indicatorViewBaseInfoMonitorContentEntityList = new ArrayList<>();
            List<IndicatorViewBaseInfoMonitorContentRefEntity> indicatorViewBaseInfoMonitorContentRefEntityList = new ArrayList<>();
            String finalIndicatorViewBaseInfoId1 = indicatorViewBaseInfoId;
            createOrUpdateIndicatorViewBaseInfoRequestRs.getCreateIndicatorViewBaseInfoMonitorRsList()
                    .forEach(createIndicatorViewBaseInfoMonitorRs -> {
                        String indicatorViewBaseInfoMonitorId = idGenerator.nextIdStr();
                        indicatorViewBaseInfoMonitorEntityList.add(
                            IndicatorViewBaseInfoMonitorEntity
                                .builder()
                                .indicatorViewBaseInfoMonitorId(indicatorViewBaseInfoMonitorId)
                                .appId(appId)
                                .indicatorViewBaseInfoId(finalIndicatorViewBaseInfoId1)
                                .name(createIndicatorViewBaseInfoMonitorRs.getName())
                                .seq(createIndicatorViewBaseInfoMonitorRs.getSeq())
                                .build()
                        );
                        createIndicatorViewBaseInfoMonitorRs.getCreateIndicatorViewBaseInfoMonitorContentRsList()
                            .forEach(createIndicatorViewBaseInfoMonitorContentRs -> {
                                String indicatorViewBaseInfoMonitorContentId = idGenerator.nextIdStr();
                                indicatorViewBaseInfoMonitorContentEntityList.add(
                                    IndicatorViewBaseInfoMonitorContentEntity
                                        .builder()
                                        .indicatorViewBaseInfoMonitorContentId(indicatorViewBaseInfoMonitorContentId)
                                        .appId(appId)
                                        .indicatorViewBaseInfoMonitorId(indicatorViewBaseInfoMonitorId)
                                        .name(createIndicatorViewBaseInfoMonitorContentRs.getName())
                                        .seq(createIndicatorViewBaseInfoMonitorContentRs.getSeq())
                                        .build()
                                );
                                AtomicInteger seqAtomicInteger = new AtomicInteger(1);
                                createIndicatorViewBaseInfoMonitorContentRs.getIndicatorInstanceIdList()
                                    .forEach(indicatorInstanceId -> indicatorViewBaseInfoMonitorContentRefEntityList.add(
                                        IndicatorViewBaseInfoMonitorContentRefEntity
                                            .builder()
                                            .indicatorViewBaseInfoMonitorContentRefId(idGenerator.nextIdStr())
                                            .appId(appId)
                                            .indicatorViewBaseInfoMonitorContentId(indicatorViewBaseInfoMonitorContentId)
                                            .indicatorInstanceId(indicatorInstanceId)
                                            .seq(seqAtomicInteger.getAndIncrement())
                                            .build()
                                    ));
                            });
                    });
            indicatorViewBaseInfoMonitorService.saveBatch(indicatorViewBaseInfoMonitorEntityList);
            indicatorViewBaseInfoMonitorContentService.saveBatch(indicatorViewBaseInfoMonitorContentEntityList);
            indicatorViewBaseInfoMonitorContentRefService.saveBatch(indicatorViewBaseInfoMonitorContentRefEntityList);
            List<IndicatorViewBaseInfoSingleEntity> indicatorViewBaseInfoSingleEntityList = new ArrayList<>();
            String finalIndicatorViewBaseInfoId2 = indicatorViewBaseInfoId;
            createOrUpdateIndicatorViewBaseInfoRequestRs.getCreateIndicatorViewBaseInfoSingleRsList()
                    .forEach(createIndicatorViewBaseInfoSingleRs -> indicatorViewBaseInfoSingleEntityList.add(
                        IndicatorViewBaseInfoSingleEntity
                            .builder()
                            .indicatorViewBaseInfoSingleId(idGenerator.nextIdStr())
                            .appId(appId)
                            .indicatorViewBaseInfoId(finalIndicatorViewBaseInfoId2)
                            .indicatorInstanceId(createIndicatorViewBaseInfoSingleRs.getIndicatorInstanceId())
                            .seq(createIndicatorViewBaseInfoSingleRs.getSeq())
                            .build()
                    ));
            indicatorViewBaseInfoSingleService.saveBatch(indicatorViewBaseInfoSingleEntityList);
        } finally {
            lock.unlock();
        }
    }

    /**
    * @param
    * @return
    * @说明: 删除指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorViewBaseInfo(String indicatorViewBaseInfoId ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 批量删除
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void batchDelete(String string ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更改指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorViewBaseInfo(UpdateIndicatorViewBaseInfoRequest updateIndicatorViewBaseInfo ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取查看指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorViewBaseInfoResponse getIndicatorViewBaseInfo(String indicatorViewBaseInfoId ) {
        return new IndicatorViewBaseInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选查看指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorViewBaseInfoResponse> listIndicatorViewBaseInfo(String appId, String indicatorCategoryId, String name ) {
        return new ArrayList<IndicatorViewBaseInfoResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选查看指标基本信息类
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorViewBaseInfo(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name ) {
        return new String();
    }

    public IndicatorViewBaseInfoResponseRs getRs(String indicatorViewBaseInfoId) {
        IndicatorViewBaseInfoEntity indicatorViewBaseInfoEntity = indicatorViewBaseInfoService.lambdaQuery()
            .eq(IndicatorViewBaseInfoEntity::getIndicatorViewBaseInfoId, indicatorViewBaseInfoId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method IndicatorViewBaseInfoResponseRs param indicatorViewBaseInfoId:{} is illegal", indicatorViewBaseInfoId);
                throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
            });
        Long id = indicatorViewBaseInfoEntity.getId();
        String dbIndicatorViewBaseInfoId = indicatorViewBaseInfoEntity.getIndicatorViewBaseInfoId();
        String appId = indicatorViewBaseInfoEntity.getAppId();
        String indicatorFuncId = indicatorViewBaseInfoEntity.getIndicatorFuncId();
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        List<IndicatorViewBaseInfoDescrEntity> indicatorViewBaseInfoDescrEntityList = indicatorViewBaseInfoDescrService.lambdaQuery()
            .eq(IndicatorViewBaseInfoDescrEntity::getAppId, appId)
            .eq(IndicatorViewBaseInfoDescrEntity::getIndicatorViewBaseInfoId, dbIndicatorViewBaseInfoId)
            .list();
        Map<String, List<IndicatorViewBaseInfoDescrRefEntity>> kIndicatorViewBaseInfoDescIdVIndicatorViewBaseInfoDescrRefListMap = new HashMap<>();
        if (!indicatorViewBaseInfoDescrEntityList.isEmpty()) {
          indicatorViewBaseInfoDescrRefService.lambdaQuery()
              .eq(IndicatorViewBaseInfoDescrRefEntity::getAppId, appId)
              .in(IndicatorViewBaseInfoDescrRefEntity::getIndicatorViewBaseInfoDescId, indicatorViewBaseInfoDescrEntityList
                  .stream().map(IndicatorViewBaseInfoDescrEntity::getIndicatorViewBaseInfoDescId).collect(Collectors.toSet()))
              .list()
              .forEach(indicatorViewBaseInfoDescrRefEntity -> {
                String indicatorViewBaseInfoDescId = indicatorViewBaseInfoDescrRefEntity.getIndicatorViewBaseInfoDescId();
                indicatorInstanceIdSet.add(indicatorViewBaseInfoDescrRefEntity.getIndicatorInstanceId());
                List<IndicatorViewBaseInfoDescrRefEntity> indicatorViewBaseInfoDescrRefEntityList = kIndicatorViewBaseInfoDescIdVIndicatorViewBaseInfoDescrRefListMap.get(indicatorViewBaseInfoDescId);
                if (Objects.isNull(indicatorViewBaseInfoDescrRefEntityList)) {
                  indicatorViewBaseInfoDescrRefEntityList = new ArrayList<>();
                }
                indicatorViewBaseInfoDescrRefEntityList.add(indicatorViewBaseInfoDescrRefEntity);
                kIndicatorViewBaseInfoDescIdVIndicatorViewBaseInfoDescrRefListMap.put(indicatorViewBaseInfoDescId, indicatorViewBaseInfoDescrRefEntityList);
              });
        }
        List<IndicatorViewBaseInfoMonitorEntity> indicatorViewBaseInfoMonitorEntityList = indicatorViewBaseInfoMonitorService.lambdaQuery()
            .eq(IndicatorViewBaseInfoMonitorEntity::getAppId, appId)
            .eq(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoId, dbIndicatorViewBaseInfoId)
            .list();
        Map<String, List<IndicatorViewBaseInfoMonitorContentEntity>> kIndicatorViewBaseInfoMonitorIdVIndicatorViewBaseInfoMonitorContentListMap = new HashMap<>();
        Set<String> indicatorViewBaseInfoMonitorContentIdSet = new HashSet<>();
        Map<String, List<IndicatorViewBaseInfoMonitorContentRefEntity>> kIndicatorViewBaseInfoMonitorContentIdVIndicatorViewBaseInfoMonitorContentRefListMap = new HashMap<>();
        if (!indicatorViewBaseInfoMonitorEntityList.isEmpty()) {
          indicatorViewBaseInfoMonitorContentService.lambdaQuery()
              .eq(IndicatorViewBaseInfoMonitorContentEntity::getAppId, appId)
              .in(IndicatorViewBaseInfoMonitorContentEntity::getIndicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorEntityList
                  .stream().map(IndicatorViewBaseInfoMonitorEntity::getIndicatorViewBaseInfoMonitorId).collect(Collectors.toSet()))
              .list()
              .forEach(indicatorViewBaseInfoMonitorContentEntity -> {
                indicatorViewBaseInfoMonitorContentIdSet.add(indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorContentId());
                String indicatorViewBaseInfoMonitorId = indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorId();
                List<IndicatorViewBaseInfoMonitorContentEntity> indicatorViewBaseInfoMonitorContentEntityList = kIndicatorViewBaseInfoMonitorIdVIndicatorViewBaseInfoMonitorContentListMap.get(indicatorViewBaseInfoMonitorId);
                if (Objects.isNull(indicatorViewBaseInfoMonitorContentEntityList)) {
                  indicatorViewBaseInfoMonitorContentEntityList = new ArrayList<>();
                }
                indicatorViewBaseInfoMonitorContentEntityList.add(indicatorViewBaseInfoMonitorContentEntity);
                kIndicatorViewBaseInfoMonitorIdVIndicatorViewBaseInfoMonitorContentListMap.put(indicatorViewBaseInfoMonitorId, indicatorViewBaseInfoMonitorContentEntityList);
              });
          if (!indicatorViewBaseInfoMonitorContentIdSet.isEmpty()) {
            indicatorViewBaseInfoMonitorContentRefService.lambdaQuery()
                .eq(IndicatorViewBaseInfoMonitorContentRefEntity::getAppId, appId)
                .in(IndicatorViewBaseInfoMonitorContentRefEntity::getIndicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentIdSet)
                .list()
                .forEach(indicatorViewBaseInfoMonitorContentRefEntity -> {
                  indicatorInstanceIdSet.add(indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorInstanceId());
                  String indicatorViewBaseInfoMonitorContentId = indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorViewBaseInfoMonitorContentId();
                  List<IndicatorViewBaseInfoMonitorContentRefEntity> indicatorViewBaseInfoMonitorContentRefEntityList = kIndicatorViewBaseInfoMonitorContentIdVIndicatorViewBaseInfoMonitorContentRefListMap.get(indicatorViewBaseInfoMonitorContentId);
                  if (indicatorViewBaseInfoMonitorContentRefEntityList.isEmpty()) {
                    indicatorViewBaseInfoMonitorContentRefEntityList = new ArrayList<>();
                  }
                  indicatorViewBaseInfoMonitorContentRefEntityList.add(indicatorViewBaseInfoMonitorContentRefEntity);
                  kIndicatorViewBaseInfoMonitorContentIdVIndicatorViewBaseInfoMonitorContentRefListMap.put(indicatorViewBaseInfoMonitorContentId, indicatorViewBaseInfoMonitorContentRefEntityList);
                });
          }
        }
        List<IndicatorViewBaseInfoSingleEntity> indicatorViewBaseInfoSingleEntityList = indicatorViewBaseInfoSingleService.lambdaQuery()
            .eq(IndicatorViewBaseInfoSingleEntity::getAppId, appId)
            .eq(IndicatorViewBaseInfoSingleEntity::getIndicatorViewBaseInfoId, dbIndicatorViewBaseInfoId)
            .list();
        indicatorViewBaseInfoSingleEntityList.forEach(indicatorViewBaseInfoSingleEntity -> indicatorInstanceIdSet.add(indicatorViewBaseInfoSingleEntity.getIndicatorInstanceId()));
        Map<String, IndicatorInstanceResponseRs> kIndicatorInstanceIdVIndicatorInstanceResponseRsMap = new HashMap<>();
        if (!indicatorInstanceIdSet.isEmpty()) {
          indicatorInstanceService.lambdaQuery()
              .eq(IndicatorInstanceEntity::getAppId, appId)
              .in(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
              .list()
              .forEach(indicatorInstanceEntity -> kIndicatorInstanceIdVIndicatorInstanceResponseRsMap.put(
                  indicatorInstanceEntity.getIndicatorInstanceId(), IndicatorInstanceBiz.indicatorInstance2ResponseRs(indicatorInstanceEntity)
              ));
        }
        List<IndicatorViewBaseInfoDescrResponseRs> indicatorViewBaseInfoDescrResponseRsList = new ArrayList<>();
        if (!indicatorViewBaseInfoDescrEntityList.isEmpty()) {
          indicatorViewBaseInfoDescrResponseRsList = indicatorViewBaseInfoDescrEntityList
              .stream()
              .map(indicatorViewBaseInfoDescrEntity -> {
                String indicatorViewBaseInfoDescId = indicatorViewBaseInfoDescrEntity.getIndicatorViewBaseInfoDescId();
                List<IndicatorViewBaseInfoDescrRefEntity> indicatorViewBaseInfoDescrRefList = kIndicatorViewBaseInfoDescIdVIndicatorViewBaseInfoDescrRefListMap.get(indicatorViewBaseInfoDescId);
                return IndicatorViewBaseInfoDescrResponseRs
                    .builder()
                    .id(indicatorViewBaseInfoDescrEntity.getId())
                    .indicatorViewBaseInfoDescId(indicatorViewBaseInfoDescId)
                    .appId(indicatorViewBaseInfoDescrEntity.getAppId())
                    .indicatorViewBaseInfoId(indicatorViewBaseInfoDescrEntity.getIndicatorViewBaseInfoId())
                    .name(indicatorViewBaseInfoDescrEntity.getName())
                    .seq(indicatorViewBaseInfoDescrEntity.getSeq())
                    .indicatorViewBaseInfoDescrRefResponseRsList(indicatorViewBaseInfoDescrRefList
                        .stream()
                        .map(indicatorViewBaseInfoDescrRefEntity -> {
                          return IndicatorViewBaseInfoDescrRefResponseRs
                              .builder()
                              .id(indicatorViewBaseInfoDescrRefEntity.getId())
                              .indicatorViewBaseInfoDescRefId(indicatorViewBaseInfoDescrRefEntity.getIndicatorViewBaseInfoDescRefId())
                              .appId(indicatorViewBaseInfoDescrRefEntity.getAppId())
                              .indicatorViewBaseInfoDescId(indicatorViewBaseInfoDescrRefEntity.getIndicatorViewBaseInfoDescId())
                              .indicatorInstanceResponseRs(kIndicatorInstanceIdVIndicatorInstanceResponseRsMap.get(indicatorViewBaseInfoDescrRefEntity.getIndicatorInstanceId()))
                              .seq(indicatorViewBaseInfoDescrRefEntity.getSeq())
                              .build();
                        })
                        .collect(Collectors.toList())
                    )
                    .build();
              })
              .collect(Collectors.toList());
        }
        List<IndicatorViewBaseInfoMonitorResponseRs> indicatorViewBaseInfoMonitorResponseRsList = new ArrayList<>();
        if (!indicatorViewBaseInfoMonitorEntityList.isEmpty()) {
          indicatorViewBaseInfoMonitorResponseRsList = indicatorViewBaseInfoMonitorEntityList
              .stream()
              .map(indicatorViewBaseInfoMonitorEntity -> {
                String indicatorViewBaseInfoMonitorId = indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoMonitorId();
                List<IndicatorViewBaseInfoMonitorContentEntity> indicatorViewBaseInfoMonitorContentEntityList = kIndicatorViewBaseInfoMonitorIdVIndicatorViewBaseInfoMonitorContentListMap
                    .get(indicatorViewBaseInfoMonitorId);
                return IndicatorViewBaseInfoMonitorResponseRs
                    .builder()
                    .id(indicatorViewBaseInfoMonitorEntity.getId())
                    .indicatorViewBaseInfoMonitorId(indicatorViewBaseInfoMonitorId)
                    .appId(indicatorViewBaseInfoMonitorEntity.getAppId())
                    .indicatorViewBaseInfoId(indicatorViewBaseInfoMonitorEntity.getIndicatorViewBaseInfoId())
                    .name(indicatorViewBaseInfoMonitorEntity.getName())
                    .seq(indicatorViewBaseInfoMonitorEntity.getSeq())
                    .indicatorViewBaseInfoMonitorContentResponseRsList(indicatorViewBaseInfoMonitorContentEntityList
                        .stream()
                        .map(indicatorViewBaseInfoMonitorContentEntity -> {
                          String indicatorViewBaseInfoMonitorContentId = indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorContentId();
                          List<IndicatorViewBaseInfoMonitorContentRefEntity> indicatorViewBaseInfoMonitorContentRefEntityList = kIndicatorViewBaseInfoMonitorContentIdVIndicatorViewBaseInfoMonitorContentRefListMap
                              .get(indicatorViewBaseInfoMonitorContentId);
                          return IndicatorViewBaseInfoMonitorContentResponseRs
                              .builder()
                              .id(indicatorViewBaseInfoMonitorContentEntity.getId())
                              .indicatorViewBaseInfoMonitorContentId(indicatorViewBaseInfoMonitorContentId)
                              .appId(indicatorViewBaseInfoMonitorContentEntity.getAppId())
                              .indicatorViewBaseInfoMonitorId(indicatorViewBaseInfoMonitorContentEntity.getIndicatorViewBaseInfoMonitorId())
                              .name(indicatorViewBaseInfoMonitorContentEntity.getName())
                              .seq(indicatorViewBaseInfoMonitorContentEntity.getSeq())
                              .indicatorViewBaseInfoMonitorContentRefResponseRsList(indicatorViewBaseInfoMonitorContentRefEntityList
                                  .stream()
                                  .map(indicatorViewBaseInfoMonitorContentRefEntity -> {
                                    return IndicatorViewBaseInfoMonitorContentRefResponseRs
                                        .builder()
                                        .id(indicatorViewBaseInfoMonitorContentRefEntity.getId())
                                        .indicatorViewBaseInfoMonitorContentRefId(indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorViewBaseInfoMonitorContentRefId())
                                        .appId(indicatorViewBaseInfoMonitorContentRefEntity.getAppId())
                                        .indicatorViewBaseInfoMonitorContentId(indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorViewBaseInfoMonitorContentId())
                                        .indicatorInstanceResponseRs(kIndicatorInstanceIdVIndicatorInstanceResponseRsMap.get(indicatorViewBaseInfoMonitorContentRefEntity.getIndicatorInstanceId()))
                                        .seq(indicatorViewBaseInfoMonitorContentRefEntity.getSeq())
                                        .build();
                                  })
                                  .collect(Collectors.toList())
                              )
                              .build();
                        })
                        .collect(Collectors.toList())
                    )
                    .build();
              })
              .collect(Collectors.toList());
        }
        List<IndicatorViewBaseInfoSingleResponseRs> indicatorViewBaseInfoSingleResponseRsList = new ArrayList<>();
        if (!indicatorViewBaseInfoSingleEntityList.isEmpty()) {
          indicatorViewBaseInfoSingleResponseRsList = indicatorViewBaseInfoSingleEntityList
              .stream()
              .map(indicatorViewBaseInfoSingleEntity -> {
                return IndicatorViewBaseInfoSingleResponseRs
                    .builder()
                    .id(indicatorViewBaseInfoSingleEntity.getId())
                    .indicatorViewBaseInfoSingleId(indicatorViewBaseInfoSingleEntity.getIndicatorViewBaseInfoSingleId())
                    .appId(indicatorViewBaseInfoSingleEntity.getAppId())
                    .indicatorViewBaseInfoId(indicatorViewBaseInfoSingleEntity.getIndicatorViewBaseInfoId())
                    .indicatorInstanceResponseRs(kIndicatorInstanceIdVIndicatorInstanceResponseRsMap.get(indicatorViewBaseInfoSingleEntity.getIndicatorInstanceId()))
                    .seq(indicatorViewBaseInfoSingleEntity.getSeq())
                    .build();
              })
              .collect(Collectors.toList());
        }
        return IndicatorViewBaseInfoResponseRs
            .builder()
            .id(id)
            .indicatorViewBaseInfoId(dbIndicatorViewBaseInfoId)
            .appId(appId)
            .indicatorFuncId(indicatorFuncId)
            .indicatorViewBaseInfoDescrResponseRsList(indicatorViewBaseInfoDescrResponseRsList)
            .indicatorViewBaseInfoMonitorResponseRsList(indicatorViewBaseInfoMonitorResponseRsList)
            .indicatorViewBaseInfoSingleResponseRsList(indicatorViewBaseInfoSingleResponseRsList)
            .build();
    }
}