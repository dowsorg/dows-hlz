package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.IndicatorCategoryException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.dows.hep.entity.IndicatorCategoryRefEntity;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.dows.hep.service.IndicatorCategoryRefService;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorInstanceService;
import org.dows.sequence.api.IdGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
* @description project descr:指标:指标类别
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorCategoryBiz{
    @Value("${redisson.lock.lease-time.teacher.indicator-category-create-delete-update:5000}")
    private Integer leaseTimeIndicatorCategoryCreateDeleteUpdate;
    private final String indicatorCategoryFieldPid = "pid";

    private final IndicatorCategoryRefService indicatorCategoryRefService;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorInstanceService indicatorInstanceService;
    private final RedissonClient redissonClient;
    private final IdGenerator idGenerator;
    private final RsIndicatorInstanceBiz rsIndicatorInstanceBiz;

    public static IndicatorCategoryResponse indicatorCategoryEntity2Response(IndicatorCategoryEntity indicatorCategoryEntity) {
        if (Objects.isNull(indicatorCategoryEntity)) {
            return null;
        }
        return IndicatorCategoryResponse
            .builder()
            .id(indicatorCategoryEntity.getId())
            .indicatorCategoryId(indicatorCategoryEntity.getIndicatorCategoryId())
            .appId(indicatorCategoryEntity.getAppId())
            .pid(indicatorCategoryEntity.getPid())
            .categoryName(indicatorCategoryEntity.getCategoryName())
            .seq(indicatorCategoryEntity.getSeq())
            .build();
    }

    /**
    * @param
    * @return
    * @说明: 创建指标类别
    * @关联表: indicator_category
    * @工时: 3H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    @Transactional(rollbackFor = Exception.class)
    public void createIndicatorCategory(CreateIndicatorCategoryRequest createIndicatorCategory) throws InterruptedException {

    }

    @Transactional(rollbackFor = Exception.class)
    public void createRs(CreateOrUpdateIndicatorCategoryRequestRs createOrUpdateIndicatorCategoryRequestRs) {
        IndicatorCategoryEntity indicatorCategoryEntity = null;
        String indicatorCategoryId = createOrUpdateIndicatorCategoryRequestRs.getIndicatorCategoryId();
        String appId = createOrUpdateIndicatorCategoryRequestRs.getAppId();
        String pid = createOrUpdateIndicatorCategoryRequestRs.getPid();
        String categoryName = createOrUpdateIndicatorCategoryRequestRs.getCategoryName();
        Integer seq = createOrUpdateIndicatorCategoryRequestRs.getSeq();
        if (StringUtils.isNotBlank(pid)) {
            indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getIndicatorCategoryId, pid)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method batchCreateOrUpdateIndicatorCategory param createOrUpdateIndicatorCategoryRequestRs pid:{} is illegal", pid);
                    return new IndicatorCategoryException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        if (StringUtils.isBlank(indicatorCategoryId)) {
            indicatorCategoryId = idGenerator.nextIdStr();
            AtomicInteger seqAtomicInteger = new AtomicInteger(1);
            indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getPid, pid)
                .orderByDesc(IndicatorCategoryEntity::getSeq)
                .last(EnumString.LIMIT_1.getStr())
                .oneOpt()
                .ifPresent(indicatorFuncEntity -> seqAtomicInteger.set(indicatorFuncEntity.getSeq() + 1));
            indicatorCategoryEntity = IndicatorCategoryEntity
                .builder()
                .indicatorCategoryId(indicatorCategoryId)
                .appId(appId)
                .pid(pid)
                .categoryName(categoryName)
                .seq(seqAtomicInteger.get())
                .build();
        } else {
            String finalIndicatorCategoryId = indicatorCategoryId;
            indicatorCategoryEntity = indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method batchCreateOrUpdateIndicatorCategory param createOrUpdateIndicatorCategoryRequestRs indicatorCategoryId:{} is illegal", finalIndicatorCategoryId);
                    return new IndicatorCategoryException(EnumESC.VALIDATE_EXCEPTION);
                });
            indicatorCategoryEntity.setCategoryName(categoryName);
            indicatorCategoryEntity.setSeq(seq);
        }
        indicatorCategoryService.saveOrUpdate(indicatorCategoryEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchCreateOrUpdateRs(BatchCreateOrUpdateIndicatorCategoryRequest batchCreateOrUpdateIndicatorCategoryRequest) throws InterruptedException {
        String appId = batchCreateOrUpdateIndicatorCategoryRequest.getAppId();
        String pid = batchCreateOrUpdateIndicatorCategoryRequest.getPid();
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_CATEGORY_CREATE_DELETE_UPDATE, indicatorCategoryFieldPid, pid));
        boolean isLocked = lock.tryLock(leaseTimeIndicatorCategoryCreateDeleteUpdate, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorCategoryException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_CATEGORY_LATER);
        }
        try {
            if (Objects.nonNull(pid)) {
                indicatorCategoryService.lambdaQuery()
                    .eq(IndicatorCategoryEntity::getIndicatorCategoryId, pid)
                    .oneOpt()
                    .orElseThrow(() -> {
                        log.warn("method batchCreateOrUpdateIndicatorCategory param batchCreateOrUpdateIndicatorCategory pid：{} is illegal", pid);
                        return new IndicatorCategoryException(EnumESC.VALIDATE_EXCEPTION);
                    });
            }
            List<IndicatorCategoryEntity> creatIndicatorCategoryEntityList = new ArrayList<>();
            List<IndicatorCategoryEntity> updateIndicatorCategoryEntityList = new ArrayList<>();
            List<BatchCreateOrUpdateIndicatorCategoryDTO> batchCreateOrUpdateIndicatorCategoryDTOList = batchCreateOrUpdateIndicatorCategoryRequest.getBatchCreateOrUpdateIndicatorCategoryDTOList();
            List<String> paramIndicatorCategoryIdList = batchCreateOrUpdateIndicatorCategoryDTOList
                .stream()
                .map(BatchCreateOrUpdateIndicatorCategoryDTO::getIndicatorCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            List<String> dbIndicatorCategoryIdList = new ArrayList<>();
            Map<String, IndicatorCategoryEntity> kIndicatorCategoryIdVIndicatorCategoryEntityMap = indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getAppId, appId)
                .eq(IndicatorCategoryEntity::getPid, pid)
                .list()
                .stream()
                .peek(indicatorCategoryEntity -> dbIndicatorCategoryIdList.add(indicatorCategoryEntity.getIndicatorCategoryId()))
                .collect(Collectors.toMap(IndicatorCategoryEntity::getIndicatorCategoryId, a -> a));
            if (paramIndicatorCategoryIdList.size() != dbIndicatorCategoryIdList.size()) {
                log.warn("方法batchCreateOrUpdateIndicatorCategory的参数batchCreateOrUpdateIndicatorCategory传过来的修改类别数量与数据库不一致");
                throw new IndicatorCategoryException(EnumESC.VALIDATE_EXCEPTION);
            }
            if (paramIndicatorCategoryIdList.parallelStream()
                .anyMatch(indicatorCategoryId -> !dbIndicatorCategoryIdList.contains(indicatorCategoryId))
            ) {
                log.warn("方法batchCreateOrUpdateIndicatorCategory的参数batchCreateOrUpdateIndicatorCategory传过来的IndicatorCategoryIdList存在数据库不存在的。" +
                    "paramIndicatorCategoryIdList：{}, dbIndicatorCategoryIdList:{}", paramIndicatorCategoryIdList, dbIndicatorCategoryIdList);
                throw new IndicatorCategoryException(EnumESC.VALIDATE_EXCEPTION);
            }
            AtomicInteger atomicInteger = new AtomicInteger(1);
            batchCreateOrUpdateIndicatorCategoryDTOList
                .forEach(batchCreateOrUpdateIndicatorCategoryDTO -> {
                    String indicatorCategoryId = batchCreateOrUpdateIndicatorCategoryDTO.getIndicatorCategoryId();
                    if (Objects.isNull(indicatorCategoryId)) {
                        creatIndicatorCategoryEntityList.add(
                            IndicatorCategoryEntity
                                .builder()
                                .indicatorCategoryId(idGenerator.nextIdStr())
                                .appId(appId)
                                .pid(pid)
                                .categoryName(batchCreateOrUpdateIndicatorCategoryDTO.getCategoryName())
                                .seq(atomicInteger.getAndIncrement())
                                .build());
                    } else {
                        IndicatorCategoryEntity indicatorCategoryEntity = kIndicatorCategoryIdVIndicatorCategoryEntityMap.get(batchCreateOrUpdateIndicatorCategoryDTO.getIndicatorCategoryId());
                        indicatorCategoryEntity.setCategoryName(batchCreateOrUpdateIndicatorCategoryDTO.getCategoryName());
                        indicatorCategoryEntity.setSeq(atomicInteger.getAndIncrement());
                        updateIndicatorCategoryEntityList.add(indicatorCategoryEntity);
                    }
                });
            if (!creatIndicatorCategoryEntityList.isEmpty()) {
                indicatorCategoryService.saveBatch(creatIndicatorCategoryEntityList);
            }
            if (!updateIndicatorCategoryEntityList.isEmpty()) {
                indicatorCategoryService.updateBatchById(updateIndicatorCategoryEntityList);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
    * @param
    * @return
    * @说明: 删除指标类别
    * @关联表: 
    * @工时: 2H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String indicatorCategoryId) throws ExecutionException, InterruptedException {
        EnumIndicatorCategory enumIndicatorCategory = EnumIndicatorCategory.kCodeVEnumIndicatorCategoryMap.get(indicatorCategoryId);
        if (Objects.nonNull(enumIndicatorCategory) && EnumStatus.ENABLE.getCode().equals(enumIndicatorCategory.getCannotDelete())) {
            throw new IndicatorCategoryException(EnumESC.SYSTEM_INDICATOR_CATEGORY_CANNOT_DELETE);
        }

        AtomicReference<IndicatorCategoryEntity> indicatorCategoryEntityAR = new AtomicReference<>();
        CompletableFuture<Void> cfCheckIndicatorCategoryId = CompletableFuture.runAsync(() -> {
            rsIndicatorInstanceBiz.checkIndicatorCategoryId(indicatorCategoryEntityAR, indicatorCategoryId);
        });
        cfCheckIndicatorCategoryId.get();
        IndicatorCategoryEntity indicatorCategoryEntity = indicatorCategoryEntityAR.get();
        String appId = indicatorCategoryEntity.getAppId();

        Set<String> indicatorInstanceIdSet = new HashSet<>();
        CompletableFuture<Void> cfPopulateIndicatorInstanceIdSet = CompletableFuture.runAsync(() -> {
            rsIndicatorInstanceBiz.populateIndicatorInstanceIdSet(indicatorInstanceIdSet, indicatorCategoryId);
        });
        cfPopulateIndicatorInstanceIdSet.get();
        rsIndicatorInstanceBiz.checkIndicatorInstanceDelete(appId, indicatorInstanceIdSet);

        boolean isRefByIndicatorCategory = indicatorCategoryService.lambdaQuery()
            .eq(IndicatorCategoryEntity::getPid, indicatorCategoryId)
            .exists();

        if (isRefByIndicatorCategory) {
            throw new IndicatorCategoryException(EnumESC.INDICATOR_CATEGORY_HAS_DATA_CANNOT_DELETE);
        }
        boolean isRefByIndicatorInstance = indicatorInstanceService.lambdaQuery()
            .eq(IndicatorInstanceEntity::getIndicatorCategoryId, indicatorCategoryId)
            .exists();
        if (isRefByIndicatorInstance) {
            throw new IndicatorCategoryException(EnumESC.INDICATOR_CATEGORY_HAS_DATA_CANNOT_DELETE);
        }
        boolean isSuccess = indicatorCategoryService.remove(
            new LambdaQueryWrapper<IndicatorCategoryEntity>()
                .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
        );
        if (!isSuccess) {
            throw new IndicatorCategoryException(EnumESC.INDICATOR_CATEGORY_DELETE_FAILED);
        }
    }
    /**
    * @param
    * @return
    * @说明: 更新指标类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    @Transactional(rollbackFor = Exception.class)
    public void updateIndicatorCategory(UpdateIndicatorCategoryRequest updateIndicatorCategory ) {
        
    }

    public List<IndicatorCategoryResponse> getByPid(String appId, String pid) {
        return indicatorCategoryService.lambdaQuery()
            .eq(IndicatorCategoryEntity::getAppId, appId)
            .isNull(StringUtils.isBlank(pid), IndicatorCategoryEntity::getPid)
            .eq(StringUtils.isNotBlank(pid), IndicatorCategoryEntity::getPid, pid)
            .orderByAsc(IndicatorCategoryEntity::getSeq)
            .list()
            .stream()
            .map(IndicatorCategoryBiz::indicatorCategoryEntity2Response)
            .collect(Collectors.toList());
    }
    /**
    * @param
    * @return
    * @说明: 查询指标类别
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorCategoryResponse getIndicatorCategory(String indicatorCategoryId ) {
        return new IndicatorCategoryResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选指标类别
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorCategoryResponse> listIndicatorCategory(String appId, Long pid, String indicatorCategoryId, String categoryCode, String categoryName ) {
        return new ArrayList<IndicatorCategoryResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选指标类别
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorCategory(Integer pageNo, Integer pageSize, String appId, Long pid, String indicatorCategoryId, String categoryCode, String categoryName ) {
        return new String();
    }
    /**
    * @param
    * @return
    * @说明: 一键同步（非常复杂）
    * @关联表: 
    * @工时: 8H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void sync(String appId ) {
        
    }
}