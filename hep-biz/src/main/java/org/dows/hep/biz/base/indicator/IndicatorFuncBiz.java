package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.CreateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.response.IndicatorFuncOrgItemResponse;
import org.dows.hep.api.base.indicator.response.IndicatorFuncOrgResponse;
import org.dows.hep.api.base.indicator.response.IndicatorFuncResponse;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumIndicatorCategory;
import org.dows.hep.api.enums.EnumRedissonLock;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.api.exception.IndicatorFuncException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorFuncService;
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
* @description project descr:指标:指标功能
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorFuncBiz{
    @Value("${redisson.lock.lease-time.teacher.indicator-func-create-delete-update:5000}")
    private Integer leaseTimeIndicatorFuncCreateDeleteUpdate;

    private final String indicatorFuncFieldPid = "pid";
    private final IdGenerator idGenerator;
    private final RedissonClient redissonClient;
    private final IndicatorFuncService indicatorFuncService;
    private final IndicatorCategoryService indicatorCategoryService;
    /**
    * @param
    * @return
    * @说明: 创建指标功能
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    @Transactional(rollbackFor = Exception.class)
    public void create(CreateIndicatorFuncRequest createIndicatorFuncRequest) throws InterruptedException {
        String pid = createIndicatorFuncRequest.getPid();
        String indicatorCategoryId = createIndicatorFuncRequest.getIndicatorCategoryId();
        Set<String> indicatorCategoryIdSet = new HashSet<>();
        indicatorCategoryIdSet.add(pid);
        indicatorCategoryIdSet.add(indicatorCategoryId);
        List<IndicatorCategoryEntity> indicatorCategoryEntityList = indicatorCategoryService.lambdaQuery()
            .in(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
            .list();
        if (indicatorCategoryEntityList.size() < 2) {
            log.info("method IndicatorFuncBiz.create param createIndicatorFuncRequest pid:{} or indicatorCategoryId:{} is illegal", pid, indicatorCategoryId);
            throw new IndicatorFuncException(EnumESC.VALIDATE_EXCEPTION);
        }
        String appId = indicatorCategoryEntityList.get(0).getAppId();
        String name = createIndicatorFuncRequest.getName();
        String operationTip = createIndicatorFuncRequest.getOperationTip();
        String dialogTip = createIndicatorFuncRequest.getDialogTip();
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_FUNC_CREATE_DELETE_UPDATE, indicatorFuncFieldPid, pid));
        boolean isLocked = lock.tryLock(leaseTimeIndicatorFuncCreateDeleteUpdate, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorFuncException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_FUNC_LATER);
        }
        try {
            AtomicInteger seqAtomicInteger = new AtomicInteger(1);
            indicatorFuncService.lambdaQuery()
                    .eq(IndicatorFuncEntity::getPid, pid)
                        .orderByDesc(IndicatorFuncEntity::getSeq)
                            .last(EnumString.LIMIT_1.getStr())
                                .oneOpt()
                                    .ifPresent(indicatorFuncEntity -> seqAtomicInteger.set(indicatorFuncEntity.getSeq() + 1));
            String indicatorFuncId = idGenerator.nextIdStr();
            indicatorFuncService.save(
                IndicatorFuncEntity
                    .builder()
                    .indicatorFuncId(indicatorFuncId)
                    .appId(appId)
                    .pid(pid)
                    .indicatorCategoryId(indicatorCategoryId)
                    .name(name)
                    .operationTip(operationTip)
                    .dialogTip(dialogTip)
                    .seq(seqAtomicInteger.get())
                    .build()
            );
            indicatorCategoryService.save(
                IndicatorCategoryEntity
                    .builder()
                    .indicatorCategoryId(indicatorFuncId)
                    .appId(appId)
                    .pid(indicatorCategoryId)
                    .categoryName(name)
                    .seq(seqAtomicInteger.get())
                    .build()
            );
        } finally {
            lock.unlock();
        }
    }
    /**
    * @param
    * @return
    * @说明: 删除指标功能
    * @关联表: 
    * @工时: 2H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorFunc(String indicatorFunc ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新指标功能
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateIndicatorFuncRequest updateIndicatorFuncRequest) throws InterruptedException {
        String indicatorFuncId = updateIndicatorFuncRequest.getIndicatorFuncId();
        IndicatorFuncEntity indicatorFuncEntity = indicatorFuncService.lambdaQuery()
            .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method updateIndicatorFunc param updateIndicatorFuncRequest's indicatorFuncId:{}, is illegal", indicatorFuncId);
                throw new IndicatorFuncException(EnumESC.VALIDATE_EXCEPTION);
            });
        String appId = indicatorFuncEntity.getAppId();
        String pid = indicatorFuncEntity.getPid();
        String name = updateIndicatorFuncRequest.getName();
        String operationTip = updateIndicatorFuncRequest.getOperationTip();
        String dialogTip = updateIndicatorFuncRequest.getDialogTip();
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_FUNC_CREATE_DELETE_UPDATE, indicatorFuncFieldPid, pid));
        boolean isLocked = lock.tryLock(leaseTimeIndicatorFuncCreateDeleteUpdate, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorFuncException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_FUNC_LATER);
        }
        try {
            indicatorFuncEntity.setName(name);
            indicatorFuncEntity.setOperationTip(operationTip);
            indicatorFuncEntity.setDialogTip(dialogTip);
            indicatorFuncService.updateById(indicatorFuncEntity);
        } finally {
            lock.unlock();
        }
    }
    /**
    * @param
    * @return
    * @说明: 获取指标功能
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorFuncResponse getIndicatorFunc(String indicatorFunc ) {
        return new IndicatorFuncResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选指标类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorFuncResponse> listIndicatorFunc(String appId, String indicatorCategoryId) {
        return new ArrayList<IndicatorFuncResponse>();
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
    public String pageIndicatorFunc(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name ) {
        return new String();
    }

    public List<IndicatorFuncResponse> getByPidAndAppId(String appId, String pid) {
        return indicatorFuncService.lambdaQuery()
            .eq(IndicatorFuncEntity::getAppId, appId)
            .eq(IndicatorFuncEntity::getPid, pid)
            .orderByAsc(IndicatorFuncEntity::getSeq)
            .list()
            .stream()
            .map(IndicatorFuncBiz::indicatorFunc2Response)
            .collect(Collectors.toList());
    }

    public static IndicatorFuncResponse indicatorFunc2Response(IndicatorFuncEntity indicatorFuncEntity) {
        if (Objects.isNull(indicatorFuncEntity)) {
            return null;
        }
        return IndicatorFuncResponse
            .builder()
            .id(indicatorFuncEntity.getId())
            .indicatorFuncId(indicatorFuncEntity.getIndicatorFuncId())
            .appId(indicatorFuncEntity.getAppId())
            .pid(indicatorFuncEntity.getPid())
            .indicatorCategoryId(indicatorFuncEntity.getIndicatorCategoryId())
            .name(indicatorFuncEntity.getName())
            .operationTip(indicatorFuncEntity.getOperationTip())
            .dialogTip(indicatorFuncEntity.getDialogTip())
            .seq(indicatorFuncEntity.getSeq())
            .build();
    }

    /* runsix:TODO 关联表后面再删除 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String indicatorFuncId) {
        if (StringUtils.isBlank(indicatorFuncId)) {
            log.warn("method IndicatorFuncBiz.delete param indicatorFuncId is blank");
            throw new IndicatorFuncException(EnumESC.VALIDATE_EXCEPTION);
        }
        boolean isRemoved = indicatorFuncService.remove(
            new LambdaQueryWrapper<IndicatorFuncEntity>()
                .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
        );
        if (!isRemoved) {
            log.warn("method IndicatorFuncBiz.delete param indicatorFuncId:{} is illegal", indicatorFuncId);
            throw new IndicatorFuncException(EnumESC.VALIDATE_EXCEPTION);
        }
    }


    public List<IndicatorFuncOrgResponse> getOrgEditFuncByAppId(String appId) {
        Set<String> indicatorFuncPidSet = new HashSet<>();
        indicatorFuncPidSet.add(EnumIndicatorCategory.VIEW_MANAGEMENT.getCode());
        indicatorFuncPidSet.add(EnumIndicatorCategory.JUDGE_MANAGEMENT.getCode());
        indicatorFuncPidSet.add(EnumIndicatorCategory.OPERATE_MANAGEMENT.getCode());
        Map<String, List<IndicatorFuncEntity>> kPidVIndicatorFuncEntityListMap = new HashMap<>();
        indicatorFuncService.lambdaQuery()
            .eq(IndicatorFuncEntity::getAppId, appId)
            .in(IndicatorFuncEntity::getPid, indicatorFuncPidSet)
            .list()
            .forEach(indicatorFuncEntity -> {
                String pid = indicatorFuncEntity.getPid();
                List<IndicatorFuncEntity> indicatorFuncEntityList = kPidVIndicatorFuncEntityListMap.get(pid);
                if (Objects.isNull(indicatorFuncEntityList)) {
                    indicatorFuncEntityList = new ArrayList<>();
                }
                indicatorFuncEntityList.add(indicatorFuncEntity);
                kPidVIndicatorFuncEntityListMap.put(pid, indicatorFuncEntityList);
            });
        return indicatorFuncPidSet
            .stream()
            .map(indicatorFuncPid -> {
                List<IndicatorFuncOrgItemResponse> indicatorFuncOrgItemResponseList = new ArrayList<>();
                List<IndicatorFuncEntity> indicatorFuncEntityList = kPidVIndicatorFuncEntityListMap.get(indicatorFuncPid);
                if (Objects.isNull(indicatorFuncEntityList)) {
                    indicatorFuncEntityList = new ArrayList<>();
                }
                indicatorFuncEntityList.forEach(indicatorFuncEntity -> {
                    indicatorFuncOrgItemResponseList.add(
                        IndicatorFuncOrgItemResponse
                            .builder()
                            .name(indicatorFuncEntity.getName())
                            .indicatorFuncId(indicatorFuncEntity.getIndicatorFuncId())
                            .build());
                });
                return IndicatorFuncOrgResponse
                    .builder()
                    .pid(indicatorFuncPid)
                    .pName(EnumIndicatorCategory.getCategoryNameByCode(indicatorFuncPid))
                    .indicatorFuncOrgItemResponseList(indicatorFuncOrgItemResponseList)
                    .build();
            }).collect(Collectors.toList());
    }
}