package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.CreateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.response.IndicatorFuncResponse;
import org.dows.hep.biz.enums.EnumESC;
import org.dows.hep.biz.enums.EnumRedissonLock;
import org.dows.hep.biz.enums.EnumString;
import org.dows.hep.biz.exception.IndicatorFuncException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public void createIndicatorFunc(CreateIndicatorFuncRequest createIndicatorFuncRequest) throws InterruptedException {
        String indicatorCategoryId = createIndicatorFuncRequest.getPid();
        IndicatorCategoryEntity indicatorCategoryEntity = indicatorCategoryService.lambdaQuery()
            .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("方法createIndicatorFunc的参数createIndicatorFuncRequest的indicatorCategoryId：{},不合法", indicatorCategoryId);
                throw new IndicatorFuncException(EnumESC.VALIDATE_EXCEPTION);
            });
        String appId = indicatorCategoryEntity.getAppId();
        String pid = indicatorCategoryEntity.getPid();
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
            indicatorFuncService.save(
                IndicatorFuncEntity
                    .builder()
                    .indicatorFuncId(idGenerator.nextIdStr())
                    .appId(appId)
                    .pid(pid)
                    .name(name)
                    .operationTip(operationTip)
                    .dialogTip(dialogTip)
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
    public void updateIndicatorFunc(UpdateIndicatorFuncRequest updateIndicatorFuncRequest) throws InterruptedException {
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
            .list()
            .stream()
            .map(IndicatorFuncBiz::indicatorFunc2Response)
            .collect(Collectors.toList());
    }

    private static IndicatorFuncResponse indicatorFunc2Response(IndicatorFuncEntity indicatorFuncEntity) {
        if (Objects.isNull(indicatorFuncEntity)) {
            return null;
        }
        return IndicatorFuncResponse
            .builder()
            .id(indicatorFuncEntity.getId())
            .indicatorFuncId(indicatorFuncEntity.getIndicatorFuncId())
            .appId(indicatorFuncEntity.getAppId())
            .pid(indicatorFuncEntity.getPid())
            .name(indicatorFuncEntity.getName())
            .operationTip(indicatorFuncEntity.getOperationTip())
            .dialogTip(indicatorFuncEntity.getDialogTip())
            .seq(indicatorFuncEntity.getSeq())
            .build();
    }
}