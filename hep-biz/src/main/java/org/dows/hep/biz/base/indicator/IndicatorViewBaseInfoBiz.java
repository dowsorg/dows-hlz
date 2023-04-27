package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorViewBaseInfoResponse;
import org.dows.hep.biz.enums.EnumESC;
import org.dows.hep.biz.enums.EnumRedissonLock;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final String indicatorFuncFieldPid = "pid";
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
     * 1.save IndicatorViewBaseInfoEntity
     *   1.1 save List<IndicatorViewBaseInfoDescrEntity>
     *     1.1.1 save List<IndicatorViewBaseInfoDescrRefEntity>
     *   1.2 save List<IndicatorViewBaseInfoMonitorEntity>
     *     1.2.1 save List<IndicatorViewBaseInfoMonitorContentEntity>
     *       1.2.1.1 save List<IndicatorViewBaseInfoMonitorContentRefEntity>
     *   1.3 save List<IndicatorViewBaseInfoSingleEntity>
     *
    */
    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateRs(CreateOrUpdateIndicatorViewBaseInfoRequestRs createIndicatorViewBaseInfoRs) throws InterruptedException {
        String indicatorViewBaseInfoId = createIndicatorViewBaseInfoRs.getIndicatorViewBaseInfoId();
        if (StringUtils.isNotBlank(indicatorViewBaseInfoId)) {

        } else {
            indicatorViewBaseInfoId = idGenerator.nextIdStr();
        }
        String appId = createIndicatorViewBaseInfoRs.getAppId();
        String indicatorFuncId = createIndicatorViewBaseInfoRs.getIndicatorFuncId();
        IndicatorFuncEntity indicatorFuncEntity = indicatorFuncService.lambdaQuery()
            .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method createIndicatorViewBaseInfoRs param createIndicatorViewBaseInfoRs field indicatorFuncId:{} is illegal", indicatorFuncId);
                throw new IndicatorViewBaseInfoException(EnumESC.VALIDATE_EXCEPTION);
            });
        String pid = indicatorFuncEntity.getPid();
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_VIEW_BASE_INFO_CREATE_DELETE_UPDATE, indicatorFuncFieldPid, pid));
        boolean isLocked = lock.tryLock(leaseTimeIndicatorViewBaseInfoCreateDeleteUpdate, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
        }
        try {
            indicatorViewBaseInfoService.saveOrUpdate(
                IndicatorViewBaseInfoEntity
                    .builder()
                    .indicatorViewBaseInfoId(indicatorViewBaseInfoId)
                    .appId(appId)
                    .indicatorFuncId(indicatorFuncId)
                    .build()
            );
            List<IndicatorViewBaseInfoDescrEntity> indicatorViewBaseInfoDescrEntityList = new ArrayList<>();
            List<IndicatorViewBaseInfoDescrRefEntity> indicatorViewBaseInfoDescrRefEntityList = new ArrayList<>();
            String finalIndicatorViewBaseInfoId = indicatorViewBaseInfoId;
            createIndicatorViewBaseInfoRs.getCreateIndicatorViewBaseInfoDescrRsList()
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
            createIndicatorViewBaseInfoRs.getCreateIndicatorViewBaseInfoMonitorRsList()
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
            createIndicatorViewBaseInfoRs.getCreateIndicatorViewBaseInfoSingleRsList()
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
}