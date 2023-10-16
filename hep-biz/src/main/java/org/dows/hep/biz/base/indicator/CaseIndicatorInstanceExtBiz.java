package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.enums.EnumIndicatorRuleType;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 人物指标复制
 *
 * @description: lifel 2023/10/10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseIndicatorInstanceExtBiz {

    private final IndicatorInstanceBiz indicatorInstanceBiz;
    private final RsUtilBiz rsUtilBiz;
    private final RsCaseIndicatorInstanceBiz rsCaseIndicatorInstanceBiz;
    private final CaseIndicatorCategoryService caseIndicatorCategoryService;
    private final CaseIndicatorCategoryRefService caseIndicatorCategoryRefService;
    private final CaseIndicatorCategoryPrincipalRefService caseIndicatorCategoryPrincipalRefService;
    private final CaseIndicatorRuleService caseIndicatorRuleService;
    private final CaseIndicatorInstanceService caseIndicatorInstanceService;
    private final CaseIndicatorExpressionRefService caseIndicatorExpressionRefService;
    private final CaseIndicatorExpressionService caseIndicatorExpressionService;
    private final CaseIndicatorExpressionItemService caseIndicatorExpressionItemService;
    private final CaseIndicatorExpressionInfluenceService caseIndicatorExpressionInfluenceService;
    private final IdGenerator idGenerator;
    private final CaseIndicatorInstanceBiz caseIndicatorInstanceBiz;


    /**
     * 基础指标
     */
    public void getByAppId(String appId, String principalId) throws ExecutionException, InterruptedException {
        List<IndicatorInstanceCategoryResponseRs> indicatorInstanceCategoryResponseRsList = indicatorInstanceBiz.getByAppId(appId);
        if (CollectionUtils.isEmpty(indicatorInstanceCategoryResponseRsList)) {
            log.error("CaseIndicatorInstanceExtBiz.v2CopyPersonIndicatorInstance indicatorInstanceCategoryResponseRsList is empty do not copy");
            return;
        }
        //所有
        Set<String> allOldIdSet = new HashSet<>();
        //指标id
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        indicatorInstanceCategoryResponseRsList.forEach(indicatorCategoryResponseRs -> {
            allOldIdSet.add(indicatorCategoryResponseRs.getIndicatorCategoryId());
            //当前目录下的指标
            List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList = indicatorCategoryResponseRs.getIndicatorInstanceResponseRsList();
            if (CollectionUtils.isEmpty(indicatorInstanceResponseRsList)) {
                return;
            }
            indicatorInstanceResponseRsList.forEach(indicatorInstance -> {
                allOldIdSet.add(indicatorInstance.getIndicatorCategoryId());
                indicatorInstanceIdSet.add(indicatorInstance.getIndicatorInstanceId());
                List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList = indicatorInstance.getIndicatorExpressionResponseRsList();
                if (CollectionUtils.isEmpty(indicatorExpressionResponseRsList)) {
                    return;
                }
                indicatorExpressionResponseRsList.forEach(indicatorExpression -> {
                    allOldIdSet.add(indicatorExpression.getIndicatorExpressionId());
                    allOldIdSet.add(indicatorExpression.getIndicatorExpressionRefId());
                    List<IndicatorExpressionItemResponseRs> indicatorExpressionItemResponseRsList = indicatorExpression.getIndicatorExpressionItemResponseRsList();
                    Set<String> collect = indicatorExpressionItemResponseRsList.stream()
                            .map(IndicatorExpressionItemResponseRs::getIndicatorExpressionItemId)
                            .collect(Collectors.toSet());
                    allOldIdSet.addAll(collect);
                    IndicatorExpressionItemResponseRs maxIndicatorItem = indicatorExpression.getMaxIndicatorExpressionItemResponseRs();
                    allOldIdSet.add(maxIndicatorItem.getIndicatorExpressionItemId());
                    IndicatorExpressionItemResponseRs minIndicatorItem = indicatorExpression.getMinIndicatorExpressionItemResponseRs();
                    allOldIdSet.add(minIndicatorItem.getIndicatorExpressionItemId());
                });
            });
        });
        saveIndicatorInstanceAsync(allOldIdSet, indicatorInstanceIdSet, principalId, indicatorInstanceCategoryResponseRsList);
    }

    /**
     * 具体指标
     */
    public void getByPersonIdAndAppId(String appId, String principalId, String newAccount) throws ExecutionException, InterruptedException {
        List<CaseIndicatorInstanceCategoryResponseRs> caseIndicatorInstanceCategoryList = caseIndicatorInstanceBiz.getByPersonIdAndAppId(principalId, appId);
        if (CollectionUtils.isEmpty(caseIndicatorInstanceCategoryList)) {
            log.error("CaseIndicatorInstanceExtBiz.copyPersonIndicatorInstance caseIndicatorInstanceCategoryResponseRsList is empty do not copy");
            return;
        }
        //所有
        Set<String> allOldIdSet = new HashSet<>();
        //指标id
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        caseIndicatorInstanceCategoryList.forEach(caseIndicatorCategory -> {
            allOldIdSet.add(caseIndicatorCategory.getIndicatorCategoryId());
            List<CaseIndicatorInstanceResponseRs> caseIndicatorInstanceResponseRsList = caseIndicatorCategory.getCaseIndicatorInstanceResponseRsList();
            caseIndicatorInstanceResponseRsList.forEach(caseIndicatorInstance -> {
                allOldIdSet.add(caseIndicatorInstance.getIndicatorInstanceId());
                indicatorInstanceIdSet.add(caseIndicatorInstance.getIndicatorInstanceId());
                List<CaseIndicatorExpressionResponseRs> caseIndicatorExpressionResponseRsList = caseIndicatorInstance.getCaseIndicatorExpressionResponseRsList();
                caseIndicatorExpressionResponseRsList.forEach(caseIndicatorExpression -> {
                    allOldIdSet.add(caseIndicatorExpression.getIndicatorExpressionId());
                    allOldIdSet.add(caseIndicatorExpression.getIndicatorExpressionRefId());
                    CaseIndicatorExpressionItemResponseRs caseMaxItem = caseIndicatorExpression.getCaseMaxIndicatorExpressionItemResponseRs();
                    if (Objects.nonNull(caseMaxItem)) {
                        allOldIdSet.add(caseMaxItem.getIndicatorExpressionItemId());
                    }
                    CaseIndicatorExpressionItemResponseRs caseMinItem = caseIndicatorExpression.getCaseMinIndicatorExpressionItemResponseRs();
                    if (Objects.nonNull(caseMinItem)) {
                        allOldIdSet.add(caseMinItem.getIndicatorExpressionItemId());
                    }
                });
            });
        });
        saveIndicatorInstanceAsyncV2(allOldIdSet, indicatorInstanceIdSet, newAccount, caseIndicatorInstanceCategoryList);
    }

    /**
     * TODO  人物指标复制
     * 根据人物id查询关联 案例指标
     * 复制到新的人物上
     */
    @Transactional(rollbackFor = Exception.class)
    public void duplicatePersonIndicator(String appId, String personId, String newAccountId) throws ExecutionException, InterruptedException {

        List<CaseIndicatorCategoryPrincipalRefEntity> casePrincipalRefList = caseIndicatorCategoryPrincipalRefService.lambdaQuery()
                .eq(CaseIndicatorCategoryPrincipalRefEntity::getAppId, appId)
                .eq(CaseIndicatorCategoryPrincipalRefEntity::getPrincipalId, personId)
                .list();
        //案例指标目录id
        Set<String> indicatorCategoryIdSet = casePrincipalRefList.stream()
                .map(CaseIndicatorCategoryPrincipalRefEntity::getIndicatorCategoryId)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(indicatorCategoryIdSet)) {
            return;
        }
        //案例指标目录
        List<CaseIndicatorCategoryEntity> caseIndicatorCategoryEntityList = caseIndicatorCategoryService.lambdaQuery()
                .eq(CaseIndicatorCategoryEntity::getAppId, appId)
                .in(CaseIndicatorCategoryEntity::getCaseIndicatorCategoryId, indicatorCategoryIdSet)
                .orderByAsc(CaseIndicatorCategoryEntity::getSeq)
                .list();
        //案例指标实例
        List<CaseIndicatorInstanceEntity> caseIndicatorInstanceList = caseIndicatorInstanceService.lambdaQuery()
                .eq(CaseIndicatorInstanceEntity::getAppId, appId)
                .in(CaseIndicatorInstanceEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
                .list();

        // 案例指标实例id
        Set<String> caseIndicatorInstanceIdSet = caseIndicatorInstanceList.stream()
                .map(CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(caseIndicatorInstanceIdSet)) {
            return;
        }
        //案例指标规则
        List<CaseIndicatorRuleEntity> caseIndicatorRuleList = caseIndicatorRuleService.lambdaQuery()
                .eq(CaseIndicatorRuleEntity::getAppId, appId)
                .in(CaseIndicatorRuleEntity::getVariableId, caseIndicatorInstanceIdSet)
                .list();

        //案例指标公式关联
        List<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefList = caseIndicatorExpressionRefService.lambdaQuery()
                .eq(CaseIndicatorExpressionRefEntity::getAppId, appId)
                .in(CaseIndicatorExpressionRefEntity::getReasonId, caseIndicatorInstanceIdSet)
                .list();
        Set<String> indicatorExpressionIdSet = caseIndicatorExpressionRefList.stream().map(CaseIndicatorExpressionRefEntity::getIndicatorExpressionId).collect(Collectors.toSet());
        //案例指标公式
        List<CaseIndicatorExpressionEntity> caseIndicatorExpressionList = caseIndicatorExpressionService.lambdaQuery()
                .eq(CaseIndicatorExpressionEntity::getAppId, appId)
                .in(CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId, indicatorExpressionIdSet)
                .list();
        //案例指标细项id
        Set<String> caseIndicatorExperssionItemIdSet = new HashSet<>();
        caseIndicatorExpressionList.forEach(expression -> {
            caseIndicatorExperssionItemIdSet.add(expression.getMaxIndicatorExpressionItemId());
            caseIndicatorExperssionItemIdSet.add(expression.getMinIndicatorExpressionItemId());
        });
        Set<CaseIndicatorExpressionItemEntity> caseExpressionItemList;
        if (!CollectionUtils.isEmpty(caseIndicatorExperssionItemIdSet)) {
            caseExpressionItemList = new HashSet<>(caseIndicatorExpressionItemService.lambdaQuery()
                    .eq(CaseIndicatorExpressionItemEntity::getAppId, appId)
                    .in(CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId, caseIndicatorExperssionItemIdSet)
                    .list());
        } else {
            caseExpressionItemList = new HashSet<>();
        }
        //案例指标 目录与实例 关系
        List<CaseIndicatorCategoryRefEntity> caseIndicatorCategoryRefList = caseIndicatorCategoryRefService.lambdaQuery()
                .eq(CaseIndicatorCategoryRefEntity::getAppId, appId)
                .in(CaseIndicatorCategoryRefEntity::getIndicatorInstanceId, caseIndicatorInstanceIdSet)
                .in(CaseIndicatorCategoryRefEntity::getIndicatorCategoryId, indicatorCategoryIdSet)
                .list();
        //案例指标影响
        List<CaseIndicatorExpressionInfluenceEntity> caseIndicatorInfluenceList = caseIndicatorExpressionInfluenceService.lambdaQuery()
                .in(CaseIndicatorExpressionInfluenceEntity::getIndicatorInstanceId, caseIndicatorInstanceIdSet)
                .list();

        Set<String> allOldIdSet = new HashSet<>();
        allOldIdSet.addAll(indicatorCategoryIdSet);
        allOldIdSet.addAll(caseIndicatorInstanceIdSet);
        allOldIdSet.addAll(indicatorExpressionIdSet);
        allOldIdSet.addAll(caseIndicatorExperssionItemIdSet);

        //生成新的id
        Map<String, String> kOldIdVNewIdMap = new HashMap<>();
        CompletableFuture<Void> cfPopulateKOldIdVNewIdMap = CompletableFuture.runAsync(() ->
            rsUtilBiz.populateKOldIdVNewIdMap(kOldIdVNewIdMap, allOldIdSet));
        cfPopulateKOldIdVNewIdMap.get();

        CompletableFuture<Void> gitNewCasPrincipalRefList = CompletableFuture.runAsync(() ->
            gitNewCasPrincipalRefList(casePrincipalRefList, newAccountId, kOldIdVNewIdMap));
        gitNewCasPrincipalRefList.get();

        CompletableFuture<Void> getNewCaseIndicatorCategoryEntityList = CompletableFuture.runAsync(() ->
            getNewCaseIndicatorCategoryEntityList(caseIndicatorCategoryEntityList, kOldIdVNewIdMap));
        getNewCaseIndicatorCategoryEntityList.get();

        CompletableFuture<Void> getNewCaseIndicatorInstanceList = CompletableFuture.runAsync(() ->
            getNewCaseIndicatorInstanceList(caseIndicatorInstanceList, newAccountId, kOldIdVNewIdMap));
        getNewCaseIndicatorInstanceList.get();

        CompletableFuture<Void> getNewCaseIndicatorRuleList = CompletableFuture.runAsync(() ->
            getNewCaseIndicatorRuleList(caseIndicatorRuleList, kOldIdVNewIdMap));
        getNewCaseIndicatorRuleList.get();

        CompletableFuture<Void> getNewCaseIndicatorExpressionRefList = CompletableFuture.runAsync(() ->
            getNewCaseIndicatorExpressionRefList(caseIndicatorExpressionRefList, kOldIdVNewIdMap));
        getNewCaseIndicatorExpressionRefList.get();

        CompletableFuture<Void> getNewCaseIndicatorExpressionList = CompletableFuture.runAsync(() ->
            getNewCaseIndicatorExpressionList(caseIndicatorExpressionList, kOldIdVNewIdMap));
        getNewCaseIndicatorExpressionList.get();

        CompletableFuture<Void> getNewCaseExpressionItemList = CompletableFuture.runAsync(() ->
            getNewCaseExpressionItemList(caseExpressionItemList, kOldIdVNewIdMap));
        getNewCaseExpressionItemList.get();

        CompletableFuture<Void> getNewCaseIndicatorCategoryRefList = CompletableFuture.runAsync(() ->
            getNewCaseIndicatorCategoryRefList(new HashSet<>(caseIndicatorCategoryRefList), kOldIdVNewIdMap));
        getNewCaseIndicatorCategoryRefList.get();

        CompletableFuture<Void> getNewCaseIndicatorInfluenceList = CompletableFuture.runAsync(() ->
            getNewCaseIndicatorInfluenceList(caseIndicatorInfluenceList, kOldIdVNewIdMap));
        getNewCaseIndicatorInfluenceList.get();

        //save
        caseIndicatorCategoryPrincipalRefService.saveOrUpdateBatch(casePrincipalRefList);
        caseIndicatorCategoryService.saveOrUpdateBatch(caseIndicatorCategoryEntityList);
        caseIndicatorInstanceService.saveOrUpdateBatch(caseIndicatorInstanceList);
        caseIndicatorCategoryRefService.saveOrUpdateBatch(caseIndicatorCategoryRefList);
        caseIndicatorRuleService.saveOrUpdateBatch(caseIndicatorRuleList);
        caseIndicatorExpressionRefService.saveOrUpdateBatch(caseIndicatorExpressionRefList);
        caseIndicatorExpressionService.saveOrUpdateBatch(caseIndicatorExpressionList);
        caseIndicatorExpressionItemService.saveOrUpdateBatch(caseExpressionItemList);
        caseIndicatorExpressionInfluenceService.saveOrUpdateBatch(caseIndicatorInfluenceList);
    }

    private void getNewCaseIndicatorInfluenceList(List<CaseIndicatorExpressionInfluenceEntity> caseIndicatorInfluenceList,
                                                  Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(caseIndicatorInfluenceList, kOldIdVNewIdMap, "null")) {
            return;
        }
        caseIndicatorInfluenceList.forEach(caseIndicatorInfluence -> {
            caseIndicatorInfluence.setCaseIndicatorExpressionInfluenceId(idGenerator.nextIdStr());
            caseIndicatorInfluence.setIndicatorInstanceId(checkNullNewId(caseIndicatorInfluence.getIndicatorInstanceId(), kOldIdVNewIdMap));
            caseIndicatorInfluence.setInfluenceIndicatorInstanceIdList(
                    rsCaseIndicatorInstanceBiz.convertIndicatorInstanceIdList2Case(caseIndicatorInfluence.getInfluenceIndicatorInstanceIdList(), kOldIdVNewIdMap));
            caseIndicatorInfluence.setInfluencedIndicatorInstanceIdList(
                    rsCaseIndicatorInstanceBiz.convertIndicatorInstanceIdList2Case(caseIndicatorInfluence.getInfluencedIndicatorInstanceIdList(), kOldIdVNewIdMap));
            caseIndicatorInfluence.setId(null);
            caseIndicatorInfluence.setDt(new Date());
        });

    }

    private void getNewCaseIndicatorCategoryRefList(Set<CaseIndicatorCategoryRefEntity> caseIndicatorCategoryRefList,
                                                    Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(caseIndicatorCategoryRefList, kOldIdVNewIdMap, "null")) {
            return;
        }
        caseIndicatorCategoryRefList.forEach(caseIndicatorCategoryRef -> {
            caseIndicatorCategoryRef.setCaseIndicatorCategoryRefId(idGenerator.nextIdStr());
            caseIndicatorCategoryRef.setIndicatorCategoryId(checkNullNewId(caseIndicatorCategoryRef.getIndicatorCategoryId(), kOldIdVNewIdMap));
            caseIndicatorCategoryRef.setIndicatorInstanceId(checkNullNewId(caseIndicatorCategoryRef.getIndicatorInstanceId(), kOldIdVNewIdMap));
            caseIndicatorCategoryRef.setId(null);
            caseIndicatorCategoryRef.setDt(new Date());
        });
    }

    private void getNewCaseExpressionItemList(Set<CaseIndicatorExpressionItemEntity> caseExpressionItemList,
                                              Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(caseExpressionItemList, kOldIdVNewIdMap, "null")) {
            return;
        }
        caseExpressionItemList.forEach(caseExpressionItem -> {
            caseExpressionItem.setCaseIndicatorExpressionItemId(checkNullNewId(caseExpressionItem.getCaseIndicatorExpressionItemId(), kOldIdVNewIdMap));
            caseExpressionItem.setId(null);
            caseExpressionItem.setDt(new Date());
        });
    }

    private void getNewCaseIndicatorExpressionList(List<CaseIndicatorExpressionEntity> caseIndicatorExpressionList,
                                                   Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(caseIndicatorExpressionList, kOldIdVNewIdMap, "null")) {
            return;
        }
        caseIndicatorExpressionList.forEach(caseIndicatorExpression -> {
            caseIndicatorExpression.setCaseIndicatorExpressionId(checkNullNewId(caseIndicatorExpression.getCaseIndicatorExpressionId(), kOldIdVNewIdMap));
            caseIndicatorExpression.setCasePrincipalId(checkNullNewId(caseIndicatorExpression.getCasePrincipalId(), kOldIdVNewIdMap));
            caseIndicatorExpression.setMaxIndicatorExpressionItemId(checkNullNewId(caseIndicatorExpression.getMaxIndicatorExpressionItemId(), kOldIdVNewIdMap));
            caseIndicatorExpression.setMinIndicatorExpressionItemId(checkNullNewId(caseIndicatorExpression.getMinIndicatorExpressionItemId(), kOldIdVNewIdMap));
            caseIndicatorExpression.setId(null);
            caseIndicatorExpression.setDt(new Date());
        });
    }

    private void getNewCaseIndicatorExpressionRefList(List<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefList,
                                                      Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(caseIndicatorExpressionRefList, kOldIdVNewIdMap, "null")) {
            return;
        }
        caseIndicatorExpressionRefList.forEach(caseExpression -> {
            caseExpression.setCaseIndicatorExpressionRefId(idGenerator.nextIdStr());
            caseExpression.setReasonId(checkNullNewId(caseExpression.getReasonId(), kOldIdVNewIdMap));
            caseExpression.setId(null);
            caseExpression.setDt(new Date());
        });
    }

    private void getNewCaseIndicatorRuleList(List<CaseIndicatorRuleEntity> caseIndicatorRuleList,
                                             Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(caseIndicatorRuleList, kOldIdVNewIdMap, "null")) {
            return;
        }
        caseIndicatorRuleList.forEach(caseIndicatorRule -> {
            caseIndicatorRule.setCaseIndicatorRuleId(idGenerator.nextIdStr());
            caseIndicatorRule.setVariableId(checkNullNewId(caseIndicatorRule.getVariableId(), kOldIdVNewIdMap));
            caseIndicatorRule.setId(null);
            caseIndicatorRule.setDt(new Date());
        });

    }

    private void getNewCaseIndicatorInstanceList(List<CaseIndicatorInstanceEntity> caseIndicatorInstanceList,
                                                 String newAccountId, Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(caseIndicatorInstanceList, kOldIdVNewIdMap, newAccountId)) {
            return;
        }
        caseIndicatorInstanceList.forEach(caseIndicatorInstance -> {
            caseIndicatorInstance.setCaseIndicatorInstanceId(checkNullNewId(caseIndicatorInstance.getCaseIndicatorInstanceId(), kOldIdVNewIdMap));
            caseIndicatorInstance.setIndicatorCategoryId(checkNullNewId(caseIndicatorInstance.getIndicatorCategoryId(), kOldIdVNewIdMap));
            caseIndicatorInstance.setPrincipalId(newAccountId);
            caseIndicatorInstance.setId(null);
            caseIndicatorInstance.setDt(new Date());
        });
    }

    private void getNewCaseIndicatorCategoryEntityList(List<CaseIndicatorCategoryEntity> caseIndicatorCategoryEntityList,
                                                       Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(caseIndicatorCategoryEntityList, kOldIdVNewIdMap, "null")) {
            return;
        }
        caseIndicatorCategoryEntityList.forEach(caseIndicatorCategory -> {
            caseIndicatorCategory.setCaseIndicatorCategoryId(checkNullNewId(caseIndicatorCategory.getCaseIndicatorCategoryId(), kOldIdVNewIdMap));
            caseIndicatorCategory.setId(null);
            caseIndicatorCategory.setDt(new Date());
        });
    }

    private void gitNewCasPrincipalRefList(List<CaseIndicatorCategoryPrincipalRefEntity> casePrincipalRefList,
                                           String newAccountId, Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(casePrincipalRefList,  kOldIdVNewIdMap, newAccountId)) {
            return;
        }
        casePrincipalRefList.forEach(casePrincipalRef -> {
            casePrincipalRef.setPrincipalId(newAccountId);
            casePrincipalRef.setIndicatorCategoryId(checkNullNewId(casePrincipalRef.getIndicatorCategoryId(), kOldIdVNewIdMap));
            casePrincipalRef.setId(null);
            casePrincipalRef.setDt(new Date());
        });
    }

    /**
     * is null return false
     * else return true
     */
    private boolean checkNull(Object oldList, Map<String, String> kOldIdVNewIdMap,
                              String newAccount) {
        return Objects.isNull(oldList) || Objects.isNull(kOldIdVNewIdMap) || StringUtils.isEmpty(newAccount);
    }

    /**
     * id is blank return id
     * else return newId
     */
    public static String checkNullNewId(String id, Map<String, String> kOldIdVNewIdMap) {
        if (StringUtils.isBlank(id)||"0".equals(id)) {
            return id;
        }
        return kOldIdVNewIdMap.get(id);
    }

    /***
     * principalId 新的人物id
     */
    private void saveIndicatorInstanceAsyncV2(Set<String> allOldIdSet, Set<String> indicatorInstanceIdSet, String principalId,
                                              List<CaseIndicatorInstanceCategoryResponseRs> byPersonIdAndAppIdList
    ) throws ExecutionException, InterruptedException {
        //生成新的id
        Map<String, String> kOldIdVNewIdMap = new HashMap<>();
        CompletableFuture<Void> cfPopulateKOldIdVNewIdMap = CompletableFuture.runAsync(() -> {
            rsUtilBiz.populateKOldIdVNewIdMap(kOldIdVNewIdMap, allOldIdSet);
        });
        cfPopulateKOldIdVNewIdMap.get();

        //案例机构人物 指标模板
        List<CaseIndicatorCategoryEntity> caseIndicatorCategoryEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateCaseIndicatorCategoryList = CompletableFuture.runAsync(() -> {
            populateCaseIndicatorCategoryList(caseIndicatorCategoryEntityList, byPersonIdAndAppIdList, kOldIdVNewIdMap);
        });
        cfPopulateCaseIndicatorCategoryList.get();

        //机构人物与案例指标模板关系
        List<CaseIndicatorCategoryPrincipalRefEntity> caseIndicatorCategoryPrincipalRefEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateCaseIndicatorCategoryPrincipalRefList = CompletableFuture.runAsync(() -> {
            populateCaseIndicatorCategoryPrincipalRefList(caseIndicatorCategoryPrincipalRefEntityList, byPersonIdAndAppIdList, kOldIdVNewIdMap, principalId);
        });
        cfPopulateCaseIndicatorCategoryPrincipalRefList.get();

        //案例指标
        List<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateCaseIndicatorInstanceEntityList = CompletableFuture.runAsync(() -> {
            populateCaseIndicatorInstanceEntityList(caseIndicatorInstanceEntityList, byPersonIdAndAppIdList, kOldIdVNewIdMap, principalId);
        });
        cfPopulateCaseIndicatorInstanceEntityList.get();

        //案例指标分类与案例指标 关系
        List<CaseIndicatorCategoryRefEntity> caseIndicatorCategoryRefEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateCaseIndicatorCategoryRefList = CompletableFuture.runAsync(() -> {
            populateCaseIndicatorCategoryRefList(caseIndicatorCategoryRefEntityList, byPersonIdAndAppIdList, kOldIdVNewIdMap);
        });
        cfPopulateCaseIndicatorCategoryRefList.get();

        //案例指标规则
        List<CaseIndicatorRuleEntity> caseIndicatorRuleEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateCaseIndicatorRuleEntityList = CompletableFuture.runAsync(() -> {
            populateCaseIndicatorRuleEntityList(caseIndicatorRuleEntityList, byPersonIdAndAppIdList, kOldIdVNewIdMap);
        });
        cfPopulateCaseIndicatorRuleEntityList.get();

        //案例指标公式与案例指标 关系
        List<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefEntityList = new ArrayList<>();
        //案例指标公式
        List<CaseIndicatorExpressionEntity> caseIndicatorExpressionEntityList = new ArrayList<>();
        //案例指标细项
        List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateAllCaseIndicatorExpressionEntityList = CompletableFuture.runAsync(() -> {
            populateAllCaseIndicatorExpressionEntityList(caseIndicatorExpressionRefEntityList, caseIndicatorExpressionEntityList, caseIndicatorExpressionItemEntityList, byPersonIdAndAppIdList, kOldIdVNewIdMap);
        });
        cfPopulateAllCaseIndicatorExpressionEntityList.get();

        //指标库与案例关系
        List<IndicatorExpressionInfluenceEntity> indicatorExpressionInfluenceEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateIndicatorExpressionInfluenceEntityList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateIndicatorExpressionInfluenceEntityList(indicatorExpressionInfluenceEntityList, indicatorInstanceIdSet);
        });
        cfPopulateIndicatorExpressionInfluenceEntityList.get();

        //案例库与案例指标关系
        List<CaseIndicatorExpressionInfluenceEntity> caseIndicatorExpressionInfluenceEntityList = new ArrayList<>();
        Set<String> indicatorExpressionInfluenceSet = indicatorExpressionInfluenceEntityList.stream().map(IndicatorExpressionInfluenceEntity::getIndicatorExpressionInfluenceId).collect(Collectors.toSet());
        CompletableFuture<Void> cfPopulateCaseIndicatorExpressionInfluenceEntityList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateCaseIndicatorExpressionInfluenceEntityList(caseIndicatorExpressionInfluenceEntityList, indicatorExpressionInfluenceSet, kOldIdVNewIdMap);
        });
        cfPopulateCaseIndicatorExpressionInfluenceEntityList.get();

        caseIndicatorCategoryPrincipalRefService.saveOrUpdateBatch(caseIndicatorCategoryPrincipalRefEntityList);
        caseIndicatorCategoryService.saveOrUpdateBatch(caseIndicatorCategoryEntityList);
        caseIndicatorInstanceService.saveOrUpdateBatch(caseIndicatorInstanceEntityList);
        caseIndicatorCategoryRefService.saveOrUpdateBatch(caseIndicatorCategoryRefEntityList);
        caseIndicatorRuleService.saveOrUpdateBatch(caseIndicatorRuleEntityList);
        caseIndicatorExpressionRefService.saveOrUpdateBatch(caseIndicatorExpressionRefEntityList);
        caseIndicatorExpressionService.saveOrUpdateBatch(caseIndicatorExpressionEntityList);
        caseIndicatorExpressionItemService.saveOrUpdateBatch(caseIndicatorExpressionItemEntityList);
        caseIndicatorExpressionInfluenceService.saveOrUpdateBatch(caseIndicatorExpressionInfluenceEntityList);

    }

    public void populateAllCaseIndicatorExpressionEntityList(
            List<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefEntityList,
            List<CaseIndicatorExpressionEntity> caseIndicatorExpressionEntityList,
            List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList,
            List<CaseIndicatorInstanceCategoryResponseRs> caseIndicatorInstanceCategoryList,
            Map<String, String> kOldIdVNewIdMap
    ) {
        caseIndicatorInstanceCategoryList.forEach(indicatorInstanceCategoryResponseRs -> {
            List<CaseIndicatorInstanceResponseRs> caseIndicatorInstanceList = indicatorInstanceCategoryResponseRs.getCaseIndicatorInstanceResponseRsList();
            if (CollectionUtils.isEmpty(caseIndicatorInstanceList)) {
                return;
            }
            caseIndicatorInstanceList.forEach(indicatorInstanceResponseRs -> {
                String indicatorInstanceId = indicatorInstanceResponseRs.getIndicatorInstanceId();
                List<CaseIndicatorExpressionResponseRs> caseIndicatorExpressionList = indicatorInstanceResponseRs.getCaseIndicatorExpressionResponseRsList();
                if (Objects.isNull(caseIndicatorExpressionList) || caseIndicatorExpressionList.isEmpty()) {
                    return;
                }
                caseIndicatorExpressionList.forEach(caseIndicatorExpression -> {
                    String indicatorExpressionId = caseIndicatorExpression.getIndicatorExpressionId();
                    String indicatorExpressionRefId = caseIndicatorExpression.getIndicatorExpressionRefId();
                    String principalId = caseIndicatorExpression.getPrincipalId();
                    //新的案例指标公式细项
                    String caseMinIndicatorExpressionItemId = null;
                    String caseMaxIndicatorExpressionItemId = null;
                    CaseIndicatorExpressionItemResponseRs minCaseIndicatorExpressionItem = caseIndicatorExpression.getCaseMinIndicatorExpressionItemResponseRs();
                    if (Objects.nonNull(minCaseIndicatorExpressionItem)) {
                        String minIndicatorExpressionItemId = minCaseIndicatorExpressionItem.getIndicatorExpressionItemId();
                        caseMinIndicatorExpressionItemId = kOldIdVNewIdMap.get(minIndicatorExpressionItemId);
                        CaseIndicatorExpressionItemEntity minCaseIndicatorExpressionItemEntity = convertIndicatorExpressionItemResponseRs2Case(minCaseIndicatorExpressionItem, kOldIdVNewIdMap);
                        if (Objects.nonNull(minCaseIndicatorExpressionItemEntity)) {
                            caseIndicatorExpressionItemEntityList.add(minCaseIndicatorExpressionItemEntity);
                        }
                    }
                    CaseIndicatorExpressionItemResponseRs maxCaseIndicatorExpressionItem = caseIndicatorExpression.getCaseMaxIndicatorExpressionItemResponseRs();
                    if (Objects.nonNull(maxCaseIndicatorExpressionItem)) {
                        String maxIndicatorExpressionItemId = maxCaseIndicatorExpressionItem.getIndicatorExpressionItemId();
                        caseMaxIndicatorExpressionItemId = kOldIdVNewIdMap.get(maxIndicatorExpressionItemId);
                        CaseIndicatorExpressionItemEntity maxCaseIndicatorExpressionItemEntity = convertIndicatorExpressionItemResponseRs2Case(maxCaseIndicatorExpressionItem, kOldIdVNewIdMap);
                        if (Objects.nonNull(maxCaseIndicatorExpressionItemEntity)) {
                            caseIndicatorExpressionItemEntityList.add(maxCaseIndicatorExpressionItemEntity);
                        }
                    }
                    caseIndicatorExpressionRefEntityList.add(CaseIndicatorExpressionRefEntity
                            .builder()
                            .caseIndicatorExpressionRefId(kOldIdVNewIdMap.get(indicatorExpressionRefId))
                            .indicatorExpressionRefId(indicatorExpressionRefId)
                            .appId(caseIndicatorExpression.getAppId())
                            .indicatorExpressionId(kOldIdVNewIdMap.get(indicatorExpressionId))
                            .reasonId(kOldIdVNewIdMap.get(indicatorInstanceId))
                            .build());
                    caseIndicatorExpressionEntityList.add(CaseIndicatorExpressionEntity
                            .builder()
                            .caseIndicatorExpressionId(indicatorExpressionId)
                            .indicatorExpressionId(kOldIdVNewIdMap.get(indicatorExpressionId))
                            .appId(caseIndicatorExpression.getAppId())
                            .casePrincipalId(kOldIdVNewIdMap.get(principalId))
                            .principalId(principalId)
                            .minIndicatorExpressionItemId(caseMinIndicatorExpressionItemId)
                            .maxIndicatorExpressionItemId(caseMaxIndicatorExpressionItemId)
                            .type(caseIndicatorExpression.getType())
                            .source(caseIndicatorExpression.getSource())
                            .build());
                    List<CaseIndicatorExpressionItemResponseRs> caseIndicatorExpressionItemList = caseIndicatorExpression.getCaseIndicatorExpressionItemResponseRsList();
                    if (Objects.isNull(caseIndicatorExpressionItemList) || caseIndicatorExpressionItemList.isEmpty()) {
                        return;
                    }
                    caseIndicatorExpressionItemList.forEach(indicatorExpressionItemResponseRs -> {
                        CaseIndicatorExpressionItemEntity caseIndicatorExpressionItemEntity = convertIndicatorExpressionItemResponseRs2Case(indicatorExpressionItemResponseRs, kOldIdVNewIdMap);
                        if (Objects.nonNull(caseIndicatorExpressionItemEntity)) {
                            caseIndicatorExpressionItemEntityList.add(caseIndicatorExpressionItemEntity);
                        }
                    });
                });
            });
        });
    }

    public CaseIndicatorExpressionItemEntity convertIndicatorExpressionItemResponseRs2Case(
            CaseIndicatorExpressionItemResponseRs caseIndicatorExpressionItem,
            Map<String, String> kOldIdVNewIdMap
    ) {
        if (Objects.isNull(caseIndicatorExpressionItem)
                || Objects.isNull(kOldIdVNewIdMap)
        ) {
            return null;
        }
        String indicatorExpressionItemId = caseIndicatorExpressionItem.getIndicatorExpressionItemId();
        String indicatorExpressionId = caseIndicatorExpressionItem.getIndicatorExpressionId();
        String caseIndicatorExpressionId = null;
        if (Objects.nonNull(indicatorExpressionId)) {
            caseIndicatorExpressionId = kOldIdVNewIdMap.get(indicatorExpressionId);
        }
        return CaseIndicatorExpressionItemEntity
                .builder()
                .caseIndicatorExpressionItemId(indicatorExpressionItemId)
                .indicatorExpressionItemId(kOldIdVNewIdMap.get(indicatorExpressionItemId))
                .appId(caseIndicatorExpressionItem.getAppId())
                .indicatorExpressionId(caseIndicatorExpressionId)
                .conditionRaw(caseIndicatorExpressionItem.getConditionRaw())
                .conditionExpression(caseIndicatorExpressionItem.getConditionExpression())
                .conditionNameList(caseIndicatorExpressionItem.getConditionNameList())
                .conditionValList(rsCaseIndicatorInstanceBiz.convertConditionValList2Case(caseIndicatorExpressionItem.getConditionValList(), kOldIdVNewIdMap))
                .resultRaw(caseIndicatorExpressionItem.getResultRaw())
                .resultExpression(caseIndicatorExpressionItem.getResultExpression())
                .resultNameList(caseIndicatorExpressionItem.getResultNameList())
                .resultValList(rsCaseIndicatorInstanceBiz.convertResultValList2Case(caseIndicatorExpressionItem.getResultValList(), kOldIdVNewIdMap))
                .seq(caseIndicatorExpressionItem.getSeq())
                .build();
    }

    public void populateCaseIndicatorRuleEntityList(
            List<CaseIndicatorRuleEntity> caseIndicatorRuleEntityList,
            List<CaseIndicatorInstanceCategoryResponseRs> caseIndicatorInstanceCategoryList,
            Map<String, String> kOldIdVNewIdMap
    ) {
        if (Objects.isNull(caseIndicatorRuleEntityList)
                || Objects.isNull(caseIndicatorInstanceCategoryList) || caseIndicatorInstanceCategoryList.isEmpty()
                || Objects.isNull(kOldIdVNewIdMap)
        ) {
            return;
        }
        caseIndicatorInstanceCategoryList.forEach(indicatorInstanceCategoryResponseRs -> {
            List<CaseIndicatorInstanceResponseRs> caseIndicatorInstanceList = indicatorInstanceCategoryResponseRs.getCaseIndicatorInstanceResponseRsList();
            if (Objects.isNull(caseIndicatorInstanceList) || caseIndicatorInstanceList.isEmpty()) {
                return;
            }
            caseIndicatorInstanceList.forEach(indicatorInstanceResponseRs -> {
                String indicatorInstanceId = indicatorInstanceResponseRs.getIndicatorInstanceId();
                caseIndicatorRuleEntityList.add(CaseIndicatorRuleEntity
                        .builder()
                        .caseIndicatorRuleId(idGenerator.nextIdStr())
                        .appId(indicatorInstanceCategoryResponseRs.getAppId())
                        .variableId(kOldIdVNewIdMap.get(indicatorInstanceId))
                        .ruleType(EnumIndicatorRuleType.INDICATOR.getCode())
                        .min(indicatorInstanceResponseRs.getMin())
                        .max(indicatorInstanceResponseRs.getMax())
                        .def(indicatorInstanceResponseRs.getDef())
                        .descr(indicatorInstanceResponseRs.getDescr())
                        .build());
            });
        });
    }

    public void populateCaseIndicatorCategoryRefList(
            List<CaseIndicatorCategoryRefEntity> caseIndicatorCategoryRefEntityList,
            List<CaseIndicatorInstanceCategoryResponseRs> caseIndicatorInstanceCategoryList,
            Map<String, String> kOldIdVNewIdMap
    ) {
        if (Objects.isNull(caseIndicatorCategoryRefEntityList)
                || Objects.isNull(caseIndicatorInstanceCategoryList) || caseIndicatorInstanceCategoryList.isEmpty()
                || Objects.isNull(kOldIdVNewIdMap)
        ) {
            return;
        }
        caseIndicatorInstanceCategoryList.forEach(indicatorInstanceCategoryResponseRs -> {
            String indicatorCategoryId = indicatorInstanceCategoryResponseRs.getIndicatorCategoryId();
            List<CaseIndicatorInstanceResponseRs> caseIndicatorInstanceResponseRsList = indicatorInstanceCategoryResponseRs.getCaseIndicatorInstanceResponseRsList();
            if (Objects.isNull(caseIndicatorInstanceResponseRsList) || caseIndicatorInstanceResponseRsList.isEmpty()) {
                return;
            }
            caseIndicatorInstanceResponseRsList.forEach(indicatorInstanceResponseRs -> {
                String indicatorInstanceId = indicatorInstanceResponseRs.getIndicatorInstanceId();
                caseIndicatorCategoryRefEntityList.add(CaseIndicatorCategoryRefEntity
                        .builder()
                        .caseIndicatorCategoryRefId(idGenerator.nextIdStr())
                        .appId(indicatorInstanceCategoryResponseRs.getAppId())
                        .indicatorCategoryId(kOldIdVNewIdMap.get(indicatorCategoryId))
                        .indicatorInstanceId(kOldIdVNewIdMap.get(indicatorInstanceId))
                        .seq(indicatorInstanceResponseRs.getSeq())
                        .build());
            });
        });
    }

    public void populateCaseIndicatorInstanceEntityList(
            List<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityList,
            List<CaseIndicatorInstanceCategoryResponseRs> caseIndicatorInstanceCategoryList,
            Map<String, String> kOldIdVNewIdMap,
            String principalId
    ) {
        if (Objects.isNull(caseIndicatorInstanceEntityList)
                || Objects.isNull(caseIndicatorInstanceCategoryList) || caseIndicatorInstanceCategoryList.isEmpty()
                || Objects.isNull(kOldIdVNewIdMap)
                || StringUtils.isBlank(principalId)
        ) {
            return;
        }
        caseIndicatorInstanceCategoryList.forEach(indicatorInstanceCategory -> {
            List<CaseIndicatorInstanceResponseRs> caseIndicatorInstanceResponseRsList = indicatorInstanceCategory.getCaseIndicatorInstanceResponseRsList();
            if (Objects.isNull(caseIndicatorInstanceResponseRsList) || caseIndicatorInstanceResponseRsList.isEmpty()) {
                return;
            }
            String indicatorCategoryId = indicatorInstanceCategory.getIndicatorCategoryId();
            caseIndicatorInstanceResponseRsList.forEach(caseIndicatorInstance -> {
                String indicatorInstanceId = caseIndicatorInstance.getIndicatorInstanceId();
                caseIndicatorInstanceEntityList.add(CaseIndicatorInstanceEntity
                        .builder()
                        .caseIndicatorInstanceId(kOldIdVNewIdMap.get(indicatorInstanceId))
                        .indicatorInstanceId(indicatorInstanceId)
                        .appId(caseIndicatorInstance.getAppId())
                        .principalId(principalId)
                        .indicatorCategoryId(kOldIdVNewIdMap.get(indicatorCategoryId))
                        .indicatorName(caseIndicatorInstance.getIndicatorName())
                        .displayByPercent(caseIndicatorInstance.getDisplayByPercent())
                        .unit(caseIndicatorInstance.getUnit())
                        .core(caseIndicatorInstance.getCore())
                        .descr(caseIndicatorInstance.getDescr())
                        .build());
            });
        });
    }

    public void populateCaseIndicatorCategoryList(
            List<CaseIndicatorCategoryEntity> caseIndicatorCategoryEntityList,
            List<CaseIndicatorInstanceCategoryResponseRs> caseIndicatorInstanceCategoryList,
            Map<String, String> kOldIdVNewIdMap
    ) {
        if (Objects.isNull(caseIndicatorCategoryEntityList)
                || Objects.isNull(caseIndicatorInstanceCategoryList) || caseIndicatorInstanceCategoryList.isEmpty()
                || Objects.isNull(kOldIdVNewIdMap)
        ) {
            return;
        }
        caseIndicatorInstanceCategoryList.forEach(indicatorInstanceCategoryResponseRs -> {
            String indicatorCategoryId = indicatorInstanceCategoryResponseRs.getIndicatorCategoryId();
            caseIndicatorCategoryEntityList.add(CaseIndicatorCategoryEntity
                    .builder()
                    .caseIndicatorCategoryId(kOldIdVNewIdMap.get(indicatorCategoryId))
                    .indicatorCategoryId(indicatorCategoryId)
                    .appId(indicatorInstanceCategoryResponseRs.getAppId())
                    .pid(indicatorInstanceCategoryResponseRs.getPid())
                    .categoryName(indicatorInstanceCategoryResponseRs.getCategoryName())
                    .seq(indicatorInstanceCategoryResponseRs.getSeq())
                    .build());
        });
    }

    public void populateCaseIndicatorCategoryPrincipalRefList(
            List<CaseIndicatorCategoryPrincipalRefEntity> caseIndicatorCategoryPrincipalRefEntityList,
            List<CaseIndicatorInstanceCategoryResponseRs> caseIndicatorInstanceCategoryList,
            Map<String, String> kOldIdVNewIdMap,
            String principalId
    ) {
        if (Objects.isNull(caseIndicatorCategoryPrincipalRefEntityList)
                || Objects.isNull(caseIndicatorInstanceCategoryList) || caseIndicatorInstanceCategoryList.isEmpty()
                || Objects.isNull(kOldIdVNewIdMap)
                || StringUtils.isBlank(principalId)
        ) {
            return;
        }
        caseIndicatorInstanceCategoryList.forEach(indicatorInstanceCategoryResponseRs -> {
            String indicatorCategoryId = indicatorInstanceCategoryResponseRs.getIndicatorCategoryId();
            caseIndicatorCategoryPrincipalRefEntityList.add(CaseIndicatorCategoryPrincipalRefEntity
                    .builder()
                    .caseIndicatorCategoryPrincipalRefId(idGenerator.nextIdStr())
                    .appId(indicatorInstanceCategoryResponseRs.getAppId())
                    .principalId(principalId)
                    .indicatorCategoryId(kOldIdVNewIdMap.get(indicatorCategoryId))
                    .build());
        });
    }

    private void saveIndicatorInstanceAsync(Set<String> allOldIdSet,
                                            Set<String> indicatorInstanceIdSet,
                                            String principalId,
                                            List<IndicatorInstanceCategoryResponseRs> indicatorInstanceCategoryResponseRsList
    ) throws ExecutionException, InterruptedException {
        //生成新的id
        Map<String, String> kOldIdVNewIdMap = new HashMap<>();
        CompletableFuture<Void> cfPopulateKOldIdVNewIdMap = CompletableFuture.runAsync(() -> {
            rsUtilBiz.populateKOldIdVNewIdMap(kOldIdVNewIdMap, allOldIdSet);
        });
        cfPopulateKOldIdVNewIdMap.get();
        //指标模板复制
        List<CaseIndicatorCategoryEntity> caseIndicatorCategoryEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateCaseIndicatorCategoryList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateCaseIndicatorCategoryList(caseIndicatorCategoryEntityList, indicatorInstanceCategoryResponseRsList, kOldIdVNewIdMap);
        });
        cfPopulateCaseIndicatorCategoryList.get();

        List<CaseIndicatorCategoryRefEntity> caseIndicatorCategoryRefEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateCaseIndicatorCategoryRefList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateCaseIndicatorCategoryRefList(caseIndicatorCategoryRefEntityList, indicatorInstanceCategoryResponseRsList, kOldIdVNewIdMap);
        });
        cfPopulateCaseIndicatorCategoryRefList.get();

        List<CaseIndicatorCategoryPrincipalRefEntity> caseIndicatorCategoryPrincipalRefEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateCaseIndicatorCategoryPrincipalRefList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateCaseIndicatorCategoryPrincipalRefList(caseIndicatorCategoryPrincipalRefEntityList, indicatorInstanceCategoryResponseRsList, kOldIdVNewIdMap, principalId);
        });
        cfPopulateCaseIndicatorCategoryPrincipalRefList.get();

        List<CaseIndicatorRuleEntity> caseIndicatorRuleEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateCaseIndicatorRuleEntityList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateCaseIndicatorRuleEntityList(caseIndicatorRuleEntityList, indicatorInstanceCategoryResponseRsList, kOldIdVNewIdMap);
        });
        cfPopulateCaseIndicatorRuleEntityList.get();

        List<CaseIndicatorInstanceEntity> caseIndicatorInstanceEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateCaseIndicatorInstanceEntityList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateCaseIndicatorInstanceEntityList(caseIndicatorInstanceEntityList, indicatorInstanceCategoryResponseRsList, kOldIdVNewIdMap, principalId);
        });
        cfPopulateCaseIndicatorInstanceEntityList.get();

        List<CaseIndicatorExpressionRefEntity> caseIndicatorExpressionRefEntityList = new ArrayList<>();
        List<CaseIndicatorExpressionEntity> caseIndicatorExpressionEntityList = new ArrayList<>();
        List<CaseIndicatorExpressionItemEntity> caseIndicatorExpressionItemEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateAllCaseIndicatorExpressionEntityList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateAllCaseIndicatorExpressionEntityList(caseIndicatorExpressionRefEntityList, caseIndicatorExpressionEntityList, caseIndicatorExpressionItemEntityList, indicatorInstanceCategoryResponseRsList, kOldIdVNewIdMap);
        });
        cfPopulateAllCaseIndicatorExpressionEntityList.get();

        List<IndicatorExpressionInfluenceEntity> indicatorExpressionInfluenceEntityList = new ArrayList<>();
        CompletableFuture<Void> cfPopulateIndicatorExpressionInfluenceEntityList = CompletableFuture.runAsync(() -> {
            rsCaseIndicatorInstanceBiz.populateIndicatorExpressionInfluenceEntityList(indicatorExpressionInfluenceEntityList, indicatorInstanceIdSet);
        });
        cfPopulateIndicatorExpressionInfluenceEntityList.get();

        List<CaseIndicatorExpressionInfluenceEntity> caseIndicatorExpressionInfluenceEntityList = new ArrayList<>();
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
        //TODO 最后
    }


}
