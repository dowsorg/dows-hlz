package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
        if (checkNullObject(caseIndicatorInfluenceList, kOldIdVNewIdMap)) {
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
        if (checkNullObject(caseIndicatorCategoryRefList, kOldIdVNewIdMap)) {
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
        if (checkNullObject(caseExpressionItemList, kOldIdVNewIdMap)) {
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
        if (checkNullObject(caseIndicatorExpressionList, kOldIdVNewIdMap)) {
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
        if (checkNullObject(caseIndicatorExpressionRefList, kOldIdVNewIdMap)) {
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
        if (checkNullObject(caseIndicatorRuleList, kOldIdVNewIdMap)) {
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
        if (checkNullObject(caseIndicatorInstanceList, kOldIdVNewIdMap, newAccountId)) {
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
        if (checkNullObject(caseIndicatorCategoryEntityList, kOldIdVNewIdMap)) {
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
        if (checkNullObject(casePrincipalRefList, kOldIdVNewIdMap, newAccountId)) {
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
    private boolean checkNullObject(Object oldList, Map<String, String> kOldIdVNewIdMap) {
        return Objects.isNull(oldList) || Objects.isNull(kOldIdVNewIdMap);
    }
    private boolean checkNullObject(Object oldList, Map<String, String> kOldIdVNewIdMap,
                                    String newAccount) {
        return StringUtils.isEmpty(newAccount) || checkNullObject(oldList, kOldIdVNewIdMap);
    }

    /**
     * id is blank return id
     * else return newId
     */
    public static String checkNullNewId(String id, Map<String, String> kOldIdVNewIdMap) {
        if (StringUtils.isBlank(id) || "0".equals(id)) {
            return id;
        }
        return kOldIdVNewIdMap.get(id);
    }

}
