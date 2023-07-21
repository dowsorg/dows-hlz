package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.IndicatorInstanceException;
import org.dows.hep.api.exception.IndicatorViewBaseInfoException;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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

    @Value("${redisson.lock.lease-time.teacher.indicator-batch-update-core-food:5000}")
    private Integer leaseTimeIndicatorBatchUpdateCoreFood;
    private final String indicatorInstanceFieldPid = "pid";
    private final String indicatorInstanceFieldAppId = "appId";
    private final IdGenerator idGenerator;
    private final RedissonClient redissonClient;
    private final IndicatorInstanceService indicatorInstanceService;
    private final IndicatorCategoryRefService indicatorCategoryRefService;
    private final IndicatorRuleService indicatorRuleService;
    private final IndicatorCategoryService indicatorCategoryService;
    private final IndicatorExpressionBiz indicatorExpressionBiz;
    private final RsIndicatorInstanceBiz rsIndicatorInstanceBiz;
    private final IndicatorExpressionInfluenceService indicatorExpressionInfluenceService;
    private final RsCalculateBiz rsCalculateBiz;

    public static IndicatorInstanceResponseRs indicatorInstance2ResponseRs(
        IndicatorInstanceEntity indicatorInstanceEntity,
        String def,
        String min,
        String max,
        Integer seq,
        List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList) {
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
            .displayByPercent(indicatorInstanceEntity.getDisplayByPercent())
            .unit(indicatorInstanceEntity.getUnit())
            .core(indicatorInstanceEntity.getCore())
            .food(indicatorInstanceEntity.getFood())
            .type(indicatorInstanceEntity.getType())
            .expression(indicatorInstanceEntity.getExpression())
            .rawExpression(indicatorInstanceEntity.getRawExpression())
            .descr(indicatorInstanceEntity.getDescr())
            .dt(indicatorInstanceEntity.getDt())
            .indicatorExpressionResponseRsList(indicatorExpressionResponseRsList)
            .def(def)
            .min(min)
            .max(max)
            .seq(seq)
            .build();
    }

    public static IndicatorInstanceCategoryResponseRs indicatorInstanceCategoryResponseRs(
        IndicatorCategoryEntity indicatorCategoryEntity,
        List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList
        ) {
        if (Objects.isNull(indicatorCategoryEntity)) {
            return null;
        }
        return IndicatorInstanceCategoryResponseRs
            .builder()
            .id(indicatorCategoryEntity.getId())
            .indicatorCategoryId(indicatorCategoryEntity.getIndicatorCategoryId())
            .appId(indicatorCategoryEntity.getAppId())
            .pid(indicatorCategoryEntity.getPid())
            .categoryName(indicatorCategoryEntity.getCategoryName())
            .seq(indicatorCategoryEntity.getSeq())
            .dt(indicatorCategoryEntity.getDt())
            .indicatorInstanceResponseRsList(indicatorInstanceResponseRsList)
            .build();
    }
    public void populateKIndicatorInstanceIdVSeqMap(String appId, Set<String> indicatorInstanceIdSet, Map<String, Integer> kIndicatorInstanceIdVSeqMap) {
        if (Objects.isNull(kIndicatorInstanceIdVSeqMap)) {
            log.warn("method IndicatorInstanceBiz.populateKIndicatorInstanceIdVSeqMap param kIndicatorInstanceIdVSeqMap is null");
            return;
        }
        if (Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()) {
            return;
        }
        indicatorCategoryRefService.lambdaQuery()
            .eq(IndicatorCategoryRefEntity::getAppId, appId)
            .in(IndicatorCategoryRefEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
            .list()
            .forEach(indicatorCategoryRefEntity -> kIndicatorInstanceIdVSeqMap.put(indicatorCategoryRefEntity.getIndicatorInstanceId(), indicatorCategoryRefEntity.getSeq()));
    }

    public void populateKIndicatorInstanceIdVIndicatorRuleMap(String appId, Set<String> indicatorInstanceIdSet, Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleMap) {
        if (Objects.isNull(kIndicatorInstanceIdVIndicatorRuleMap)) {
            log.warn("method IndicatorInstanceBiz.populateKIndicatorInstanceIdVDefMap param kIndicatorInstanceIdVIndicatorRuleMap is null");
            return;
        }
        if (Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()) {
            return;
        }
        indicatorRuleService.lambdaQuery()
            .eq(IndicatorRuleEntity::getAppId, appId)
            .in(IndicatorRuleEntity::getVariableId, indicatorInstanceIdSet)
            .list()
            .forEach(indicatorRuleEntity -> kIndicatorInstanceIdVIndicatorRuleMap.put(indicatorRuleEntity.getVariableId(), indicatorRuleEntity));
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateRs(CreateOrUpdateIndicatorInstanceRequestRs createOrUpdateIndicatorInstanceRequestRs) throws InterruptedException {
        String indicatorInstanceId = createOrUpdateIndicatorInstanceRequestRs.getIndicatorInstanceId();
        String indicatorCategoryId = createOrUpdateIndicatorInstanceRequestRs.getIndicatorCategoryId();
        Integer type = createOrUpdateIndicatorInstanceRequestRs.getType();
        String appId = createOrUpdateIndicatorInstanceRequestRs.getAppId();
        indicatorCategoryService.lambdaQuery()
            .eq(IndicatorCategoryEntity::getAppId, appId)
            .eq(IndicatorCategoryEntity::getIndicatorCategoryId, indicatorCategoryId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method createOrUpdateRs param createOrUpdateIndicatorInstanceRequestRs indicatorCategoryId：{} is illegal", indicatorCategoryId);
                return new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
            });
        String indicatorName = createOrUpdateIndicatorInstanceRequestRs.getIndicatorName();
        Integer displayByPercent = createOrUpdateIndicatorInstanceRequestRs.getDisplayByPercent();
        String unit = createOrUpdateIndicatorInstanceRequestRs.getUnit();
        Integer core = createOrUpdateIndicatorInstanceRequestRs.getCore();
        Integer food = createOrUpdateIndicatorInstanceRequestRs.getFood();
        String min = createOrUpdateIndicatorInstanceRequestRs.getMin();
        String max = createOrUpdateIndicatorInstanceRequestRs.getMax();
        String def = createOrUpdateIndicatorInstanceRequestRs.getDef();
        IndicatorInstanceEntity indicatorInstanceEntity = null;
        IndicatorCategoryRefEntity indicatorCategoryRefEntity = null;
        IndicatorRuleEntity indicatorRuleEntity = null;
        IndicatorExpressionInfluenceEntity indicatorExpressionInfluenceEntity = null;
        if (StringUtils.isBlank(indicatorInstanceId)) {
            indicatorInstanceId = idGenerator.nextIdStr();
            indicatorInstanceEntity = IndicatorInstanceEntity
                .builder()
                .indicatorInstanceId(indicatorInstanceId)
                .appId(appId)
                .indicatorCategoryId(indicatorCategoryId)
                .indicatorName(indicatorName)
                .displayByPercent(displayByPercent)
                .unit(unit)
                .core(core)
                .food(food)
                .type(type)
                .build();
            AtomicInteger seqAtomicInteger = new AtomicInteger(1);
            indicatorCategoryRefService.lambdaQuery()
                .eq(IndicatorCategoryRefEntity::getIndicatorCategoryId, indicatorCategoryId)
                .orderByDesc(IndicatorCategoryRefEntity::getSeq)
                .last(EnumString.LIMIT_1.getStr())
                .oneOpt()
                .ifPresent(indicatorCategoryRefEntity1 -> seqAtomicInteger.set(indicatorCategoryRefEntity1.getSeq() + 1));
            indicatorCategoryRefEntity = IndicatorCategoryRefEntity
                .builder()
                .indicatorCategoryRefId(idGenerator.nextIdStr())
                .appId(appId)
                .indicatorCategoryId(indicatorCategoryId)
                .indicatorInstanceId(indicatorInstanceId)
                .seq(seqAtomicInteger.get())
                .build();
            indicatorRuleEntity = IndicatorRuleEntity
                .builder()
                .indicatorRuleId(idGenerator.nextIdStr())
                .appId(appId)
                .variableId(indicatorInstanceId)
                .ruleType(EnumIndicatorRuleType.INDICATOR.getCode())
                .min(min)
                .max(max)
                .def(def)
                .build();
            indicatorExpressionInfluenceEntity = IndicatorExpressionInfluenceEntity
                .builder()
                .indicatorExpressionInfluenceId(idGenerator.nextIdStr())
                .appId(appId)
                .indicatorInstanceId(indicatorInstanceId)
                .build();
        } else {
            String finalIndicatorInstanceId = indicatorInstanceId;
            indicatorInstanceEntity = indicatorInstanceService.lambdaQuery()
                .eq(IndicatorInstanceEntity::getAppId, appId)
                .eq(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method createOrUpdateRs param createOrUpdateIndicatorInstanceRequestRs indicatorInstanceId:{} is illegal", finalIndicatorInstanceId);
                    throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
                });
            indicatorInstanceEntity.setIndicatorName(indicatorName);
            indicatorInstanceEntity.setDisplayByPercent(displayByPercent);
            indicatorInstanceEntity.setUnit(unit);
            indicatorInstanceEntity.setCore(core);
            indicatorInstanceEntity.setFood(food);
            indicatorRuleEntity = indicatorRuleService.lambdaQuery()
                .eq(IndicatorRuleEntity::getAppId, appId)
                .eq(IndicatorRuleEntity::getVariableId, finalIndicatorInstanceId)
                .oneOpt()
                .orElseThrow(() -> {
                    log.warn("method createOrUpdateRs param createOrUpdateIndicatorInstanceRequestRs indicatorInstanceId:{} is illegal, do not have indicator rule", finalIndicatorInstanceId);
                    return new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
                });
            indicatorRuleEntity.setMin(min);
            indicatorRuleEntity.setMax(max);
            indicatorRuleEntity.setDef(def);
        }
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_INSTANCE_CREATE_DELETE_UPDATE, indicatorInstanceFieldPid, indicatorCategoryId));
        boolean isLocked = lock.tryLock(leaseTimeIndicatorInstanceCreateDeleteUpdate, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorInstanceException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_INSTANCE_LATER);
        }
        try {
            if (Objects.nonNull(indicatorExpressionInfluenceEntity)) {indicatorExpressionInfluenceService.saveOrUpdate(indicatorExpressionInfluenceEntity);}
            indicatorInstanceService.saveOrUpdate(indicatorInstanceEntity);
            indicatorCategoryRefService.saveOrUpdate(indicatorCategoryRefEntity);
            indicatorRuleService.saveOrUpdate(indicatorRuleEntity);
            /* runsix:重新计算健康指数 */
            rsCalculateBiz.databaseRsCalculateHealthScore(DatabaseRsCalculateHealthScoreRequestRs
                .builder()
                .appId(EnumString.APP_ID.getStr())
                .build());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * runsix method process
     * TODO 删除的引用关系需要确认
     * 1.delete IndicatorInstance
     * 2.delete IndicatorCategoryRef
     * 3.delete IndicatorRule
    */
    /* runsix: TODO 删除指标公式相关 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String indicatorInstanceId) throws InterruptedException, ExecutionException {
        IndicatorInstanceEntity indicatorInstanceEntity = indicatorInstanceService.lambdaQuery()
            .eq(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("方法deleteIndicatorInstance的参数indicatorInstanceId：{}，不存在", indicatorInstanceId);
                throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
            });
        EnumIndicatorType enumIndicatorType = EnumIndicatorType.kTypeVEnumIndicatorTypeMap.get(indicatorInstanceEntity.getType());
        if (Objects.nonNull(enumIndicatorType) && !EnumIndicatorType.USER_CREATED.getType().equals(enumIndicatorType.getType())) {
            throw new IndicatorInstanceException(EnumESC.SYSTEM_INDICATOR_INSTANCE_CANNOT_DELETE);
        }
        String appId = indicatorInstanceEntity.getAppId();

        /* runsix:删除检查 */
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        indicatorInstanceIdSet.add(indicatorInstanceId);
        rsIndicatorInstanceBiz.checkIndicatorInstanceDelete(appId, indicatorInstanceIdSet);

        String indicatorCategoryId = indicatorInstanceEntity.getIndicatorCategoryId();
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
            AtomicInteger atomicInteger = new AtomicInteger(1);
            List<IndicatorCategoryRefEntity> indicatorCategoryRefEntityList = indicatorCategoryRefService.lambdaQuery()
                .eq(IndicatorCategoryRefEntity::getAppId, appId)
                .eq(IndicatorCategoryRefEntity::getIndicatorCategoryId, indicatorCategoryId)
                .orderByAsc(IndicatorCategoryRefEntity::getSeq)
                .list()
                .stream()
                .peek(indicatorCategoryRefEntity1 -> {
                    indicatorCategoryRefEntity1.setSeq(atomicInteger.getAndIncrement());
                })
                .collect(Collectors.toList());
            indicatorCategoryRefService.saveOrUpdateBatch(indicatorCategoryRefEntityList);
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


    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateCore(BatchUpdateCoreRequestRs batchUpdateCoreRequestRs) throws InterruptedException {
        String appId = batchUpdateCoreRequestRs.getAppId();
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_BATCH_UPDATE_CORE_FOOD, indicatorInstanceFieldAppId, appId));
        boolean isLocked = lock.tryLock(leaseTimeIndicatorBatchUpdateCoreFood, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
        }
        try {
            List<IndicatorInstanceEntity> indicatorInstanceEntityList = new ArrayList<>();
            List<String> dbIndicatorInstanceIdList = new ArrayList<>();
            List<String> paramIndicatorInstanceIdList = batchUpdateCoreRequestRs.getIndicatorInstanceIdList();
            indicatorInstanceService.lambdaQuery()
                .eq(IndicatorInstanceEntity::getAppId, appId)
                .list()
                .forEach(indicatorInstanceEntity -> {
                    indicatorInstanceEntityList.add(indicatorInstanceEntity);
                    dbIndicatorInstanceIdList.add(indicatorInstanceEntity.getIndicatorInstanceId());
                });
            if (
                paramIndicatorInstanceIdList.stream().anyMatch(indicatorInstanceId -> !dbIndicatorInstanceIdList.contains(indicatorInstanceId))
            ) {
                log.warn("method IndicatorInstanceBiz.batchUpdateCore param batchUpdateCoreRequestRs paramIndicatorInstanceIdList:{} is illegal", paramIndicatorInstanceIdList);
            }
            indicatorInstanceEntityList.forEach(indicatorInstanceEntity -> {
                String indicatorInstanceId = indicatorInstanceEntity.getIndicatorInstanceId();
                if (paramIndicatorInstanceIdList.contains(indicatorInstanceId)) {
                    indicatorInstanceEntity.setCore(EnumStatus.ENABLE.getCode());
                } else {
                    indicatorInstanceEntity.setCore(EnumStatus.DISABLE.getCode());
                }
            });
            indicatorInstanceService.saveOrUpdateBatch(indicatorInstanceEntityList);
        } finally {
            lock.unlock();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateFood(BatchUpdateFoodRequestRs batchUpdateFoodRequestRs) throws InterruptedException {
        String appId = batchUpdateFoodRequestRs.getAppId();
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_BATCH_UPDATE_CORE_FOOD, indicatorInstanceFieldAppId, appId));
        boolean isLocked = lock.tryLock(leaseTimeIndicatorBatchUpdateCoreFood, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorViewBaseInfoException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_VIEW_BASE_INFO_LATER);
        }
        try {
            List<IndicatorInstanceEntity> indicatorInstanceEntityList = new ArrayList<>();
            List<String> dbIndicatorInstanceIdList = new ArrayList<>();
            List<String> paramIndicatorInstanceIdList = batchUpdateFoodRequestRs.getIndicatorInstanceIdList();
            indicatorInstanceService.lambdaQuery()
                .eq(IndicatorInstanceEntity::getAppId, appId)
                .list()
                .forEach(indicatorInstanceEntity -> {
                    indicatorInstanceEntityList.add(indicatorInstanceEntity);
                    dbIndicatorInstanceIdList.add(indicatorInstanceEntity.getIndicatorInstanceId());
                });
            if (
                paramIndicatorInstanceIdList.stream().anyMatch(indicatorInstanceId -> !dbIndicatorInstanceIdList.contains(indicatorInstanceId))
            ) {
                log.warn("method IndicatorInstanceBiz.batchUpdateFood param batchUpdateCoreRequestRs paramIndicatorInstanceIdList:{} is illegal", paramIndicatorInstanceIdList);
            }
            indicatorInstanceEntityList.forEach(indicatorInstanceEntity -> {
                String indicatorInstanceId = indicatorInstanceEntity.getIndicatorInstanceId();
                if (paramIndicatorInstanceIdList.contains(indicatorInstanceId)) {
                    indicatorInstanceEntity.setFood(EnumStatus.ENABLE.getCode());
                } else {
                    indicatorInstanceEntity.setFood(EnumStatus.DISABLE.getCode());
                }
            });
            indicatorInstanceService.saveOrUpdateBatch(indicatorInstanceEntityList);
        } finally {
            lock.unlock();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void move(UpdateIndicatorInstanceMoveRequestRs updateIndicatorInstanceMoveRequestRs) throws InterruptedException {
        String indicatorInstanceId = updateIndicatorInstanceMoveRequestRs.getIndicatorInstanceId();
        Integer up = updateIndicatorInstanceMoveRequestRs.getUp();
        if (!EnumStatus.ENABLE.getCode().equals(up) && !EnumStatus.DISABLE.getCode().equals(up)) {
            log.warn("method move param updateIndicatorInstanceMoveRequestRs up:{} is illegal", up);
            throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
        }
        IndicatorInstanceEntity indicatorInstanceEntity = indicatorInstanceService.lambdaQuery()
            .eq(IndicatorInstanceEntity::getIndicatorInstanceId, indicatorInstanceId)
            .oneOpt()
            .orElseThrow(() -> {
                log.warn("method move param updateIndicatorInstanceMoveRequestRs indicatorInstanceId:{} is illegal", indicatorInstanceId);
                throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
            });
        String appId = indicatorInstanceEntity.getAppId();
        String indicatorCategoryId = indicatorInstanceEntity.getIndicatorCategoryId();
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_INSTANCE_CREATE_DELETE_UPDATE, indicatorInstanceFieldPid, indicatorCategoryId));
        boolean isLocked = lock.tryLock(leaseTimeIndicatorInstanceCreateDeleteUpdate, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorInstanceException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_INSTANCE_LATER);
        }
        try {
            IndicatorCategoryRefEntity currentIndicatorCategoryRefEntity = null;
            IndicatorCategoryRefEntity anotherIndicatorCategoryRefEntity = null;
            List<IndicatorCategoryRefEntity> indicatorCategoryRefEntityList = new ArrayList<>();
            Map<String, IndicatorCategoryRefEntity> kIndicatorInstanceIdVIndicatorCategoryRefMap = new HashMap<>();
            Map<Integer, IndicatorCategoryRefEntity> kSeqVIndicatorCategoryRefMap = new HashMap<>();
            AtomicReference<Integer> maxSeqAR = new AtomicReference<>(0);
            AtomicReference<Integer> minSeqAR = new AtomicReference<>(0);
            indicatorCategoryRefService.lambdaQuery()
                .eq(IndicatorCategoryRefEntity::getIndicatorCategoryId, indicatorCategoryId)
                .list()
                .forEach(indicatorCategoryRefEntity -> {
                    String indicatorInstanceId1 = indicatorCategoryRefEntity.getIndicatorInstanceId();
                    Integer seq1 = indicatorCategoryRefEntity.getSeq();
                    if (seq1 < minSeqAR.get()) {
                        minSeqAR.set(seq1);
                    }
                    if (seq1 > maxSeqAR.get()) {
                        maxSeqAR.set(seq1);
                    }
                    kIndicatorInstanceIdVIndicatorCategoryRefMap.put(indicatorInstanceId1, indicatorCategoryRefEntity);
                    kSeqVIndicatorCategoryRefMap.put(seq1, indicatorCategoryRefEntity);
                });
            currentIndicatorCategoryRefEntity = kIndicatorInstanceIdVIndicatorCategoryRefMap.get(indicatorInstanceId);
            Integer currentSeq = currentIndicatorCategoryRefEntity.getSeq();
            if (EnumStatus.DISABLE.getCode().equals(up)) {
                if (currentSeq >= maxSeqAR.get()) {
                    log.warn("method move param updateIndicatorInstanceMoveRequestRs last one cannot move down");
                    throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
                }
                Integer nextSeq = currentSeq + 1;
                currentIndicatorCategoryRefEntity.setSeq(nextSeq);
                anotherIndicatorCategoryRefEntity = kSeqVIndicatorCategoryRefMap.get(nextSeq);
                anotherIndicatorCategoryRefEntity.setSeq(currentSeq);
            } else {
                if (currentSeq <= 1) {
                    log.warn("method move param updateIndicatorInstanceMoveRequestRs first one cannot move up");
                    throw new IndicatorInstanceException(EnumESC.VALIDATE_EXCEPTION);
                }
                Integer previousSeq = currentSeq - 1;
                currentIndicatorCategoryRefEntity.setSeq(previousSeq);
                anotherIndicatorCategoryRefEntity = kSeqVIndicatorCategoryRefMap.get(previousSeq);
                anotherIndicatorCategoryRefEntity.setSeq(currentSeq);
            }
            indicatorCategoryRefEntityList.add(currentIndicatorCategoryRefEntity);
            indicatorCategoryRefEntityList.add(anotherIndicatorCategoryRefEntity);
            indicatorCategoryRefService.saveOrUpdateBatch(indicatorCategoryRefEntityList);
        } finally {
            lock.unlock();
        }
    }

    public List<IndicatorInstanceCategoryResponseRs> getByAppId(String appId) {
        Set<String> indicatorCategoryIdSet = new HashSet<>();
        List<IndicatorCategoryEntity> indicatorCategoryEntityList = indicatorCategoryService.lambdaQuery()
            .eq(IndicatorCategoryEntity::getAppId, appId)
            .eq(IndicatorCategoryEntity::getPid, EnumIndicatorCategory.INDICATOR_MANAGEMENT.getCode())
            .list()
            .stream()
            .peek(indicatorCategoryEntity -> indicatorCategoryIdSet.add(indicatorCategoryEntity.getIndicatorCategoryId()))
            .collect(Collectors.toList());
        Map<String, List<IndicatorInstanceEntity>> kIndicatorCategoryIdVIndicatorInstanceListMap = new HashMap<>();
        if (indicatorCategoryIdSet.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        indicatorInstanceService.lambdaQuery()
            .eq(IndicatorInstanceEntity::getAppId, appId)
            .in(IndicatorInstanceEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
            .list()
            .forEach(indicatorInstanceEntity -> {
                String indicatorCategoryId = indicatorInstanceEntity.getIndicatorCategoryId();
                List<IndicatorInstanceEntity> indicatorInstanceEntityList = kIndicatorCategoryIdVIndicatorInstanceListMap.get(indicatorCategoryId);
                if (Objects.isNull(indicatorInstanceEntityList)) {
                    indicatorInstanceEntityList = new ArrayList<>();
                }
                indicatorInstanceIdSet.add(indicatorInstanceEntity.getIndicatorInstanceId());
                indicatorInstanceEntityList.add(indicatorInstanceEntity);
                kIndicatorCategoryIdVIndicatorInstanceListMap.put(indicatorCategoryId, indicatorInstanceEntityList);
            });
        Map<String, List<IndicatorExpressionResponseRs>> kReasonIdVIndicatorExpressionResponseRsListMap = new HashMap<>();
        indicatorExpressionBiz.populateKReasonIdVIndicatorExpressionResponseRsListMap(appId, indicatorInstanceIdSet, kReasonIdVIndicatorExpressionResponseRsListMap);
        Map<String, IndicatorRuleEntity> kIndicatorInstanceIdVIndicatorRuleMap = new HashMap<>();
        populateKIndicatorInstanceIdVIndicatorRuleMap(appId, indicatorInstanceIdSet, kIndicatorInstanceIdVIndicatorRuleMap);
        Map<String, Integer> kIndicatorInstanceIdVSeqMap = new HashMap<>();
        populateKIndicatorInstanceIdVSeqMap(appId, indicatorInstanceIdSet, kIndicatorInstanceIdVSeqMap);
        return indicatorCategoryEntityList
            .stream()
            .map(indicatorCategoryEntity -> {
                String indicatorCategoryId = indicatorCategoryEntity.getIndicatorCategoryId();
                List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList = new ArrayList<>();
                List<IndicatorInstanceEntity> indicatorInstanceEntityList = kIndicatorCategoryIdVIndicatorInstanceListMap.get(indicatorCategoryId);
                if (Objects.nonNull(indicatorInstanceEntityList)) {
                    indicatorInstanceResponseRsList = indicatorInstanceEntityList.stream()
                        .map(indicatorInstanceEntity -> {
                            String indicatorInstanceId = indicatorInstanceEntity.getIndicatorInstanceId();
                            List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList = kReasonIdVIndicatorExpressionResponseRsListMap.get(indicatorInstanceId);
                            String def = null;
                            String min = null;
                            String max = null;
                            IndicatorRuleEntity indicatorRuleEntity = kIndicatorInstanceIdVIndicatorRuleMap.get(indicatorInstanceId);
                            if (Objects.nonNull(indicatorRuleEntity)) {
                                def = indicatorRuleEntity.getDef();
                                min = indicatorRuleEntity.getMin();
                                max = indicatorRuleEntity.getMax();
                            }
                            Integer seq = kIndicatorInstanceIdVSeqMap.get(indicatorInstanceId);
                            return IndicatorInstanceBiz.indicatorInstance2ResponseRs(indicatorInstanceEntity, def, min, max, seq, indicatorExpressionResponseRsList);
                        })
                        .sorted(Comparator.comparingInt(IndicatorInstanceResponseRs::getSeq))
                        .collect(Collectors.toList());
                }
                return IndicatorInstanceBiz.indicatorInstanceCategoryResponseRs(
                    indicatorCategoryEntity, indicatorInstanceResponseRsList
                );
            }).collect(Collectors.toList());
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