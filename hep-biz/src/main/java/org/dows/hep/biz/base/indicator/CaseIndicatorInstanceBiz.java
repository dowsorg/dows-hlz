package org.dows.hep.biz.base.indicator;

import cn.hutool.core.lang.Assert;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.request.CaseCreateCopyToPersonRequestRs;
import org.dows.hep.api.base.indicator.request.CaseRsCalculateHealthScoreRequestRs;
import org.dows.hep.api.base.indicator.request.CreateOrUpdateCaseIndicatorInstanceRequestRs;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.exception.CaseIndicatorInstanceException;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.exception.IndicatorInstanceException;
import org.dows.hep.api.exception.RsCaseIndicatorInstanceBizException;
import org.dows.hep.api.tenant.casus.request.UpdateIndicatorValueRequest;
import org.dows.hep.biz.eval.EvalCaseHealthIndexBiz;
import org.dows.hep.biz.extend.uim.XAccountInstanceApi;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
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
 * @author runsix
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseIndicatorInstanceBiz {
    @Value("${redisson.lock.lease-time.teacher.case-indicator-instance-create-delete-update:5000}")
    private Integer leaseTimeCaseIndicatorInstanceCreateDeleteUpdate;

    private final String caseIndicatorInstanceFieldPid = "pid";
    private final IdGenerator idGenerator;
    private final IndicatorInstanceBiz indicatorInstanceBiz;
    private final IndicatorExpressionInfluenceService indicatorExpressionInfluenceService;
    private final CaseIndicatorExpressionBiz caseIndicatorExpressionBiz;
    private final CaseIndicatorCategoryService caseIndicatorCategoryService;
    private final CaseIndicatorCategoryRefService caseIndicatorCategoryRefService;
    private final CaseIndicatorRuleService caseIndicatorRuleService;
    private final CaseIndicatorInstanceService caseIndicatorInstanceService;
    private final CaseIndicatorExpressionService caseIndicatorExpressionService;
    private final CaseIndicatorExpressionItemService caseIndicatorExpressionItemService;
    private final CaseIndicatorExpressionRefService caseIndicatorExpressionRefService;
    private final CaseIndicatorExpressionInfluenceService caseIndicatorExpressionInfluenceService;
    private final CaseIndicatorCategoryPrincipalRefService caseIndicatorCategoryPrincipalRefService;
    private final CasePersonService casePersonService;
    private final RsUtilBiz rsUtilBiz;
    private final RsCaseIndicatorInstanceBiz rsCaseIndicatorInstanceBiz;
    private final RedissonClient redissonClient;
    private final RsCaseCalculateBiz rsCaseCalculateBiz;

    private final EvalCaseHealthIndexBiz evalCaseHealthIndexBiz;

    private final XAccountInstanceApi xAccountInstanceApi;
    public static CaseIndicatorInstanceResponseRs caseIndicatorInstance2ResponseRs(
            CaseIndicatorInstanceEntity caseIndicatorInstanceEntity,
            List<CaseIndicatorExpressionResponseRs> caseIndicatorExpressionResponseRsList,
            String def,
            String min,
            String max,
            Integer seq
    ) {
        if (Objects.isNull(caseIndicatorInstanceEntity)) {
            return null;
        }
        if (Objects.isNull(caseIndicatorExpressionResponseRsList)) {
            caseIndicatorExpressionResponseRsList = new ArrayList<>();
        }
        return CaseIndicatorInstanceResponseRs
                .builder()
                .id(caseIndicatorInstanceEntity.getId())
                .indicatorInstanceId(caseIndicatorInstanceEntity.getCaseIndicatorInstanceId())
                .appId(caseIndicatorInstanceEntity.getAppId())
                .principal(caseIndicatorInstanceEntity.getPrincipalId())
                .indicatorCategoryId(caseIndicatorInstanceEntity.getIndicatorCategoryId())
                .indicatorName(caseIndicatorInstanceEntity.getIndicatorName())
                .displayByPercent(caseIndicatorInstanceEntity.getDisplayByPercent())
                .unit(caseIndicatorInstanceEntity.getUnit())
                .core(caseIndicatorInstanceEntity.getCore())
                .food(caseIndicatorInstanceEntity.getFood())
                .descr(caseIndicatorInstanceEntity.getDescr())
                .dt(caseIndicatorInstanceEntity.getDt())
                .caseIndicatorExpressionResponseRsList(caseIndicatorExpressionResponseRsList)
                .def(def)
                .min(min)
                .max(max)
                .seq(seq)
                .build();
    }

    public void populateKCaseIndicatorInstanceIdVIndicatorRuleMap(String appId, Set<String> caseIndicatorInstanceIdSet, Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleMap) {
        if (Objects.isNull(kCaseIndicatorInstanceIdVCaseIndicatorRuleMap)) {
            log.warn("method CaseIndicatorInstanceBiz.populateKIndicatorInstanceIdVIndicatorRuleMap param kCaseIndicatorInstanceIdVCaseIndicatorRuleMap is null");
            return;
        }
        if (Objects.isNull(caseIndicatorInstanceIdSet) || caseIndicatorInstanceIdSet.isEmpty()) {
            return;
        }
        caseIndicatorRuleService.lambdaQuery()
                .eq(CaseIndicatorRuleEntity::getAppId, appId)
                .in(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceIdSet)
                .list()
                .forEach(caseIndicatorRuleEntity -> kCaseIndicatorInstanceIdVCaseIndicatorRuleMap.put(caseIndicatorRuleEntity.getVariableId(), caseIndicatorRuleEntity));
    }

    public void populateKCaseIndicatorInstanceIdVSeqMap(String appId, Set<String> indicatorInstanceIdSet, Map<String, Integer> kCaseIndicatorInstanceIdVSeqMap) {
        if (Objects.isNull(kCaseIndicatorInstanceIdVSeqMap)) {
            log.warn("method CaseIndicatorInstanceBiz.populateKCaseIndicatorInstanceIdVSeqMap param kCaseIndicatorInstanceIdVSeqMap is null");
            return;
        }
        if (Objects.isNull(indicatorInstanceIdSet) || indicatorInstanceIdSet.isEmpty()) {
            return;
        }
        caseIndicatorCategoryRefService.lambdaQuery()
                .eq(CaseIndicatorCategoryRefEntity::getAppId, appId)
                .in(CaseIndicatorCategoryRefEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
                .list()
                .forEach(indicatorCategoryRefEntity -> kCaseIndicatorInstanceIdVSeqMap.put(indicatorCategoryRefEntity.getIndicatorInstanceId(), indicatorCategoryRefEntity.getSeq()));
    }


    public static String convertConditionValList2Case(
            Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
            String conditionValList) {
        if (StringUtils.isBlank(conditionValList)) {
            return null;
        }
        if (Objects.isNull(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap)) {
            return null;
        }
        List<String> caseIndicatorInstanceIdList = Arrays.stream(conditionValList.split(EnumString.COMMA.getStr()))
                .map(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return String.join(EnumString.COMMA.getStr(), caseIndicatorInstanceIdList);
    }

    public static String convertResultValList2Case(
            Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap,
            String resultValList) {
        if (StringUtils.isBlank(resultValList)) {
            return null;
        }
        if (Objects.isNull(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap)) {
            return null;
        }
        List<String> caseIndicatorInstanceIdList = Arrays.stream(resultValList.split(EnumString.COMMA.getStr()))
                .map(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return String.join(EnumString.COMMA.getStr(), caseIndicatorInstanceIdList);
    }

    public void populateInfluenceMap(
            Map<String, Set<String>> kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap,
            Map<String, Set<String>> kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap,
            String indicatorInstanceId, String conditionValList, String resultValList
    ) {
        Set<String> influenceIndicatorInstanceIdSet = kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.get(indicatorInstanceId);
        if (Objects.isNull(influenceIndicatorInstanceIdSet)) {
            influenceIndicatorInstanceIdSet = new HashSet<>();
        }
        Set<String> influencedIndicatorInstanceIdSet = kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.get(indicatorInstanceId);
        if (Objects.isNull(influencedIndicatorInstanceIdSet)) {
            influencedIndicatorInstanceIdSet = new HashSet<>();
        }
        if (StringUtils.isNotBlank(conditionValList)) {
            influencedIndicatorInstanceIdSet.addAll(Arrays.stream(conditionValList.split(EnumString.COMMA.getStr())).collect(Collectors.toSet()));
        }
        if (StringUtils.isNotBlank(resultValList)) {
            influenceIndicatorInstanceIdSet.addAll(Arrays.stream(resultValList.split(EnumString.COMMA.getStr())).collect(Collectors.toSet()));
        }
        kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.put(indicatorInstanceId, influenceIndicatorInstanceIdSet);
        kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put(indicatorInstanceId, influencedIndicatorInstanceIdSet);
    }

    @Transactional(rollbackFor = Exception.class)
    public void copyPersonIndicatorInstance(CaseCreateCopyToPersonRequestRs caseCreateCopyToPersonRequestRs) throws ExecutionException, InterruptedException {
        /* runsix:param */
        String appId = caseCreateCopyToPersonRequestRs.getAppId();
        String principalId = caseCreateCopyToPersonRequestRs.getPrincipalId();
        /* runsix:result */
        List<IndicatorInstanceCategoryResponseRs> indicatorInstanceCategoryResponseRsList = indicatorInstanceBiz.getByAppId(appId);
        List<CaseIndicatorCategoryEntity> caseIndicatorCategoryEntityList = new ArrayList<>();
        List<CaseIndicatorCategoryRefEntity> caseIndicatorCategoryRefEntityList = new ArrayList<>();
        List<CaseIndicatorCategoryPrincipalRefEntity> caseIndicatorCategoryPrincipalRefEntityList = new ArrayList<>();
        List<CaseIndicatorRuleEntity> caseIndicatorRuleEntityList = new ArrayList<>();
        List<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityList = new ArrayList<>();
        List<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefEntityList = new ArrayList<>();
        List<CaseIndicatorExpressionEntity> caseIndicatorExpressionEntityList = new ArrayList<>();
        List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = new ArrayList<>();
        List<CaseIndicatorExpressionInfluenceEntity> caseIndicatorExpressionInfluenceEntityList = new ArrayList<>();

        Set<String> allOldIdSet = new HashSet<>();
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        if (Objects.isNull(indicatorInstanceCategoryResponseRsList) || indicatorInstanceCategoryResponseRsList.isEmpty()) {
            log.error("CaseIndicatorInstanceBiz.v2CopyPersonIndicatorInstance indicatorInstanceCategoryResponseRsList is empty do not copy");
            return;
        }
        indicatorInstanceCategoryResponseRsList.forEach(indicatorInstanceCategoryResponseRs -> {
            allOldIdSet.add(indicatorInstanceCategoryResponseRs.getIndicatorCategoryId());
            List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList = indicatorInstanceCategoryResponseRs.getIndicatorInstanceResponseRsList();
            if (Objects.isNull(indicatorInstanceResponseRsList) || indicatorInstanceResponseRsList.isEmpty()) {return;}
            indicatorInstanceResponseRsList.forEach(indicatorInstanceResponseRs -> {
                allOldIdSet.add(indicatorInstanceResponseRs.getIndicatorInstanceId());
                indicatorInstanceIdSet.add(indicatorInstanceResponseRs.getIndicatorInstanceId());
                List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList = indicatorInstanceResponseRs.getIndicatorExpressionResponseRsList();
                if (Objects.isNull(indicatorExpressionResponseRsList) || indicatorExpressionResponseRsList.isEmpty()) {return;}
                indicatorExpressionResponseRsList.forEach(indicatorExpressionResponseRs -> {
                    allOldIdSet.add(indicatorExpressionResponseRs.getIndicatorExpressionId());
                    allOldIdSet.add(indicatorExpressionResponseRs.getIndicatorExpressionRefId());
                    List<IndicatorExpressionItemResponseRs> indicatorExpressionItemResponseRsList = indicatorExpressionResponseRs.getIndicatorExpressionItemResponseRsList();
                    if (Objects.nonNull(indicatorExpressionItemResponseRsList) && !indicatorExpressionItemResponseRsList.isEmpty()) {
                        indicatorExpressionItemResponseRsList.forEach(indicatorExpressionItemResponseRs -> {
                            allOldIdSet.add(indicatorExpressionItemResponseRs.getIndicatorExpressionItemId());
                        });
                    }
                    IndicatorExpressionItemResponseRs minIndicatorExpressionItemResponseRs = indicatorExpressionResponseRs.getMinIndicatorExpressionItemResponseRs();
                    if (Objects.nonNull(minIndicatorExpressionItemResponseRs)) {
                        allOldIdSet.add(minIndicatorExpressionItemResponseRs.getIndicatorExpressionItemId());
                    }
                    IndicatorExpressionItemResponseRs maxIndicatorExpressionItemResponseRs = indicatorExpressionResponseRs.getMaxIndicatorExpressionItemResponseRs();
                    if (Objects.nonNull(maxIndicatorExpressionItemResponseRs)) {
                        allOldIdSet.add(maxIndicatorExpressionItemResponseRs.getIndicatorExpressionItemId());
                    }
                });
            });
        });

        Map<String, String> kOldIdVNewIdMap = new HashMap<>();
        CompletableFuture<Void> cfPopulateKOldIdVNewIdMap = CompletableFuture.runAsync(() -> {
            rsUtilBiz.populateKOldIdVNewIdMap(kOldIdVNewIdMap, allOldIdSet);
        });
        cfPopulateKOldIdVNewIdMap.get();

        CompletableFuture<Void> cfPopulateCaseIndicatorCategoryList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateCaseIndicatorCategoryList(caseIndicatorCategoryEntityList, indicatorInstanceCategoryResponseRsList, kOldIdVNewIdMap);
        });
        cfPopulateCaseIndicatorCategoryList.get();

        CompletableFuture<Void> cfPopulateCaseIndicatorCategoryRefList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateCaseIndicatorCategoryRefList(caseIndicatorCategoryRefEntityList, indicatorInstanceCategoryResponseRsList, kOldIdVNewIdMap);
        });
        cfPopulateCaseIndicatorCategoryRefList.get();

        CompletableFuture<Void> cfPopulateCaseIndicatorCategoryPrincipalRefList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateCaseIndicatorCategoryPrincipalRefList(caseIndicatorCategoryPrincipalRefEntityList, indicatorInstanceCategoryResponseRsList, kOldIdVNewIdMap, principalId);
        });
        cfPopulateCaseIndicatorCategoryPrincipalRefList.get();

        CompletableFuture<Void> cfPopulateCaseIndicatorRuleEntityList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateCaseIndicatorRuleEntityList(caseIndicatorRuleEntityList, indicatorInstanceCategoryResponseRsList, kOldIdVNewIdMap);
        });
        cfPopulateCaseIndicatorRuleEntityList.get();

        CompletableFuture<Void> cfPopulateCaseIndicatorInstanceEntityList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateCaseIndicatorInstanceEntityList(caseIndicatorInstanceEntityList, indicatorInstanceCategoryResponseRsList, kOldIdVNewIdMap, principalId);
        });
        cfPopulateCaseIndicatorInstanceEntityList.get();

        CompletableFuture<Void> cfPopulateAllCaseIndicatorExpressionEntityList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateAllCaseIndicatorExpressionEntityList(caseIndicatorExpressionRefEntityList, caseIndicatorExpressionEntityList, caseIndicatorExpressionItemEntityList, indicatorInstanceCategoryResponseRsList, kOldIdVNewIdMap);
        });
        cfPopulateAllCaseIndicatorExpressionEntityList.get();

        List<IndicatorExpressionInfluenceEntity> indicatorExpressionInfluenceEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateIndicatorExpressionInfluenceEntityList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateIndicatorExpressionInfluenceEntityList(indicatorExpressionInfluenceEntityList, indicatorInstanceIdSet);
        });
        cfPopulateIndicatorExpressionInfluenceEntityList.get();
        Set<String> indicatorExpressionInfluenceSet = indicatorExpressionInfluenceEntityList.stream().map(IndicatorExpressionInfluenceEntity::getIndicatorExpressionInfluenceId).collect(Collectors.toSet());
        CompletableFuture<Void> cfPopulateCaseIndicatorExpressionInfluenceEntityList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateCaseIndicatorExpressionInfluenceEntityList(caseIndicatorExpressionInfluenceEntityList, indicatorExpressionInfluenceSet, kOldIdVNewIdMap);
        });
        cfPopulateCaseIndicatorExpressionInfluenceEntityList.get();

        /* runsix:batch save */
        caseIndicatorCategoryService.saveOrUpdateBatch(caseIndicatorCategoryEntityList);
        caseIndicatorCategoryRefService.saveOrUpdateBatch(caseIndicatorCategoryRefEntityList);
        caseIndicatorCategoryPrincipalRefService.saveOrUpdateBatch(caseIndicatorCategoryPrincipalRefEntityList);
        caseIndicatorRuleService.saveOrUpdateBatch(caseIndicatorRuleEntityList);
        caseIndicatorInstanceService.saveOrUpdateBatch(caseIndicatorInstanceEntityList);
        caseIndicatorExpressionRefService.saveOrUpdateBatch(caseIndicatorExpressionRefEntityList);
        caseIndicatorExpressionService.saveOrUpdateBatch(caseIndicatorExpressionEntityList);
        caseIndicatorExpressionItemService.saveOrUpdateBatch(caseIndicatorExpressionItemEntityList);
        caseIndicatorExpressionInfluenceService.saveOrUpdateBatch(caseIndicatorExpressionInfluenceEntityList);

    }

    @Transactional(rollbackFor = Exception.class)
    public void v1OldCopyPersonIndicatorInstance(CaseCreateCopyToPersonRequestRs caseCreateCopyToPersonRequestRs) {
        String appId = caseCreateCopyToPersonRequestRs.getAppId();
        String casePersonId = caseCreateCopyToPersonRequestRs.getPrincipalId();
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        List<IndicatorInstanceCategoryResponseRs> indicatorInstanceCategoryResponseRsList = indicatorInstanceBiz.getByAppId(appId);
        List<CaseIndicatorCategoryEntity> caseIndicatorCategoryEntityList = new ArrayList<>();
        List<CaseIndicatorCategoryRefEntity> caseIndicatorCategoryRefEntityList = new ArrayList<>();
        List<CaseIndicatorRuleEntity> caseIndicatorRuleEntityList = new ArrayList<>();
        List<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityList = new ArrayList<>();
        List<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefEntityList = new ArrayList<>();
        List<CaseIndicatorExpressionEntity> caseIndicatorExpressionEntityList = new ArrayList<>();
        List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = new ArrayList<>();
        List<CaseIndicatorCategoryPrincipalRefEntity> caseIndicatorCategoryPrincipalRefEntityList = new ArrayList<>();
        List<CaseIndicatorExpressionInfluenceEntity> caseIndicatorExpressionInfluenceEntityList = new ArrayList<>();
        Map<String, String> kIndicatorInstanceIdVCaseIndicatorInstanceIdMap = new HashMap<>();
        Map<String, Set<String>> kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap = new HashMap<>();
        Map<String, Set<String>> kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap = new HashMap<>();
        /**
         * runsix method process
         * 1.caseIndicatorInstanceEntityList
         * 2.caseIndicatorRuleEntityList
         * 3.caseIndicatorCategoryRefEntityList
         * 4.caseIndicatorCategoryEntityList
         * 5.caseIndicatorCategoryPrincipalRefEntityList
         */
        indicatorInstanceCategoryResponseRsList.forEach(indicatorInstanceCategoryResponseRs -> {
            String indicatorCategoryId = indicatorInstanceCategoryResponseRs.getIndicatorCategoryId();
            String caseIndicatorCategoryId = idGenerator.nextIdStr();
            String categoryName = indicatorInstanceCategoryResponseRs.getCategoryName();
            Integer seq = indicatorInstanceCategoryResponseRs.getSeq();
            List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList = indicatorInstanceCategoryResponseRs.getIndicatorInstanceResponseRsList();
            if (Objects.isNull(indicatorInstanceResponseRsList)) {
                return;
            }
            indicatorInstanceResponseRsList.forEach(indicatorInstanceResponseRs -> {
                String indicatorInstanceId = indicatorInstanceResponseRs.getIndicatorInstanceId();
                indicatorInstanceIdSet.add(indicatorInstanceId);
                String caseIndicatorInstanceId = idGenerator.nextIdStr();
                kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.put(indicatorInstanceId, caseIndicatorInstanceId);
                String indicatorName = indicatorInstanceResponseRs.getIndicatorName();
                Integer displayByPercent = indicatorInstanceResponseRs.getDisplayByPercent();
                String unit = indicatorInstanceResponseRs.getUnit();
                Integer core = indicatorInstanceResponseRs.getCore();
                Integer food = indicatorInstanceResponseRs.getFood();
                Integer type = indicatorInstanceResponseRs.getType();
                String descr = indicatorInstanceResponseRs.getDescr();
                String def = indicatorInstanceResponseRs.getDef();
                String min = indicatorInstanceResponseRs.getMin();
                String max = indicatorInstanceResponseRs.getMax();
                Integer seqIndicatorInstance = indicatorInstanceResponseRs.getSeq();
                caseIndicatorInstanceEntityList.add(CaseIndicatorInstanceEntity
                        .builder()
                        .caseIndicatorInstanceId(caseIndicatorInstanceId)
                        .indicatorInstanceId(indicatorInstanceId)
                        .appId(appId)
                        .principalId(casePersonId)
                        .indicatorCategoryId(caseIndicatorCategoryId)
                        .indicatorName(indicatorName)
                        .displayByPercent(displayByPercent)
                        .unit(unit)
                        .core(core)
                        .food(food)
                        .type(type)
                        .descr(descr)
                        .build());
                caseIndicatorRuleEntityList.add(CaseIndicatorRuleEntity
                        .builder()
                        .caseIndicatorRuleId(idGenerator.nextIdStr())
                        .appId(appId)
                        .variableId(caseIndicatorInstanceId)
                        .ruleType(EnumIndicatorRuleType.INDICATOR.getCode())
                        .min(min)
                        .max(max)
                        .def(def)
                        .build());
                caseIndicatorCategoryRefEntityList.add(CaseIndicatorCategoryRefEntity
                        .builder()
                        .caseIndicatorCategoryRefId(idGenerator.nextIdStr())
                        .indicatorCategoryId(indicatorCategoryId)
                        .appId(appId)
                        .seq(seq)
                        .indicatorCategoryId(caseIndicatorCategoryId)
                        .indicatorInstanceId(caseIndicatorInstanceId)
                        .seq(seqIndicatorInstance)
                        .build());
            });
            caseIndicatorCategoryEntityList.add(CaseIndicatorCategoryEntity
                    .builder()
                    .caseIndicatorCategoryId(caseIndicatorCategoryId)
                    .indicatorCategoryId(indicatorCategoryId)
                    .appId(appId)
                    .pid(EnumIndicatorCategory.INDICATOR_MANAGEMENT.getCode())
                    .categoryName(categoryName)
                    .seq(seq)
                    .build());
            caseIndicatorCategoryPrincipalRefEntityList.add(CaseIndicatorCategoryPrincipalRefEntity
                    .builder()
                    .caseIndicatorCategoryPrincipalRefId(idGenerator.nextIdStr())
                    .principalId(casePersonId)
                    .indicatorCategoryId(caseIndicatorCategoryId)
                    .appId(appId)
                    .build());
        });
        /**
         * runsix method process
         * 1.caseIndicatorExpressionRefEntityList
         * 2.caseIndicatorExpressionEntityList
         * 3.caseIndicatorExpressionItemEntityList
         */
        indicatorInstanceCategoryResponseRsList.forEach(indicatorInstanceCategoryResponseRs -> {
            List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList = indicatorInstanceCategoryResponseRs.getIndicatorInstanceResponseRsList();
            if (Objects.isNull(indicatorInstanceResponseRsList)) {
                return;
            }
            indicatorInstanceResponseRsList.forEach(indicatorInstanceResponseRs -> {
                List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList = indicatorInstanceResponseRs.getIndicatorExpressionResponseRsList();
                if (Objects.isNull(indicatorExpressionResponseRsList)) {
                    return;
                }
                String indicatorInstanceId = indicatorInstanceResponseRs.getIndicatorInstanceId();
                String caseIndicatorInstanceId = kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.get(indicatorInstanceId);
                indicatorExpressionResponseRsList.forEach(indicatorExpressionResponseRs -> {
                    String indicatorExpressionId = indicatorExpressionResponseRs.getIndicatorExpressionId();
                    String caseIndicatorExpressionId = idGenerator.nextIdStr();
                    String indicatorExpressionRefId = indicatorExpressionResponseRs.getIndicatorExpressionRefId();
                    String caseIndicatorExpressionRefId = idGenerator.nextIdStr();
                    String principalId = indicatorExpressionResponseRs.getPrincipalId();
                    Integer type = indicatorExpressionResponseRs.getType();
                    Integer source = indicatorExpressionResponseRs.getSource();
                    List<IndicatorExpressionItemResponseRs> indicatorExpressionItemResponseRsList = indicatorExpressionResponseRs.getIndicatorExpressionItemResponseRsList();
                    if (Objects.isNull(indicatorExpressionItemResponseRsList) || indicatorExpressionItemResponseRsList.isEmpty()) {
                        return;
                    }
                    indicatorExpressionItemResponseRsList.forEach(indicatorExpressionItemResponseRs -> {
                        String indicatorExpressionItemId = indicatorExpressionItemResponseRs.getIndicatorExpressionItemId();
                        String caseIndicatorExpressionItemId = idGenerator.nextIdStr();
                        String conditionRaw = indicatorExpressionItemResponseRs.getConditionRaw();
                        String conditionExpression = indicatorExpressionItemResponseRs.getConditionExpression();
                        String conditionNameList = indicatorExpressionItemResponseRs.getConditionNameList();
                        String conditionValList = convertConditionValList2Case(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, indicatorExpressionItemResponseRs.getConditionValList());
                        String resultRaw = indicatorExpressionItemResponseRs.getResultRaw();
                        String resultExpression = indicatorExpressionItemResponseRs.getResultExpression();
                        String resultNameList = indicatorExpressionItemResponseRs.getResultNameList();
                        String resultValList = convertResultValList2Case(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, indicatorExpressionItemResponseRs.getResultValList());
                        Integer seqIndicatorExpressionItem = indicatorExpressionItemResponseRs.getSeq();
                        caseIndicatorExpressionItemEntityList.add(caseIndicatorExpressionBiz.caseIndicatorExpressionItemResponseRs2Case(
                                caseIndicatorExpressionItemId, indicatorExpressionItemId, appId, caseIndicatorExpressionId, conditionRaw,
                                conditionExpression, conditionNameList, conditionValList, resultRaw, resultExpression, resultNameList, resultValList, seqIndicatorExpressionItem));
                        populateInfluenceMap(
                                kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap,
                                kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap,
                                caseIndicatorInstanceId,
                                conditionValList,
                                resultValList
                        );
                    });
                    String minCaseIndicatorExpressionItemId = null;
                    String maxCaseIndicatorExpressionItemId = null;
                    IndicatorExpressionItemResponseRs minIndicatorExpressionItemResponseRs = indicatorExpressionResponseRs.getMinIndicatorExpressionItemResponseRs();
                    if (Objects.nonNull(minIndicatorExpressionItemResponseRs)) {
                        String minIndicatorExpressionItemId = minIndicatorExpressionItemResponseRs.getIndicatorExpressionItemId();
                        minCaseIndicatorExpressionItemId = idGenerator.nextIdStr();
                        String minConditionRaw = minIndicatorExpressionItemResponseRs.getConditionRaw();
                        String minConditionExpression = minIndicatorExpressionItemResponseRs.getConditionExpression();
                        String minConditionNameList = minIndicatorExpressionItemResponseRs.getConditionNameList();
                        String minConditionValList = convertConditionValList2Case(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, minIndicatorExpressionItemResponseRs.getConditionValList());
                        String minResultRaw = minIndicatorExpressionItemResponseRs.getResultRaw();
                        String minResultExpression = minIndicatorExpressionItemResponseRs.getResultExpression();
                        String minResultNameList = minIndicatorExpressionItemResponseRs.getResultNameList();
                        String minResultValList = convertResultValList2Case(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, minIndicatorExpressionItemResponseRs.getResultValList());
                        Integer minSeqIndicatorExpressionItem = minIndicatorExpressionItemResponseRs.getSeq();
                        caseIndicatorExpressionItemEntityList.add(caseIndicatorExpressionBiz.caseIndicatorExpressionItemResponseRs2Case(
                                minCaseIndicatorExpressionItemId, minIndicatorExpressionItemId, appId, null, minConditionRaw,
                                minConditionExpression, minConditionNameList, minConditionValList, minResultRaw, minResultExpression, minResultNameList, minResultValList, minSeqIndicatorExpressionItem));
                        populateInfluenceMap(
                                kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap,
                                kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap,
                                caseIndicatorInstanceId,
                                minConditionValList,
                                minResultValList
                        );
                    }
                    IndicatorExpressionItemResponseRs maxIndicatorExpressionItemResponseRs = indicatorExpressionResponseRs.getMaxIndicatorExpressionItemResponseRs();
                    if (Objects.nonNull(maxIndicatorExpressionItemResponseRs)) {
                        String maxIndicatorExpressionItemId = maxIndicatorExpressionItemResponseRs.getIndicatorExpressionItemId();
                        maxCaseIndicatorExpressionItemId = idGenerator.nextIdStr();
                        String maxConditionRaw = maxIndicatorExpressionItemResponseRs.getConditionRaw();
                        String maxConditionExpression = maxIndicatorExpressionItemResponseRs.getConditionExpression();
                        String maxConditionNameList = maxIndicatorExpressionItemResponseRs.getConditionNameList();
                        String maxConditionValList = convertConditionValList2Case(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, maxIndicatorExpressionItemResponseRs.getConditionValList());
                        String maxResultRaw = maxIndicatorExpressionItemResponseRs.getResultRaw();
                        String maxResultExpression = maxIndicatorExpressionItemResponseRs.getResultExpression();
                        String maxResultNameList = maxIndicatorExpressionItemResponseRs.getResultNameList();
                        String maxResultValList = convertResultValList2Case(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap, maxIndicatorExpressionItemResponseRs.getResultValList());
                        Integer maxSeqIndicatorExpressionItem = maxIndicatorExpressionItemResponseRs.getSeq();
                        caseIndicatorExpressionItemEntityList.add(caseIndicatorExpressionBiz.caseIndicatorExpressionItemResponseRs2Case(
                                maxCaseIndicatorExpressionItemId, maxIndicatorExpressionItemId, appId, null, maxConditionRaw,
                                maxConditionExpression, maxConditionNameList, maxConditionValList, maxResultRaw, maxResultExpression, maxResultNameList, maxResultValList, maxSeqIndicatorExpressionItem));
                        populateInfluenceMap(
                                kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap,
                                kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap,
                                caseIndicatorInstanceId,
                                maxConditionValList,
                                maxResultValList
                        );
                    }
                    caseIndicatorExpressionEntityList.add(CaseIndicatorExpressionEntity
                            .builder()
                            .caseIndicatorExpressionId(caseIndicatorExpressionId)
                            .indicatorExpressionId(indicatorExpressionId)
                            .appId(appId)
                            .casePrincipalId(kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.get(principalId))
                            .principalId(principalId)
                            .maxIndicatorExpressionItemId(maxCaseIndicatorExpressionItemId)
                            .minIndicatorExpressionItemId(minCaseIndicatorExpressionItemId)
                            .type(type)
                            .source(source)
                            .build());
                    caseIndicatorExpressionRefEntityList.add(CaseIndicatorExpressionRefEntity
                            .builder()
                            .caseIndicatorExpressionRefId(caseIndicatorExpressionRefId)
                            .indicatorExpressionRefId(indicatorExpressionRefId)
                            .appId(appId)
                            .indicatorExpressionId(caseIndicatorExpressionId)
                            .reasonId(caseIndicatorInstanceId)
                            .build());
                });
            });
        });
        /**
         * runsix method process
         * 1.caseIndicatorExpressionInfluenceEntityList
         */
        if (!indicatorInstanceIdSet.isEmpty()) {
            indicatorExpressionInfluenceService.lambdaQuery()
                    .eq(IndicatorExpressionInfluenceEntity::getAppId, appId)
                    .in(IndicatorExpressionInfluenceEntity::getIndicatorInstanceId, indicatorInstanceIdSet)
                    .list()
                    .forEach(indicatorExpressionInfluenceEntity -> {
                        String indicatorInstanceId = indicatorExpressionInfluenceEntity.getIndicatorInstanceId();
                        String influenceIndicatorInstanceIdList = indicatorExpressionInfluenceEntity.getInfluenceIndicatorInstanceIdList();
                        Set<String> influenceIndicatorInstanceIdSetSplit = new HashSet<>();
                        if (StringUtils.isNotBlank(influenceIndicatorInstanceIdList)) {
                            influenceIndicatorInstanceIdSetSplit = Arrays.stream(influenceIndicatorInstanceIdList.split(EnumString.COMMA.getStr())).collect(Collectors.toSet());
                        }
                        String influencedIndicatorInstanceIdList = indicatorExpressionInfluenceEntity.getInfluencedIndicatorInstanceIdList();
                        Set<String> influencedIndicatorInstanceIdSetSplit = new HashSet<>();
                        if (StringUtils.isNotBlank(influencedIndicatorInstanceIdList)) {
                            influencedIndicatorInstanceIdSetSplit = Arrays.stream(influencedIndicatorInstanceIdList.split(EnumString.COMMA.getStr())).collect(Collectors.toSet());
                        }
                        kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.put(indicatorInstanceId, influenceIndicatorInstanceIdSetSplit);
                        kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.put(indicatorInstanceId, influencedIndicatorInstanceIdSetSplit);
                    });
        }
        caseIndicatorInstanceEntityList.forEach(caseIndicatorInstanceEntity -> {
            String caseIndicatorInstanceId = caseIndicatorInstanceEntity.getCaseIndicatorInstanceId();
            String indicatorInstanceId = caseIndicatorInstanceEntity.getIndicatorInstanceId();
            String influenceIndicatorInstanceIdList = null;
            String influencedIndicatorInstanceIdList = null;
            Set<String> influenceIndicatorInstanceIdSet = kIndicatorInstanceIdVInfluenceIndicatorInstanceIdSetMap.get(indicatorInstanceId);
            Set<String> caseInfluenceIndicatorInstanceIdSet = new HashSet<>();
            if (Objects.nonNull(influenceIndicatorInstanceIdSet) && !influenceIndicatorInstanceIdSet.isEmpty()) {
                influenceIndicatorInstanceIdSet.forEach(indicatorInstanceId1 -> {
                    String caseIndicatorInstanceId1 = kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.get(indicatorInstanceId1);
                    if (StringUtils.isNotBlank(caseIndicatorInstanceId1)) {
                        caseInfluenceIndicatorInstanceIdSet.add(caseIndicatorInstanceId1);
                    }
                });
            }
            influenceIndicatorInstanceIdList = String.join(EnumString.COMMA.getStr(), caseInfluenceIndicatorInstanceIdSet);
            Set<String> influencedIndicatorInstanceIdSet = kIndicatorInstanceIdVInfluencedIndicatorInstanceIdSetMap.get(indicatorInstanceId);
            Set<String> caseInfluencedIndicatorInstanceIdSet = new HashSet<>();
            if (Objects.nonNull(influencedIndicatorInstanceIdSet) && !influencedIndicatorInstanceIdSet.isEmpty()) {
                influencedIndicatorInstanceIdSet.forEach(indicatorInstanceId1 -> {
                    String caseIndicatorInstanceId1 = kIndicatorInstanceIdVCaseIndicatorInstanceIdMap.get(indicatorInstanceId1);
                    if (StringUtils.isNotBlank(caseIndicatorInstanceId1)) {
                        caseInfluencedIndicatorInstanceIdSet.add(caseIndicatorInstanceId1);
                    }
                });
            }
            influencedIndicatorInstanceIdList = String.join(EnumString.COMMA.getStr(), caseInfluencedIndicatorInstanceIdSet);
            caseIndicatorExpressionInfluenceEntityList.add(CaseIndicatorExpressionInfluenceEntity
                    .builder()
                    .caseIndicatorExpressionInfluenceId(idGenerator.nextIdStr())
                    .appId(appId)
                    .indicatorInstanceId(caseIndicatorInstanceId)
                    .influenceIndicatorInstanceIdList(influenceIndicatorInstanceIdList)
                    .influencedIndicatorInstanceIdList(influencedIndicatorInstanceIdList)
                    .build());
        });
        caseIndicatorCategoryService.saveOrUpdateBatch(caseIndicatorCategoryEntityList);
        caseIndicatorCategoryRefService.saveOrUpdateBatch(caseIndicatorCategoryRefEntityList);
        caseIndicatorRuleService.saveOrUpdateBatch(caseIndicatorRuleEntityList);
        caseIndicatorInstanceService.saveOrUpdateBatch(caseIndicatorInstanceEntityList);
        caseIndicatorExpressionRefService.saveOrUpdateBatch(caseIndicatorExpressionRefEntityList);
        caseIndicatorExpressionService.saveOrUpdateBatch(caseIndicatorExpressionEntityList);
        caseIndicatorExpressionItemService.saveOrUpdateBatch(caseIndicatorExpressionItemEntityList);
        caseIndicatorCategoryPrincipalRefService.saveOrUpdateBatch(caseIndicatorCategoryPrincipalRefEntityList);
        caseIndicatorExpressionInfluenceService.saveOrUpdateBatch(caseIndicatorExpressionInfluenceEntityList);
    }

    public List<CaseIndicatorInstanceCategoryResponseRs> getByPersonIdAndAppId(String personId, String appId) {
        Map<String, List<CaseIndicatorInstanceResponseRs>> kCaseIndicatorCategoryIdVCaseIndicatorInstanceResponseRsListMap = new HashMap<>();
        Map<String, Set<String>> kCaseIndicatorCategoryIdVCaseIndicatorInstanceIdSetMap = new HashMap<>();
        Map<String, CaseIndicatorInstanceEntity> kCaseIndicatorInstanceIdVCaseIndicatorInstanceMap = new HashMap<>();
        Map<String, CaseIndicatorRuleEntity> kCaseIndicatorInstanceIdVCaseIndicatorRuleMap = new HashMap<>();
        Map<String, List<CaseIndicatorExpressionResponseRs>> kCaseIndicatorInstanceIdVCaseIndicatorExpressionResponseRsListMap = new HashMap<>();
        Map<String, Integer> kCaseIndicatorInstanceIdVSeqMap = new HashMap<>();
        Set<String> caseIndicatorInstanceIdSet = new HashSet<>();
        Set<String> indicatorCategoryIdSet = caseIndicatorCategoryPrincipalRefService.lambdaQuery()
                .eq(CaseIndicatorCategoryPrincipalRefEntity::getAppId, appId)
                .eq(CaseIndicatorCategoryPrincipalRefEntity::getPrincipalId, personId)
                .list()
                .stream()
                .map(CaseIndicatorCategoryPrincipalRefEntity::getIndicatorCategoryId)
                .collect(Collectors.toSet());
        if (indicatorCategoryIdSet.isEmpty()) {
            return new ArrayList<>();
        }
        List<CaseIndicatorCategoryEntity> caseIndicatorCategoryEntityList = caseIndicatorCategoryService.lambdaQuery()
                .eq(CaseIndicatorCategoryEntity::getAppId, appId)
                .in(CaseIndicatorCategoryEntity::getCaseIndicatorCategoryId, indicatorCategoryIdSet)
                .orderByAsc(CaseIndicatorCategoryEntity::getSeq)
                .list();
        if (caseIndicatorCategoryEntityList.isEmpty()) {
            return new ArrayList<>();
        }
        caseIndicatorInstanceService.lambdaQuery()
                .eq(CaseIndicatorInstanceEntity::getAppId, appId)
                .in(CaseIndicatorInstanceEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
                .list()
                .forEach(caseIndicatorInstanceEntity -> {
                    String caseIndicatorInstanceId = caseIndicatorInstanceEntity.getCaseIndicatorInstanceId();
                    caseIndicatorInstanceIdSet.add(caseIndicatorInstanceId);
                    String indicatorCategoryId = caseIndicatorInstanceEntity.getIndicatorCategoryId();
                    Set<String> caseIndicatorInstanceIdSet1 = kCaseIndicatorCategoryIdVCaseIndicatorInstanceIdSetMap.get(indicatorCategoryId);
                    if (Objects.isNull(caseIndicatorInstanceIdSet1)) {
                        caseIndicatorInstanceIdSet1 = new HashSet<>();
                    }
                    caseIndicatorInstanceIdSet1.add(caseIndicatorInstanceId);
                    kCaseIndicatorCategoryIdVCaseIndicatorInstanceIdSetMap.put(indicatorCategoryId, caseIndicatorInstanceIdSet1);
                    kCaseIndicatorInstanceIdVCaseIndicatorInstanceMap.put(caseIndicatorInstanceId, caseIndicatorInstanceEntity);
                });
        populateKCaseIndicatorInstanceIdVIndicatorRuleMap(appId, caseIndicatorInstanceIdSet, kCaseIndicatorInstanceIdVCaseIndicatorRuleMap);
        caseIndicatorExpressionBiz.populateKCaseReasonIdVCaseIndicatorExpressionResponseRsListMap(appId, caseIndicatorInstanceIdSet, kCaseIndicatorInstanceIdVCaseIndicatorExpressionResponseRsListMap);
        populateKCaseIndicatorInstanceIdVSeqMap(appId, caseIndicatorInstanceIdSet, kCaseIndicatorInstanceIdVSeqMap);
        kCaseIndicatorCategoryIdVCaseIndicatorInstanceIdSetMap.forEach((caseIndicatorCategoryId, caseIndicatorInstanceIdSet0) -> {
            List<CaseIndicatorInstanceResponseRs> caseIndicatorInstanceResponseRsList = caseIndicatorInstanceIdSet0
                    .stream()
                    .map(caseIndicatorInstanceId -> {
                        CaseIndicatorInstanceEntity caseIndicatorInstanceEntity = kCaseIndicatorInstanceIdVCaseIndicatorInstanceMap.get(caseIndicatorInstanceId);
                        List<CaseIndicatorExpressionResponseRs> caseIndicatorExpressionResponseRsList = kCaseIndicatorInstanceIdVCaseIndicatorExpressionResponseRsListMap.get(caseIndicatorInstanceId);
                        CaseIndicatorRuleEntity caseIndicatorRuleEntity = kCaseIndicatorInstanceIdVCaseIndicatorRuleMap.get(caseIndicatorInstanceId);
                        String def = null;
                        String min = null;
                        String max = null;
                        if (Objects.nonNull(caseIndicatorRuleEntity)) {
                            def = caseIndicatorRuleEntity.getDef();
                            min = caseIndicatorRuleEntity.getMin();
                            max = caseIndicatorRuleEntity.getMax();
                        }
                        Integer seq = kCaseIndicatorInstanceIdVSeqMap.get(caseIndicatorInstanceId);
                        return caseIndicatorInstance2ResponseRs(
                                caseIndicatorInstanceEntity,
                                caseIndicatorExpressionResponseRsList,
                                def, min, max, seq);
                    }).collect(Collectors.toList());
            kCaseIndicatorCategoryIdVCaseIndicatorInstanceResponseRsListMap.put(caseIndicatorCategoryId, caseIndicatorInstanceResponseRsList);
        });
        return caseIndicatorCategoryEntityList
                .stream()
                .map(caseIndicatorCategoryEntity -> {
                    String caseIndicatorCategoryId = caseIndicatorCategoryEntity.getCaseIndicatorCategoryId();
                    List<CaseIndicatorInstanceResponseRs> caseIndicatorInstanceResponseRsList = kCaseIndicatorCategoryIdVCaseIndicatorInstanceResponseRsListMap.get(caseIndicatorCategoryId);
                    if (Objects.isNull(caseIndicatorInstanceResponseRsList)) {
                        caseIndicatorInstanceResponseRsList = new ArrayList<>();
                    }
                    caseIndicatorInstanceResponseRsList.sort(Comparator.comparing(CaseIndicatorInstanceResponseRs::getSeq));
                    return CaseIndicatorCategoryBiz.caseIndicatorCategory2ResponseRs(
                            caseIndicatorCategoryEntity, caseIndicatorInstanceResponseRsList);
                }).collect(Collectors.toList());
    }

    public String getHealthPoint(String casePersonId) {
        String healthPoint = "1";
        CasePersonEntity casePersonEntity = casePersonService.lambdaQuery()
                .eq(CasePersonEntity::getCasePersonId, casePersonId)
                .one();
        if (Objects.nonNull(casePersonEntity)) {
            String accountId = casePersonEntity.getAccountId();
            CaseIndicatorInstanceEntity caseIndicatorInstanceEntity = caseIndicatorInstanceService.lambdaQuery()
                    .eq(CaseIndicatorInstanceEntity::getPrincipalId, accountId)
                    .eq(CaseIndicatorInstanceEntity::getType, EnumIndicatorType.HEALTH_POINT.getType())
                    .one();
            if (Objects.nonNull(caseIndicatorInstanceEntity)) {
                String caseIndicatorInstanceId = caseIndicatorInstanceEntity.getCaseIndicatorInstanceId();
                CaseIndicatorRuleEntity caseIndicatorRuleEntity = caseIndicatorRuleService.lambdaQuery()
                        .eq(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceId)
                        .one();
                if (Objects.nonNull(caseIndicatorRuleEntity)) {
                    healthPoint = caseIndicatorRuleEntity.getDef();
                }
            }
        }
        return healthPoint;
    }

    public String v2GetHealthPoint(String accountId) {
        String healthPoint = "1";
        CaseIndicatorInstanceEntity caseIndicatorInstanceEntity = caseIndicatorInstanceService.lambdaQuery()
            .eq(CaseIndicatorInstanceEntity::getPrincipalId, accountId)
            .eq(CaseIndicatorInstanceEntity::getType, EnumIndicatorType.HEALTH_POINT.getType())
            .one();
        if (Objects.nonNull(caseIndicatorInstanceEntity)) {
            String caseIndicatorInstanceId = caseIndicatorInstanceEntity.getCaseIndicatorInstanceId();
            CaseIndicatorRuleEntity caseIndicatorRuleEntity = caseIndicatorRuleService.lambdaQuery()
                .eq(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceId)
                .one();
            if (Objects.nonNull(caseIndicatorRuleEntity)) {
                healthPoint = caseIndicatorRuleEntity.getDef();
            }
        }
        return healthPoint;
    }


    /**
     * 更新NPC人物指标的值或默认值或描述
     * <p>
     * for example：
     * "indicatorInstanceId": "355501788054556672",
     * "principal": "355501787085672448",
     * "indicatorName": "性别",
     *
     * @param updateIndicatorValueRequest
     * @return
     */
    public boolean updateNpcIndicatorValue(UpdateIndicatorValueRequest updateIndicatorValueRequest) {
        CaseIndicatorInstanceEntity caseIndicatorInstanceEntity = caseIndicatorInstanceService.lambdaQuery()
                //.eq(CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId, updateIndicatorValueRequest.getCaseIndicatorInstanceId())
                .eq(CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId, updateIndicatorValueRequest.getIndicatorInstanceId())
                .eq(CaseIndicatorInstanceEntity::getPrincipalId, updateIndicatorValueRequest.getPrincipal())
                .eq(CaseIndicatorInstanceEntity::getIndicatorName, updateIndicatorValueRequest.getIndicatorName())
                .oneOpt()
                .orElse(null);
        Assert.notNull(caseIndicatorInstanceEntity,
                () -> new ExperimentException("案例人物指标[case_indicator_instance]中不存在[" + updateIndicatorValueRequest.getIndicatorName() + "]指标"));

        boolean update = caseIndicatorRuleService.lambdaUpdate()
                .set(CaseIndicatorRuleEntity::getDef, updateIndicatorValueRequest.getIndicatorValue())
                .eq(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceEntity.getCaseIndicatorInstanceId())
                .update();
        return update;
    }

    //@Transactional(rollbackFor = Exception.class)
    @DSTransactional
    public void createOrUpdateRs(CreateOrUpdateCaseIndicatorInstanceRequestRs createOrUpdateCaseIndicatorInstanceRequestRs) throws InterruptedException {
        /* runsix:param */
        String accountId = createOrUpdateCaseIndicatorInstanceRequestRs.getAccountId();
        String caseIndicatorInstanceId = createOrUpdateCaseIndicatorInstanceRequestRs.getCaseIndicatorInstanceId();
        String caseIndicatorCategoryId = createOrUpdateCaseIndicatorInstanceRequestRs.getCaseIndicatorCategoryId();
        String appId = createOrUpdateCaseIndicatorInstanceRequestRs.getAppId();
        String indicatorName = createOrUpdateCaseIndicatorInstanceRequestRs.getIndicatorName();
        Integer displayByPercent = createOrUpdateCaseIndicatorInstanceRequestRs.getDisplayByPercent();
        String def = createOrUpdateCaseIndicatorInstanceRequestRs.getDef();
        String unit = createOrUpdateCaseIndicatorInstanceRequestRs.getUnit();
        Integer core = createOrUpdateCaseIndicatorInstanceRequestRs.getCore();
        Integer food = createOrUpdateCaseIndicatorInstanceRequestRs.getFood();
        Integer type = createOrUpdateCaseIndicatorInstanceRequestRs.getType();
        String min = createOrUpdateCaseIndicatorInstanceRequestRs.getMin();
        String max = createOrUpdateCaseIndicatorInstanceRequestRs.getMax();


        /* runsix:result */
        AtomicReference<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityAR = new AtomicReference<>();
        AtomicReference<CaseIndicatorCategoryRefEntity> caseIndicatorCategoryRefEntityAR = new AtomicReference<>();
        AtomicReference<CaseIndicatorRuleEntity> caseIndicatorRuleEntityAR = new AtomicReference<>();
        AtomicReference<CaseIndicatorExpressionInfluenceEntity> caseIndicatorExpressionInfluenceEntityAR = new AtomicReference<>();
        if (StringUtils.isBlank(caseIndicatorInstanceId)) {
            caseIndicatorInstanceService.lambdaQuery()
                .eq(CaseIndicatorInstanceEntity::getPrincipalId, accountId)
                .eq(CaseIndicatorInstanceEntity::getIndicatorName, indicatorName)
                .oneOpt()
                .ifPresent(a -> {throw new CaseIndicatorInstanceException(EnumESC.CASE_INDICATOR_NAME_EXIST);});
            caseIndicatorInstanceId = idGenerator.nextIdStr();
            CaseIndicatorInstanceEntity caseIndicatorInstanceEntity = CaseIndicatorInstanceEntity
                .builder()
                .caseIndicatorInstanceId(caseIndicatorInstanceId)
                .appId(appId)
                .principalId(accountId)
                .indicatorCategoryId(caseIndicatorCategoryId)
                .indicatorName(indicatorName)
                .displayByPercent(displayByPercent)
                .unit(unit)
                .core(core)
                .food(food)
                .type(type)
                .build();
            AtomicInteger seqAtomicInteger = new AtomicInteger(1);
            caseIndicatorCategoryRefService.lambdaQuery()
                .eq(CaseIndicatorCategoryRefEntity::getIndicatorCategoryId, caseIndicatorCategoryId)
                .orderByDesc(CaseIndicatorCategoryRefEntity::getSeq)
                .last(EnumString.LIMIT_1.getStr())
                .oneOpt()
                .ifPresent(caseIndicatorCategoryRefEntity1 -> seqAtomicInteger.set(caseIndicatorCategoryRefEntity1.getSeq() + 1));
            CaseIndicatorCategoryRefEntity caseIndicatorCategoryRefEntity = CaseIndicatorCategoryRefEntity
                .builder()
                .caseIndicatorCategoryRefId(idGenerator.nextIdStr())
                .appId(appId)
                .indicatorCategoryId(caseIndicatorCategoryId)
                .indicatorInstanceId(caseIndicatorInstanceId)
                .seq(seqAtomicInteger.get())
                .build();
            CaseIndicatorRuleEntity caseIndicatorRuleEntity = CaseIndicatorRuleEntity
                .builder()
                .caseIndicatorRuleId(idGenerator.nextIdStr())
                .appId(appId)
                .variableId(caseIndicatorInstanceId)
                .ruleType(EnumIndicatorRuleType.INDICATOR.getCode())
                .min(min)
                .max(max)
                .def(def)
                .build();
            CaseIndicatorExpressionInfluenceEntity caseIndicatorExpressionInfluenceEntity = CaseIndicatorExpressionInfluenceEntity
                .builder()
                .caseIndicatorExpressionInfluenceId(idGenerator.nextIdStr())
                .appId(appId)
                .indicatorInstanceId(caseIndicatorInstanceId)
                .build();
            caseIndicatorInstanceEntityAR.set(caseIndicatorInstanceEntity);
            caseIndicatorCategoryRefEntityAR.set(caseIndicatorCategoryRefEntity);
            caseIndicatorRuleEntityAR.set(caseIndicatorRuleEntity);
            caseIndicatorExpressionInfluenceEntityAR.set(caseIndicatorExpressionInfluenceEntity);
        } else {
            rsCaseIndicatorInstanceBiz.checkCaseIndicatorInstanceIdInCaseIndicatorInstanceEntity(caseIndicatorInstanceEntityAR, caseIndicatorInstanceId);
            CaseIndicatorInstanceEntity caseIndicatorInstanceEntity = caseIndicatorInstanceEntityAR.get();
            String oldIndicatorName = caseIndicatorInstanceEntity.getIndicatorName();
            if (!StringUtils.equals(oldIndicatorName, indicatorName)) {
                caseIndicatorInstanceService.lambdaQuery()
                    .eq(CaseIndicatorInstanceEntity::getPrincipalId, accountId)
                    .eq(CaseIndicatorInstanceEntity::getIndicatorName, indicatorName)
                    .oneOpt()
                    .ifPresent(a -> {throw new CaseIndicatorInstanceException(EnumESC.CASE_INDICATOR_NAME_EXIST);});
            }
            caseIndicatorInstanceEntity.setIndicatorName(indicatorName);
            caseIndicatorInstanceEntity.setDisplayByPercent(displayByPercent);
            caseIndicatorInstanceEntity.setUnit(unit);
            caseIndicatorInstanceEntity.setCore(core);
            caseIndicatorInstanceEntity.setFood(food);
            rsCaseIndicatorInstanceBiz.checkCaseIndicatorInstanceIdInCaseIndicatorRuleEntity(caseIndicatorRuleEntityAR, caseIndicatorInstanceId);
            CaseIndicatorRuleEntity caseIndicatorRuleEntity = caseIndicatorRuleEntityAR.get();
            caseIndicatorRuleEntity.setMin(min);
            caseIndicatorRuleEntity.setMax(max);
            caseIndicatorRuleEntity.setDef(def);

            caseIndicatorInstanceEntityAR.set(caseIndicatorInstanceEntity);
            caseIndicatorRuleEntityAR.set(caseIndicatorRuleEntity);
        }
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.CASE_INDICATOR_INSTANCE_CREATE_DELETE_UPDATE, caseIndicatorInstanceFieldPid, caseIndicatorCategoryId));
        boolean isLocked = lock.tryLock(leaseTimeCaseIndicatorInstanceCreateDeleteUpdate, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorInstanceException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_INSTANCE_LATER);
        }
        try {
            if (Objects.nonNull(caseIndicatorInstanceEntityAR.get())) {caseIndicatorInstanceService.saveOrUpdate(caseIndicatorInstanceEntityAR.get());}
            if (Objects.nonNull(caseIndicatorCategoryRefEntityAR.get())) {caseIndicatorCategoryRefService.saveOrUpdate(caseIndicatorCategoryRefEntityAR.get());}
            if (Objects.nonNull(caseIndicatorRuleEntityAR.get())) {caseIndicatorRuleService.saveOrUpdate(caseIndicatorRuleEntityAR.get());}
            if (Objects.nonNull(caseIndicatorExpressionInfluenceEntityAR.get())) {caseIndicatorExpressionInfluenceService.saveOrUpdate(caseIndicatorExpressionInfluenceEntityAR.get());}
            /* runsix:重新计算健康指数 */
            /*rsCaseCalculateBiz.caseRsCalculateHealthScore(CaseRsCalculateHealthScoreRequestRs
                .builder()
                .appId(appId)
                .accountId(accountId)
                .build());*/
            evalCaseHealthIndexBiz.evalCasePersonHealthIndex(CaseRsCalculateHealthScoreRequestRs
                    .builder()
                    .appId(appId)
                    .accountId(accountId)
                    .build());
            xAccountInstanceApi.updateAccountDt(accountId,new Date());

        } finally {
            lock.unlock();
        }
    }

    /* runsix:TODO 删除指标公式相关 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String caseIndicatorInstanceId) throws ExecutionException, InterruptedException {
        AtomicReference<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityAR = new AtomicReference<>();
        CompletableFuture<Void> cfCheckCaseIndicatorInstanceId = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.checkCaseIndicatorInstanceIdInCaseIndicatorInstanceEntity(caseIndicatorInstanceEntityAR, caseIndicatorInstanceId);
        });
        cfCheckCaseIndicatorInstanceId.get();

        CaseIndicatorInstanceEntity caseIndicatorInstanceEntity = caseIndicatorInstanceEntityAR.get();
        CompletableFuture<Void> cfCheckIfCanDeleteCaseIndicatorInstanceEntity = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.checkIfCanDeleteCaseIndicatorInstanceEntity(caseIndicatorInstanceEntity);
        });
        cfCheckIfCanDeleteCaseIndicatorInstanceEntity.get();

        String appId = caseIndicatorInstanceEntity.getAppId();
        String indicatorCategoryId = caseIndicatorInstanceEntity.getIndicatorCategoryId();
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.CASE_INDICATOR_INSTANCE_CREATE_DELETE_UPDATE, caseIndicatorInstanceFieldPid, indicatorCategoryId));
        boolean isLocked = lock.tryLock(leaseTimeCaseIndicatorInstanceCreateDeleteUpdate, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new IndicatorInstanceException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_CASE_INDICATOR_INSTANCE_LATER);
        }
        try {
            boolean isRemovedCaseIndicatorInstance = caseIndicatorInstanceService.remove(
                new LambdaQueryWrapper<CaseIndicatorInstanceEntity>()
                    .eq(CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId, caseIndicatorInstanceId)
            );
            if (!isRemovedCaseIndicatorInstance) {
                log.warn("方法CaseIndicatorInstanceBiz.delete对caseIndicatorInstanceId：{}的caseIndicatorInstance删除失败", caseIndicatorInstanceId);
                throw new RsCaseIndicatorInstanceBizException(EnumESC.CASE_INDICATOR_INSTANCE_ID_IS_ILLEGAL);
            }
            boolean isRemovedCaseIndicatorCategoryRef = caseIndicatorCategoryRefService.remove(
                new LambdaQueryWrapper<CaseIndicatorCategoryRefEntity>()
                    .eq(CaseIndicatorCategoryRefEntity::getIndicatorInstanceId, caseIndicatorInstanceId)
            );
            if (!isRemovedCaseIndicatorCategoryRef) {
                log.warn("方法CaseIndicatorInstanceBiz.delete对caseIndicatorInstanceId：{}的IndicatorCategoryRef删除失败", caseIndicatorInstanceId);
                throw new IndicatorInstanceException(EnumESC.CASE_INDICATOR_INSTANCE_ID_CATEGORY_REF_IS_ILLEGAL);
            }
            AtomicInteger atomicInteger = new AtomicInteger(1);
            List<CaseIndicatorCategoryRefEntity> caseIndicatorCategoryRefEntityList = caseIndicatorCategoryRefService.lambdaQuery()
                .eq(CaseIndicatorCategoryRefEntity::getAppId, appId)
                .eq(CaseIndicatorCategoryRefEntity::getIndicatorCategoryId, indicatorCategoryId)
                .orderByAsc(CaseIndicatorCategoryRefEntity::getSeq)
                .list()
                .stream()
                .peek(caseIndicatorCategoryRefEntity1 -> {
                    caseIndicatorCategoryRefEntity1.setSeq(atomicInteger.getAndIncrement());
                })
                .collect(Collectors.toList());
            caseIndicatorCategoryRefService.saveOrUpdateBatch(caseIndicatorCategoryRefEntityList);
            boolean isRemovedCaseIndicatorRule = caseIndicatorRuleService.remove(
                new LambdaQueryWrapper<CaseIndicatorRuleEntity>()
                    .eq(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceId)
            );
            if (!isRemovedCaseIndicatorRule) {
                log.warn("方法方法CaseIndicatorInstanceBiz.delete对VariableId：{}的CaseIndicatorRule删除失败", cfCheckCaseIndicatorInstanceId);
                throw new IndicatorInstanceException(EnumESC.CASE_INDICATOR_INSTANCE_ID_RULE_IS_ILLEGAL);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * runsix method process
     * 根据account_id获取关键指标
    */
    public Map<String, List<String>> getCoreByAccountIdList(Collection<String> accountIdList) {
        Map<String, List<String>> resultMap = new HashMap<>();
        /* runsix:校验 */
        if (Objects.isNull(accountIdList) || accountIdList.isEmpty()) {return resultMap;}

        /* runsix:1.获取所有人所有核心指标 */
        Set<String> caseIndicatorInstanceIdSet = new HashSet<>();
        Map<String, List<CaseIndicatorInstanceEntity>> kAccountIdVCaseIndicatorInstanceEntityListMap = new HashMap<>();
        caseIndicatorInstanceService.lambdaQuery()
            .eq(CaseIndicatorInstanceEntity::getCore, EnumStatus.ENABLE.getCode())
            .in(CaseIndicatorInstanceEntity::getPrincipalId, accountIdList)
            .list()
            .forEach(caseIndicatorInstanceEntity -> {
                String caseIndicatorInstanceId = caseIndicatorInstanceEntity.getCaseIndicatorInstanceId();
                caseIndicatorInstanceIdSet.add(caseIndicatorInstanceId);

                String principalId = caseIndicatorInstanceEntity.getPrincipalId();
                List<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityList = kAccountIdVCaseIndicatorInstanceEntityListMap.get(principalId);
                if (Objects.isNull(caseIndicatorInstanceEntityList)) {caseIndicatorInstanceEntityList = new ArrayList<>();}
                caseIndicatorInstanceEntityList.add(caseIndicatorInstanceEntity);
                kAccountIdVCaseIndicatorInstanceEntityListMap.put(principalId, caseIndicatorInstanceEntityList);
            });

        /* runsix:校验 */
        if (caseIndicatorInstanceIdSet.isEmpty()) {return resultMap;}

        /* runsix:2.获取所有核心指标的值 */
        Map<String, String> kCaseIndicatorInstanceIdVDefMap = new HashMap<>();
        caseIndicatorRuleService.lambdaQuery()
            .in(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceIdSet)
            .list()
            .forEach(caseIndicatorRuleEntity -> {
                kCaseIndicatorInstanceIdVDefMap.put(caseIndicatorRuleEntity.getVariableId(), caseIndicatorRuleEntity.getDef());
            });


        /* runsix:3.组合数据 */
        kAccountIdVCaseIndicatorInstanceEntityListMap.forEach((accountId, caseIndicatorInstanceEntityList) -> {
            caseIndicatorInstanceEntityList.forEach(caseIndicatorInstanceEntity -> {
                List<String> resultList = resultMap.get(accountId);
                if (Objects.isNull(resultList)) {resultList = new ArrayList<>();}

                String indicatorName = caseIndicatorInstanceEntity.getIndicatorName();
                String unit = caseIndicatorInstanceEntity.getUnit();
                String caseIndicatorInstanceId = caseIndicatorInstanceEntity.getCaseIndicatorInstanceId();
                String currentVal = kCaseIndicatorInstanceIdVDefMap.get(caseIndicatorInstanceId);
                /* runsix:理论上每个指标都有默认值，如果存在数据错误，就丢掉 */
                if (StringUtils.isBlank(currentVal)) {return;}
                resultList.add(RsUtilBiz.getCoreString(indicatorName, currentVal, unit));

                resultMap.put(accountId, resultList);
            });
        });

        return resultMap;
    }
}
