package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.BatchCreateOrUpdateIndicatorCategoryDTO;
import org.dows.hep.api.base.indicator.request.BatchCreateOrUpdateIndicatorCategoryRequest;
import org.dows.hep.api.base.indicator.request.CreateIndicatorCategoryRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorCategoryRequest;
import org.dows.hep.api.base.indicator.response.IndicatorCategoryResponse;
import org.dows.hep.biz.enums.EnumESC;
import org.dows.hep.biz.enums.EnumRedissonLock;
import org.dows.hep.biz.exception.IndicatorCategoryException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorInstanceService;
import org.dows.sequence.api.IdGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final String IndicatorCategoryFieldPid = "pid";
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorInstanceService indicatorInstanceService;
    private final RedissonClient redissonClient;
    private final IdGenerator idGenerator;

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
    public void batchCreateOrUpdateIndicatorCategory(BatchCreateOrUpdateIndicatorCategoryRequest batchCreateOrUpdateIndicatorCategoryRequest) throws InterruptedException {
        String appId = batchCreateOrUpdateIndicatorCategoryRequest.getAppId();
        String pid = batchCreateOrUpdateIndicatorCategoryRequest.getPid();
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_CATEGORY_CREATE_DELETE_UPDATE, IndicatorCategoryFieldPid, pid));
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
                        log.warn("方法batchCreateOrUpdateIndicatorCategory的参数batchCreateOrUpdateIndicatorCategory的pid：{}在数据库不存在", pid);
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
    public void deleteIndicatorCategory(String indicatorCategoryId) {
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