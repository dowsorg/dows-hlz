package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.CreateIndicatorInstanceRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorInstanceRequest;
import org.dows.hep.api.base.indicator.response.IndicatorInstanceResponse;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.IndicatorInstanceException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.IndicatorCategoryEntity;
import org.dows.hep.entity.IndicatorCategoryRefEntity;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.dows.hep.entity.IndicatorRuleEntity;
import org.dows.hep.service.IndicatorCategoryRefService;
import org.dows.hep.service.IndicatorCategoryService;
import org.dows.hep.service.IndicatorInstanceService;
import org.dows.hep.service.IndicatorRuleService;
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
* @description project descr:指标:指标实例
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorInstanceBiz{
    @Value("${redisson.lock.lease-time.teacher.indicator-instance-create-delete-update:5000}")
    private Integer leaseTimeIndicatorInstanceCreateDeleteUpdate;
    private final String indicatorInstanceFieldPid = "pid";
    private final IdGenerator idGenerator;
    private final RedissonClient redissonClient;
    private final IndicatorInstanceService indicatorInstanceService;
    private final IndicatorCategoryRefService indicatorCategoryRefService;
    private final IndicatorRuleService indicatorRuleService;
    private final IndicatorCategoryService indicatorCategoryService;

    public static IndicatorInstanceResponseRs indicatorInstance2ResponseRs(IndicatorInstanceEntity indicatorInstanceEntity) {
        if (Objects.isNull(indicatorInstanceEntity)) {
            return null;
        }
        return IndicatorInstanceResponseRs
            .builder()
            .id(indicatorInstanceEntity.getId())
            .indicatorInstanceId(indicatorInstanceEntity.getIndicatorInstanceId())
            .appId(indicatorInstanceEntity.getAppId())
            .indicatorCategoryId(indicatorInstanceEntity.getIndicatorCategoryId())
            .indicatorName(indicatorInstanceEntity.getIndicatorName())
            .unit(indicatorInstanceEntity.getUnit())
            .core(indicatorInstanceEntity.getCore())
            .food(indicatorInstanceEntity.getFood())
            .expression(indicatorInstanceEntity.getExpression())
            .rawExpression(indicatorInstanceEntity.getRawExpression())
            .descr(indicatorInstanceEntity.getDescr())
            .build();
    }
    /**
    * @param
    * @return
    * @说明: 创建指标实例
    * @关联表: 
    * @工时: 3H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    /**
     * runsix method process
     * 1.save IndicatorInstance
     * 2.save IndicatorCategoryRef
     * 3.save IndicatorRule
    */
    @Transactional(rollbackFor = Exception.class)
    public void createIndicatorInstance(CreateIndicatorInstanceRequest createIndicatorInstanceRequest) throws InterruptedException {
        String indicatorCategoryId = createIndicatorInstanceRequest.getIndicatorCategoryId();
        if (Objects.nonNull(indicatorCategoryId)) {
            indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("方法createIndicatorInstance的参数createIndicatorInstanceRequest的indicatorCategoryId：{}不存在", indicatorCategoryId);
                    return new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
                });
        }
        String appId = createIndicatorInstanceRequest.getAppId();
        String indicatorInstanceId = idGenerator.nextIdStr();
        String indicatorName = createIndicatorInstanceRequest.getIndicatorName();
        String unit = createIndicatorInstanceRequest.getUnit();
        Integer core = createIndicatorInstanceRequest.getCore();
        Integer food = createIndicatorInstanceRequest.getFood();
        String min = createIndicatorInstanceRequest.getMin();
        String max = createIndicatorInstanceRequest.getMax();
        String def = createIndicatorInstanceRequest.getDef();
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_INSTANCE_CREATE_DELETE_UPDATE, indicatorInstanceFieldPid, indicatorCategoryId));
        boolean isLocked = lock.tryLock(leaseTimeIndicatorInstanceCreateDeleteUpdate, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorInstanceException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_INSTANCE_LATER);
        }
        try {
            indicatorInstanceService.save(
                IndicatorInstanceEntity
                    .builder()
                    .indicatorInstanceId(indicatorCategoryId)
                    .appId(appId)
                    .indicatorInstanceId(indicatorInstanceId)
                    .indicatorName(indicatorName)
                    .unit(unit)
                    .core(core)
                    .food(food)
                    .build()
            );
            AtomicInteger seqAtomicInteger = new AtomicInteger(1);
            indicatorCategoryRefService.lambdaQuery()
                    .eq(IndicatorCategoryRefEntity::getIndicatorCategoryId, indicatorCategoryId)
                        .orderByDesc(IndicatorCategoryRefEntity::getSeq)
                            .last(EnumString.LIMIT_1.getStr())
                                .oneOpt()
                                    .ifPresent(indicatorCategoryRefEntity -> seqAtomicInteger.set(indicatorCategoryRefEntity.getSeq() + 1));
            indicatorCategoryRefService.save(
                IndicatorCategoryRefEntity
                    .builder()
                    .indicatorCategoryRefId(idGenerator.nextIdStr())
                    .appId(appId)
                    .indicatorCategoryId(indicatorCategoryId)
                    .indicatorInstanceId(indicatorInstanceId)
                    .seq(seqAtomicInteger.get())
                    .build()
            );
            indicatorRuleService.save(
                IndicatorRuleEntity
                    .builder()
                    .indicatorRuleId(idGenerator.nextIdStr())
                    .appId(appId)
                    .variableId(indicatorInstanceId)
                    .ruleType(EnumIndicatorRuleType.INDICATOR.getCode())
                    .min(min)
                    .max(max)
                    .def(def)
                    .build()
            );
        } finally {
            lock.unlock();
        }
    }
    /**
    * @param
    * @return
    * @说明: 删除指标
    * @关联表: 
    * @工时: 2H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    /**
     * runsix method process
     * TODO 删除的引用关系需要确认
     * 1.delete IndicatorInstance
     * 2.delete IndicatorCategoryRef
     * 3.delete IndicatorRule
    */
    @Transactional(rollbackFor = Exception.class)
    public void deleteIndicatorInstance(String indicatorInstanceId) throws InterruptedException {
        IndicatorInstanceEntity indicatorInstanceEntity = indicatorInstanceService.lambdaQuery()
            .eq(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("方法deleteIndicatorInstance的参数indicatorInstanceId：{}，不存在", indicatorInstanceId);
                throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
            });
        String appId = indicatorInstanceEntity.getAppId();
        IndicatorCategoryRefEntity indicatorCategoryRefEntity = indicatorCategoryRefService.lambdaQuery()
            .eq(IndicatorCategoryRefEntity::getIndicatorInstanceId, indicatorInstanceId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("方法deleteIndicatorInstance对indicatorInstanceId：{}的IndicatorInstance不存在", indicatorInstanceId);
                throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
            });
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_INSTANCE_CREATE_DELETE_UPDATE, indicatorInstanceFieldPid, indicatorCategoryRefEntity.getIndicatorCategoryId()));
        boolean isLocked = lock.tryLock(leaseTimeIndicatorInstanceCreateDeleteUpdate, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorInstanceException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_INSTANCE_LATER);
        }
        try {
            boolean isRemovedIndicatorInstance = indicatorInstanceService.remove(
                new LambdaQueryWrapper<IndicatorInstanceEntity>()
                    .eq(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceId)
            );
            if (!isRemovedIndicatorInstance) {
                log.warn("方法deleteIndicatorInstance对indicatorInstanceId：{}的IndicatorInstance删除失败", indicatorInstanceId);
                throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
            }
            boolean isRemovedIndicatorCategoryRef = indicatorCategoryRefService.remove(
                new LambdaQueryWrapper<IndicatorCategoryRefEntity>()
                    .eq(IndicatorCategoryRefEntity::getIndicatorInstanceId, indicatorInstanceId)
            );
            if (!isRemovedIndicatorCategoryRef) {
                log.warn("方法deleteIndicatorInstance对indicatorInstanceId：{}的IndicatorCategoryRef删除失败", indicatorInstanceId);
                throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
            }
            boolean isRemovedIndicatorRule = indicatorRuleService.remove(
                new LambdaQueryWrapper<IndicatorRuleEntity>()
                    .eq(IndicatorRuleEntity::getVariableId, indicatorInstanceId)
            );
            if (!isRemovedIndicatorRule) {
                log.warn("方法deleteIndicatorInstance对VariableId：{}的IndicatorRule删除失败", indicatorInstanceId);
                throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
            }
        } finally {
            lock.unlock();
        }
    }
    /**
    * @param
    * @return
    * @说明: 更新指标
    * @关联表: 
    * @工时: 3H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorInstance(UpdateIndicatorInstanceRequest updateIndicatorInstance) {
    }
    /**
    * @param
    * @return
    * @说明: 批量更新指标
    * @关联表: 
    * @工时: 3H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void batchUpdateIndicatorInstance(List<UpdateIndicatorInstanceRequest> updateIndicatorInstance ) {
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateCore(List<String> indicatorInstanceIdList) {
        List<String> dbIndicatorInstanceIdList = new ArrayList<>();
        List<IndicatorInstanceEntity> indicatorInstanceEntityList = indicatorInstanceService.lambdaQuery()
            .in(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceIdList)
            .list()
            .stream()
            .peek(indicatorInstanceEntity -> {
                dbIndicatorInstanceIdList.add(indicatorInstanceEntity.getIndicatorInstanceId());
                indicatorInstanceEntity.setCore(EnumStatus.ENABLE.getCode());
            })
            .collect(Collectors.toList());
        for (String indicatorInstanceId : indicatorInstanceIdList) {
            if (!dbIndicatorInstanceIdList.contains(indicatorInstanceId)) {
                log.warn("method:batchUpdateCore param indicatorInstanceIdList:{}, exist illegal data indicatorInstanceId:{}", indicatorInstanceIdList, indicatorInstanceId);
                throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
            }
        }
        indicatorInstanceService.saveOrUpdateBatch(indicatorInstanceEntityList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateFood(List<String> indicatorInstanceIdList) {
        List<String> dbIndicatorInstanceIdList = new ArrayList<>();
        List<IndicatorInstanceEntity> indicatorInstanceEntityList = indicatorInstanceService.lambdaQuery()
            .in(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceIdList)
            .list()
            .stream()
            .peek(indicatorInstanceEntity -> {
                dbIndicatorInstanceIdList.add(indicatorInstanceEntity.getIndicatorInstanceId());
                indicatorInstanceEntity.setFood(EnumStatus.ENABLE.getCode());
            })
            .collect(Collectors.toList());
        for (String indicatorInstanceId : indicatorInstanceIdList) {
            if (!dbIndicatorInstanceIdList.contains(indicatorInstanceId)) {
                log.warn("method:batchUpdateFood param indicatorInstanceIdList:{}, exist illegal data indicatorInstanceId:{}", indicatorInstanceIdList, indicatorInstanceId);
                throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
            }
        }
        indicatorInstanceService.saveOrUpdateBatch(indicatorInstanceEntityList);
    }

    /**
    * @param
    * @return
    * @说明: 查询指标
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorInstanceResponse getIndicatorInstance(String indicatorInstanceId ) {
        return new IndicatorInstanceResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选指标
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorInstanceResponse> listIndicatorInstance(String appId, Integer core, Integer food, String indicatorCategoryId ) {
        return new ArrayList<IndicatorInstanceResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选指标
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorInstance(Integer pageNo, Integer pageSize, String appId, Integer core, Integer food, String indicatorCategoryId ) {
        return new String();
    }
}