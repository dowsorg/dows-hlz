package org.dows.hep.biz.eval.sync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.entity.AccountInstance;
import org.dows.hep.api.base.indicator.request.CaseRsCalculateHealthScoreRequestRs;
import org.dows.hep.api.base.indicator.request.SyncIndicatorRequest;
import org.dows.hep.api.enums.EnumIndicatorCategory;
import org.dows.hep.biz.dao.*;
import org.dows.hep.biz.eval.EvalCaseHealthIndexBiz;
import org.dows.hep.biz.extend.uim.XAccountInstanceApi;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

/**
 * 一键同步
 *
 * @author : wuzl
 * @date : 2023/10/10 10:01
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SyncPersonBiz {

    private static final String APPId="3";
    private static final int BATCHSize4Account=50;
    private static final int CONCURRENTNum=4;
    private final IdGenerator idGenerator;

    private final IndicatorInstanceDao indicatorInstanceDao;

    private final IndicatorCategoryService indicatorCategoryService;

    private final IndicatorCategoryRefService indicatorCategoryRefService;

    private final IndicatorRuleDao indicatorRuleDao;

    private final IndicatorExpressionRefDao indicatorExpressionRefDao;

    private final IndicatorExpressionDao indicatorExpressionDao;

    private final IndicatorExpressionInfluenceDao indicatorExpressionInfluenceDao;

    private final CaseIndicatorInstanceDao caseIndicatorInstanceDao;

    private final CaseIndicatorCategoryService caseIndicatorCategoryService;
    private final CaseIndicatorCategoryPrincipalRefService caseIndicatorCategoryPrincipalRefService;
    private final CaseIndicatorCategoryRefService caseIndicatorCategoryRefService;
    private final CaseIndicatorRuleDao caseIndicatorRuleDao;

    private final CaseIndicatorExpressionRefDao caseIndicatorExpressionRefDao;

    private final CaseIndicatorExpressionDao caseIndicatorExpressionDao;

    private final CaseIndicatorExpressionInfluenceDao caseIndicatorExpressionInfluenceDao;

    private final XAccountInstanceApi xAccountInstanceApi;

    private final EvalCaseHealthIndexBiz evalCaseHealthIndexBiz;

    //region 一键同步所有指标
    public boolean syncAllPerson(SyncIndicatorRequest req){
        SyncSourcePack sourcePack=loadSource();
        Set<String> baseAccountIds=loadAccountIds(false);
        Set<String> caseAccountIds=loadAccountIds(true);
        syncPersonIndicatorGroup(sourcePack, new ArrayList<>(baseAccountIds));
        syncPersonIndicatorGroup(sourcePack, new ArrayList<>(caseAccountIds));
        sourcePack.clear();
        return true;
    }

    private boolean syncPersonIndicatorGroup(SyncSourcePack sourcePack,List<String> accountIds){
        List<List<String>> batchAccountIds=ShareUtil.XCollection.splitByBatchSize(accountIds, BATCHSize4Account);
        batchAccountIds.forEach(i->syncPersonIndicatorBatch(sourcePack, i));
        return true;
    }
    private boolean syncPersonIndicatorBatch(SyncSourcePack sourcePack,Collection<String> accountIds){
        Map<String, SyncCurrentPack>  mapCurrent= loadCurrent(accountIds);
        if(ShareUtil.XObject.isEmpty(mapCurrent)){
            return true;
        }
        SyncTargetPack targetPack=new SyncTargetPack(idGenerator);
        for(SyncCurrentPack item:mapCurrent.values() ){
            item.fillNewIds(sourcePack);
            targetPack.fillData(sourcePack, item,true);
            item.clear();
        }
        boolean rst=saveTarget(targetPack);
        targetPack.clear();
        return rst;
    }
    //endregion

    //region 单个案例同步
    public boolean syncOnePerson(SyncIndicatorRequest req){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(req.getAccountId()))
                .throwMessage("人物ID不可为空");
        SyncSourcePack sourcePack=loadSource();
        SyncCurrentPack syncCurrentPack = loadCurrent(req.getAccountId(),null)
                .fillNewIds(sourcePack);
        SyncTargetPack targetPack=new SyncTargetPack(idGenerator)
                .coverData(sourcePack, syncCurrentPack, true);
        boolean rst=saveTarget(targetPack,true);
        targetPack.clear();
        evalCaseHealthIndexBiz.evalCasePersonHealthIndex(CaseRsCalculateHealthScoreRequestRs
                .builder()
                .appId(APPId)
                .accountId(req.getAccountId())
                .build());
        return rst;
    }
    //endregion

    //region 单个指标同步
    public boolean syncOneIndicator(SyncIndicatorRequest req){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(req.getAccountId()))
                .throwMessage("人物ID不可为空");
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(req.getCaseIndicatorId()))
                .throwMessage("人物指标ID不可为空");
        SyncCurrentPack currentPack = loadCurrent(req.getAccountId(),req.getCaseIndicatorId());
        final String indicatorId= AssertUtil.getNotNull(currentPack.getCurCaseIndicatorIds().get(req.getCaseIndicatorId()))
                .orElseThrow("未找到当前人物指标数据");
        SyncSourcePack sourcePack=loadSource();
        AssertUtil.getNotNull(sourcePack.getMapIndicator().get(indicatorId))
                .orElseThrow("该指标不是来源于数据库,无需同步");
        currentPack.fillNewIds(sourcePack);
        SyncTargetPack targetPack=new SyncTargetPack(idGenerator)
                .coverData(sourcePack, currentPack, indicatorId);
        boolean rst=saveTarget(targetPack);
        targetPack.clear();
        evalCaseHealthIndexBiz.evalCasePersonHealthIndex(CaseRsCalculateHealthScoreRequestRs
                .builder()
                .appId(APPId)
                .accountId(req.getAccountId())
                .build());
        return rst;
    }
    //endreigon


    private Set<String> loadAccountIds(boolean orgPersonFlag){
        final String source=orgPersonFlag?"机构人物":"人物管理";
        return ShareUtil.XCollection.toSet(xAccountInstanceApi.getAccountInstancesBySource(source, AccountInstance::getAccountId),
                AccountInstance::getAccountId);
    }

    private SyncCurrentPack loadCurrent(String accountId,String caseIndicatorId){
        SyncCurrentPack rst=new SyncCurrentPack(accountId,true);
        Set<String> caseIndicatorCategIds=new HashSet<>();
        caseIndicatorInstanceDao.getByAccountIds(Set.of(accountId),
                        CaseIndicatorInstanceEntity::getId,
                        CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId,
                        CaseIndicatorInstanceEntity::getIndicatorInstanceId,
                        CaseIndicatorInstanceEntity::getIndicatorCategoryId,
                        CaseIndicatorInstanceEntity::getPrincipalId)
                .forEach(i-> {
                    rst.fillIndicator(i);
                    caseIndicatorCategIds.add(i.getIndicatorCategoryId());
                });
        if(ShareUtil.XObject.notEmpty(caseIndicatorId)
            &&!rst.getCurCaseIndicatorIds().containsKey(caseIndicatorId)) {
            return rst;
        }
        caseIndicatorCategoryPrincipalRefService.lambdaQuery()
                .eq(CaseIndicatorCategoryPrincipalRefEntity::getPrincipalId,accountId)
                .orderByAsc(CaseIndicatorCategoryPrincipalRefEntity::getPrincipalId)
                .select(CaseIndicatorCategoryPrincipalRefEntity::getPrincipalId,
                        CaseIndicatorCategoryPrincipalRefEntity::getIndicatorCategoryId)
                .list()
                .forEach(i->caseIndicatorCategIds.add(i.getIndicatorCategoryId()));
        if(ShareUtil.XObject.notEmpty(caseIndicatorCategIds)) {
            caseIndicatorCategoryService.lambdaQuery()
                    .in(CaseIndicatorCategoryEntity::getCaseIndicatorCategoryId, caseIndicatorCategIds)
                    .isNotNull(CaseIndicatorCategoryEntity::getIndicatorCategoryId)
                    .list()
                    .forEach(i->rst.fillIndicaorCatgory(i));
        }
        caseIndicatorCategIds.clear();
        Set<String> caseIndicatorIds=ShareUtil.XObject.notEmpty(caseIndicatorId)?Set.of(caseIndicatorId):rst.getCurCaseIndicatorIds().keySet();
        caseIndicatorRuleDao.getByIndicatorIds(caseIndicatorIds )
                .forEach(i->rst.fillIndicatorRule(i));
        caseIndicatorExpressionRefDao.getByReasonId(APPId, caseIndicatorIds)
                .forEach(i->rst.fillExpressionRef(i));
        caseIndicatorExpressionDao.getByIndicatorId(caseIndicatorIds)
                .forEach(i->rst.fillExpression(i));
        caseIndicatorExpressionInfluenceDao.getByIndicatorIds(caseIndicatorIds)
                .forEach(i->rst.fillExpressionInfluence(i));
        return rst;
    }



    private Map<String, SyncCurrentPack> loadCurrent(Collection<String> accountIds){
        if(ShareUtil.XObject.isEmpty(accountIds)){
            return Collections.emptyMap();
        }
        Map<String, SyncCurrentPack> rst=ShareUtil.XCollection.toMap(accountIds, Function.identity(), k->new SyncCurrentPack(k,false));
        Map<String,String> mapAccountId=new HashMap<>();
        Set<String> caseIndicatorCategIds=new HashSet<>();
        caseIndicatorInstanceDao.getByAccountIds(accountIds,
                CaseIndicatorInstanceEntity::getId,
                CaseIndicatorInstanceEntity::getCaseIndicatorInstanceId,
                CaseIndicatorInstanceEntity::getIndicatorInstanceId,
                CaseIndicatorInstanceEntity::getIndicatorCategoryId,
                CaseIndicatorInstanceEntity::getPrincipalId)
                .forEach(i-> {
                    rst.get(i.getPrincipalId()).fillIndicator(i);
                    mapAccountId.put(i.getIndicatorCategoryId(), i.getPrincipalId());
                    caseIndicatorCategIds.add(i.getIndicatorCategoryId());
                });
        caseIndicatorCategoryPrincipalRefService.lambdaQuery()
                .in(CaseIndicatorCategoryPrincipalRefEntity::getPrincipalId,accountIds)
                .orderByAsc(CaseIndicatorCategoryPrincipalRefEntity::getPrincipalId)
                .select(CaseIndicatorCategoryPrincipalRefEntity::getPrincipalId,
                        CaseIndicatorCategoryPrincipalRefEntity::getIndicatorCategoryId)
                .list()
                .forEach(i->{
                    mapAccountId.put(i.getIndicatorCategoryId(), i.getPrincipalId());
                    caseIndicatorCategIds.add(i.getIndicatorCategoryId());
                });
        if(ShareUtil.XObject.notEmpty(caseIndicatorCategIds)) {
            caseIndicatorCategoryService.lambdaQuery()
                    .in(CaseIndicatorCategoryEntity::getCaseIndicatorCategoryId, caseIndicatorCategIds)
                    .isNotNull(CaseIndicatorCategoryEntity::getIndicatorCategoryId)
                    .select(CaseIndicatorCategoryEntity::getCaseIndicatorCategoryId,
                            CaseIndicatorCategoryEntity::getIndicatorCategoryId)
                    .list()
                    .forEach(i->rst.get(mapAccountId.get(i.getCaseIndicatorCategoryId())).fillIndicaorCatgory(i));
        }
        mapAccountId.clear();
        caseIndicatorCategIds.clear();
        return rst;
    }

    private SyncSourcePack loadSource(){
        SyncSourcePack sourcePack=new SyncSourcePack();
        sourcePack.fillIndicaorCatgory(indicatorCategoryService.lambdaQuery()
                .eq(IndicatorCategoryEntity::getPid, EnumIndicatorCategory.INDICATOR_MANAGEMENT.getCode())
                .list());
        final Collection<String> indicatorCategIds=sourcePack.getMapIndicaorCatgory().keySet();
        sourcePack.fillIndicatorCategoryRef(indicatorCategoryRefService.lambdaQuery()
                .in(IndicatorCategoryRefEntity::getIndicatorCategoryId,indicatorCategIds)
                .select(IndicatorCategoryRefEntity::getIndicatorCategoryId,
                        IndicatorCategoryRefEntity::getIndicatorInstanceId,
                        IndicatorCategoryRefEntity::getSeq)
                .list());
        sourcePack.fillIndicator(indicatorInstanceDao.getIndicatorsByCategIds(indicatorCategIds));
        final Collection<String> indicatorIds=sourcePack.getMapIndicator().keySet();
        sourcePack.fillIndicatorRule(indicatorRuleDao.getByIndicatorIds(indicatorIds));
        sourcePack.fillExpressionRef(indicatorExpressionRefDao.getByReasonId(APPId, indicatorIds));
        sourcePack.fillExpressionInfluence( indicatorExpressionInfluenceDao.getByIndicatorIds(indicatorIds));
        final Collection<String> expressionIds=ShareUtil.XCollection.toSet(sourcePack.getMapExpressionRef().values(), IndicatorExpressionRefEntity::getIndicatorExpressionId);
        sourcePack.fillExpression(indicatorExpressionDao.getByExpressionId(expressionIds, null));
        final Set<String> expressionItemIds=new HashSet<>();
        sourcePack.getMapExpression().values().forEach(i->{
            if(ShareUtil.XObject.notEmpty(i.getMinIndicatorExpressionItemId())){
                expressionItemIds.add(i.getMinIndicatorExpressionItemId());
            }
            if(ShareUtil.XObject.notEmpty(i.getMaxIndicatorExpressionItemId())){
                expressionItemIds.add(i.getMaxIndicatorExpressionItemId());
            }
        });
        sourcePack.fillExpressionItem(indicatorExpressionDao.getSubByExpressionId(expressionIds));
        sourcePack.fillExpressionItem(indicatorExpressionDao.getSubBySubIds(expressionItemIds));
        return sourcePack.sortExpressionItem();
    }

    private boolean saveTarget(SyncTargetPack targetPack){
        return saveTarget(targetPack,false);
    }
    private boolean saveTarget(SyncTargetPack targetPack,boolean removeCategoryRef){
        if(ShareUtil.XObject.allEmpty(targetPack.getListIndicator(),targetPack.getListIndicaorCatgory())){
            return true;
        }

        return caseIndicatorInstanceDao.tranSaveBatch(targetPack.getListIndicator(), false, true, ()->{
            if(targetPack.getSetOldIndicatorIds().size()>0){
                caseIndicatorInstanceDao.delByIds(targetPack.getSetOldIndicatorIds());
            }
            if(removeCategoryRef){
                String accountId=targetPack.getListIndicatorCategoryPrincipalRef().stream()
                        .map(CaseIndicatorCategoryPrincipalRefEntity::getPrincipalId)
                        .filter(ShareUtil.XObject::notEmpty)
                        .findFirst()
                        .orElse("");
                List<String> categoryIds=ShareUtil.XCollection.map(targetPack.getListIndicatorCategoryPrincipalRef(),
                        CaseIndicatorCategoryPrincipalRefEntity::getIndicatorCategoryId);
                List<String> caseIndicatorIds=ShareUtil.XCollection.map(targetPack.getListIndicatorCategoryRef(),
                        CaseIndicatorCategoryRefEntity::getIndicatorInstanceId);
                if(ShareUtil.XObject.notEmpty(categoryIds)) {
                    caseIndicatorCategoryPrincipalRefService.lambdaUpdate()
                            .eq(CaseIndicatorCategoryPrincipalRefEntity::getPrincipalId, accountId)
                            .in(CaseIndicatorCategoryPrincipalRefEntity::getIndicatorCategoryId, categoryIds)
                            .remove();
                    caseIndicatorCategoryPrincipalRefService.saveOrUpdateBatch(targetPack.getListIndicatorCategoryPrincipalRef());
                }
                if(ShareUtil.XObject.notEmpty(caseIndicatorIds)){
                    caseIndicatorCategoryRefService.lambdaUpdate()
                            .in(CaseIndicatorCategoryRefEntity::getIndicatorInstanceId,caseIndicatorIds)
                            .remove();
                    caseIndicatorCategoryRefService.saveOrUpdateBatch(targetPack.getListIndicatorCategoryRef());
                }
            }else {
                caseIndicatorCategoryPrincipalRefService.saveOrUpdateBatch(targetPack.getListIndicatorCategoryPrincipalRef());
                caseIndicatorCategoryRefService.saveOrUpdateBatch(targetPack.getListIndicatorCategoryRef());
            }

            caseIndicatorCategoryService.saveOrUpdateBatch(targetPack.getListIndicaorCatgory());
            if(targetPack.getSetOldIndicatorIds().size()>0){
                caseIndicatorRuleDao.delByIndicatorIds(targetPack.getSetOldIndicatorIds());
            }
            caseIndicatorRuleDao.tranSaveBatch(targetPack.getListIndicatorRule(),false,true);
            if(targetPack.getSetOldIndicatorIds4Expression().size()>0){
                caseIndicatorExpressionRefDao.delByReasonId(targetPack.getSetOldIndicatorIds(),true);
            }
            caseIndicatorExpressionRefDao.tranSaveBatch(targetPack.getListExpressionRef(), false, true);
            if(targetPack.getSetOldIndicatorIds4Expression().size()>0){
                caseIndicatorExpressionDao.delByIndicatorIds(targetPack.getSetOldIndicatorIds(),true);
            }
            caseIndicatorExpressionDao.tranSaveBatch(targetPack.getListExpression(), targetPack.getListExpressionItem(), false, true);
            if(targetPack.getSetOldIndicatorIds().size()>0){
                caseIndicatorExpressionInfluenceDao.delByIndicatorIds(targetPack.getSetOldIndicatorIds());
            }
            caseIndicatorExpressionInfluenceDao.tranSaveBatch(targetPack.getListExpressionInfluence(),false,true);
            return true;
        });
    }








}
