package org.dows.hep.biz.eval;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dows.account.entity.AccountInstance;
import org.dows.hep.api.base.indicator.request.SyncIndicatorRequest;
import org.dows.hep.api.enums.EnumIndicatorCategory;
import org.dows.hep.api.enums.EnumIndicatorRuleType;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.dao.*;
import org.dows.hep.biz.extend.uim.XAccountInstanceApi;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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


    //region 一键同步所有指标
    public boolean syncPersonIndicator(SyncIndicatorRequest req){
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
        Map<String,SyncIds>  mapSyncIds=loadSyncIds(accountIds);
        if(ShareUtil.XObject.isEmpty(mapSyncIds)){
            return true;
        }
        SyncTargetPack targetPack=new SyncTargetPack(idGenerator);
        for(SyncIds item:mapSyncIds.values() ){
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
        return true;
    }
    //endregion

    //region 单个指标同步
    public boolean syncOneIndicator(SyncIndicatorRequest req){
        return true;
    }
    //endreigon


    private Set<String> loadAccountIds(boolean orgPersonFlag){
        final String source=orgPersonFlag?"机构人物":"人物管理";
        return ShareUtil.XCollection.toSet(xAccountInstanceApi.getAccountInstancesBySource(source, AccountInstance::getAccountId),
                AccountInstance::getAccountId);
    }

    private Map<String,SyncIds> loadSyncIds(Collection<String> accountIds){
        if(ShareUtil.XObject.isEmpty(accountIds)){
            return Collections.emptyMap();
        }
        Map<String,SyncIds> rst=ShareUtil.XCollection.toMap(accountIds, Function.identity(), k->new SyncIds(k));
        Map<String,String> mapAccountId=new HashMap<>();
        Set<String> caseIndicatorCategIds=new HashSet<>();
        caseIndicatorInstanceDao.getByAccountIds(accountIds,
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
        final Collection<String> indicatorCategIds=sourcePack.mapIndicaorCatgory.keySet();
        sourcePack.fillIndicatorCategoryRef(indicatorCategoryRefService.lambdaQuery()
                .in(IndicatorCategoryRefEntity::getIndicatorCategoryId,indicatorCategIds)
                .select(IndicatorCategoryRefEntity::getIndicatorCategoryId,
                        IndicatorCategoryRefEntity::getIndicatorInstanceId,
                        IndicatorCategoryRefEntity::getSeq)
                .list());
        sourcePack.fillIndicator(indicatorInstanceDao.getIndicatorsByCategIds(indicatorCategIds));
        final Collection<String> indicatorIds=sourcePack.mapIndicator.keySet();
        sourcePack.fillIndicatorRule(indicatorRuleDao.getByIndicatorIds(indicatorIds));
        sourcePack.fillExpressionRef(indicatorExpressionRefDao.getByReasonId(APPId, indicatorIds));
        sourcePack.fillExpressionInfluence( indicatorExpressionInfluenceDao.getByIndicatorIds(indicatorIds));
        final Collection<String> expressionIds=ShareUtil.XCollection.toSet(sourcePack.mapExpressionRef.values(), IndicatorExpressionRefEntity::getIndicatorExpressionId);
        sourcePack.fillExpression(indicatorExpressionDao.getByExpressionId(expressionIds, null));
        final Set<String> expressionItemIds=new HashSet<>();
        sourcePack.mapExpression.values().forEach(i->{
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
        if(ShareUtil.XObject.allEmpty(targetPack.listIndicator,targetPack.listIndicaorCatgory)){
            return true;
        }
        return caseIndicatorInstanceDao.tranSaveBatch(targetPack.listIndicator, false, true, ()->{
            caseIndicatorCategoryService.saveBatch(targetPack.listIndicaorCatgory);
            caseIndicatorCategoryPrincipalRefService.saveBatch(targetPack.listIndicatorCategoryPrincipalRef);
            caseIndicatorCategoryRefService.saveBatch(targetPack.listIndicatorCategoryRef);
            caseIndicatorRuleDao.tranSaveBatch(targetPack.listIndicatorRule,false,true);
            caseIndicatorExpressionRefDao.tranSaveBatch(targetPack.listExpressionRef, false, true);
            caseIndicatorExpressionDao.tranSaveBatch(targetPack.listExpression, targetPack.listExpressionItem, false, true);
            caseIndicatorExpressionInfluenceDao.tranSaveBatch(targetPack.listExpressionInfluence,false,true);
            return true;
        });
    }

    @Data
    @Accessors(chain = true)
    public static class SyncIds {

        public SyncIds(String accountId){
            this.accountId=accountId;
        }
        private final String accountId;

        private final Map<String,CaseIndicatorCategoryEntity> curIndicaorCatgory=new HashMap<>();
        private final Map<String,CaseIndicatorInstanceEntity> curIndicaors =new HashMap<>();
        private final Set<String> newIndicatorIds =new HashSet<>();

        private final Set<String> newIndicaorCategoryIds=new HashSet<>();


        public SyncIds fillNewIds(SyncSourcePack sourcePack) {
            sourcePack.getMapIndicator().keySet().forEach(i->{
                if(!curIndicaors.containsKey(i)){
                    newIndicatorIds.add(i);
                }
            });
            sourcePack.getMapIndicaorCatgory().keySet().forEach(i->{
                if(!curIndicaorCatgory.containsKey(i)){
                    newIndicaorCategoryIds.add(i);
                }
            });
            return this;
        }

        public SyncIds fillIndicaorCatgory(CaseIndicatorCategoryEntity src){
            curIndicaorCatgory.put(src.getIndicatorCategoryId(), src);
            return this;
        }

        public SyncIds fillIndicator(CaseIndicatorInstanceEntity src){
            curIndicaors.put(src.getIndicatorInstanceId(), src);
            return this;
        }

        public void clear(){
            curIndicaorCatgory.clear();
            curIndicaors.clear();
            newIndicatorIds.clear();
            newIndicaorCategoryIds.clear();
        }

    }

    @Data
    @Accessors(chain = true)
    public static class SyncSourcePack {

        private final Map<String,String> mapOwnerId=new HashMap<>();


        private final Map<String,IndicatorCategoryEntity> mapIndicaorCatgory=new HashMap<>();

        public SyncSourcePack fillIndicaorCatgory(List<IndicatorCategoryEntity> src){
            if(ShareUtil.XObject.isEmpty(src)){
                return this;
            }
            src.forEach(i->mapIndicaorCatgory.put(i.getIndicatorCategoryId(), i));
            return this;
        }

        private final Map<String,List<IndicatorCategoryRefEntity>> mapIndicatorCategoryRef=new HashMap<>();

        public SyncSourcePack fillIndicatorCategoryRef(List<IndicatorCategoryRefEntity> src){
            if(ShareUtil.XObject.isEmpty(src)){
                return this;
            }
            src.forEach(i->mapIndicatorCategoryRef.computeIfAbsent(i.getIndicatorInstanceId(), k->new ArrayList<>()).add(i));
            return this;
        }

        private final Map<String, IndicatorInstanceEntity> mapIndicator=new HashMap<>();

        public SyncSourcePack fillIndicator(List<IndicatorInstanceEntity> src){
            if(ShareUtil.XObject.isEmpty(src)){
                return this;
            }
            src.forEach(i->mapIndicator.put(i.getIndicatorInstanceId(),i));
            return this;
        }

        private final Map<String, IndicatorRuleEntity> mapIndicatorRule=new HashMap<>();
        public SyncSourcePack fillIndicatorRule(List<IndicatorRuleEntity> src){
            if(ShareUtil.XObject.isEmpty(src)){
                return this;
            }
            src.forEach(i->mapIndicatorRule.put(i.getVariableId(),i));
            return this;
        }

        private final Map<String, IndicatorExpressionRefEntity> mapExpressionRef=new HashMap<>();
        public SyncSourcePack fillExpressionRef(List<IndicatorExpressionRefEntity> src){
            if(ShareUtil.XObject.isEmpty(src)){
                return this;
            }
            src.forEach(i->mapExpressionRef.put(i.getReasonId(),i));
            return this;
        }


        private final Map<String,IndicatorExpressionEntity> mapExpression=new HashMap<>();
        public SyncSourcePack fillExpression(List<IndicatorExpressionEntity> src){
            if(ShareUtil.XObject.isEmpty(src)){
                return this;
            }
            src.forEach(i->{
                mapExpression.put(i.getPrincipalId(),i);
                mapOwnerId.put(i.getIndicatorExpressionId(),i.getPrincipalId());
                if(ShareUtil.XObject.notEmpty(i.getMinIndicatorExpressionItemId())){
                    mapOwnerId.put(i.getMinIndicatorExpressionItemId(),i.getPrincipalId());
                }
                if(ShareUtil.XObject.notEmpty(i.getMaxIndicatorExpressionItemId())){
                    mapOwnerId.put(i.getMaxIndicatorExpressionItemId(),i.getPrincipalId());
                }
            });
            return this;
        }

        private final Map<String,List<IndicatorExpressionItemEntity>> mapExpressionItem=new HashMap<>();

        public SyncSourcePack fillExpressionItem(List<IndicatorExpressionItemEntity> src){
            if(ShareUtil.XObject.isEmpty(src)){
                return this;
            }
            src.forEach(i->{
                if(ShareUtil.XObject.notEmpty(i.getIndicatorExpressionId())){
                     String indicatorId= mapOwnerId.get(i.getIndicatorExpressionId());
                     if(ShareUtil.XObject.isEmpty(indicatorId)){
                         return;
                     }
                     mapExpressionItem.computeIfAbsent(indicatorId, k->new ArrayList<>()).add(i);
                     return;
                }
                String indicatorId= mapOwnerId.get(i.getIndicatorExpressionItemId());
                if(ShareUtil.XObject.isEmpty(indicatorId)){
                    return;
                }
                mapExpressionItem.computeIfAbsent(indicatorId, k->new ArrayList<>()).add(i);
            });
            return this;
        }

        public SyncSourcePack sortExpressionItem() {
            if (ShareUtil.XObject.isEmpty(mapExpressionItem)) {
                return this;
            }
            mapExpressionItem.values().forEach(i -> i.sort((x, y) -> {
                if (ShareUtil.XObject.allEmpty(x.getSeq(), y.getSeq())) {
                    return 0;
                }
                if (ShareUtil.XObject.isEmpty(x.getSeq())) {
                    return 1;
                }
                if (ShareUtil.XObject.isEmpty(y.getSeq())) {
                    return -1;
                }
                return x.getSeq()-y.getSeq();
            }));
            return this;
        }

        private final Map<String,IndicatorExpressionInfluenceEntity> mapExpressionInfluence=new HashMap<>();

        public SyncSourcePack fillExpressionInfluence(List<IndicatorExpressionInfluenceEntity> src){
            if(ShareUtil.XObject.isEmpty(src)){
                return this;
            }
            src.forEach(i->mapExpressionInfluence.put(i.getIndicatorInstanceId(),i));
            return this;
        }

        public void clear(){
            mapOwnerId.clear();
            mapIndicaorCatgory.clear();
            mapIndicatorCategoryRef.clear();
            mapIndicator.clear();
            mapIndicatorRule.clear();
            mapExpressionRef.clear();
            mapExpression.clear();
            mapExpressionItem.clear();
            mapExpressionInfluence.clear();
        }
    }

    @Data
    @Accessors(chain = true)
    public static class SyncTargetPack {

        public SyncTargetPack(IdGenerator idGenerator){
            this.idGenerator=idGenerator;
        }
        private final IdGenerator idGenerator;
        private final Map<String,String> mapNewId=new HashMap<>();

        public SyncTargetPack fillNewIds(SyncIds syncIds,boolean clearFlag){
            if(clearFlag){
                mapNewId.clear();
            }
            syncIds.getCurIndicaorCatgory().values().forEach(i->mapNewId.put(i.getIndicatorCategoryId(), i.getCaseIndicatorCategoryId()));
            syncIds.getNewIndicaorCategoryIds().forEach(i->mapNewId.computeIfAbsent(i,k -> idGenerator.nextIdStr()));

            syncIds.getCurIndicaors().values().forEach(i->mapNewId.put(i.getIndicatorInstanceId(), i.getCaseIndicatorInstanceId()));
            syncIds.getNewIndicatorIds().forEach(i->mapNewId.computeIfAbsent(i,k -> idGenerator.nextIdStr()));
            return this;
        }

        private final List<CaseIndicatorCategoryEntity> listIndicaorCatgory=new ArrayList<>();

        private final List<CaseIndicatorCategoryPrincipalRefEntity> listIndicatorCategoryPrincipalRef=new ArrayList<>();

        public SyncTargetPack fillIndicatorCatory(SyncSourcePack sourcePack,SyncIds syncIds){
            if(ShareUtil.XObject.isEmpty(syncIds.getNewIndicaorCategoryIds())) {
                return this;
            }
            syncIds.getNewIndicaorCategoryIds().stream()
                    .map(sourcePack.getMapIndicaorCatgory()::get)
                    .filter(ShareUtil.XObject::notEmpty)
                    .forEach(src->{
                        String newCategId=mapNewId.get(src.getIndicatorCategoryId());
                        listIndicaorCatgory.add(CopyWrapper.create(CaseIndicatorCategoryEntity::new)
                                .endFrom(src)
                                .setCaseIndicatorCategoryId(newCategId)
                                .setId(null)
                        );
                        listIndicatorCategoryPrincipalRef.add(new CaseIndicatorCategoryPrincipalRefEntity()
                                .setAppId(APPId)
                                .setPrincipalId(syncIds.accountId)
                                .setIndicatorCategoryId(newCategId)
                                .setCaseIndicatorCategoryPrincipalRefId(idGenerator.nextIdStr())
                        );
                    });
            return this;
        }

        private final List<CaseIndicatorCategoryRefEntity> listIndicatorCategoryRef=new ArrayList<>();

        public SyncTargetPack fillIndicatorCatoryRef(SyncSourcePack sourcePack,SyncIds syncIds){
            if(ShareUtil.XObject.isEmpty(syncIds.getNewIndicatorIds())) {
                return this;
            }
            syncIds.getNewIndicatorIds().stream()
                    .map(sourcePack.getMapIndicatorCategoryRef()::get)
                    .filter(ShareUtil.XObject::notEmpty)
                    .flatMap(List::stream)
                    .forEach(src-> {
                        listIndicatorCategoryRef.add(new CaseIndicatorCategoryRefEntity()
                                .setAppId(APPId)
                                .setIndicatorInstanceId(mapNewId.get(src.getIndicatorInstanceId()))
                                .setIndicatorCategoryId(mapNewId.get(src.getIndicatorCategoryId()))
                                .setCaseIndicatorCategoryRefId(idGenerator.nextIdStr())
                                .setSeq(src.getSeq())
                        );
                    });
            return this;
        }

        private final List<CaseIndicatorInstanceEntity> listIndicator=new ArrayList<>();

        public SyncTargetPack fillIndicator(SyncSourcePack sourcePack,SyncIds syncIds) {
            if (ShareUtil.XObject.isEmpty(syncIds.getNewIndicatorIds())) {
                return this;
            }
            syncIds.getNewIndicatorIds().stream()
                    .map(sourcePack.getMapIndicator()::get)
                    .filter(ShareUtil.XObject::notEmpty)
                    .forEach(src -> listIndicator.add(CopyWrapper.create(CaseIndicatorInstanceEntity::new)
                            .endFrom(src)
                            .setCaseIndicatorInstanceId(mapNewId.get(src.getIndicatorInstanceId()))
                            .setIndicatorCategoryId(mapNewId.get(src.getIndicatorCategoryId()))
                            .setPrincipalId(syncIds.accountId)
                            .setId(null)
                    ));
            return this;
        }
        private final List<CaseIndicatorRuleEntity> listIndicatorRule=new ArrayList<>();

        public SyncTargetPack fillIndicatorRule(SyncSourcePack sourcePack,SyncIds syncIds) {
            if (ShareUtil.XObject.isEmpty(syncIds.getNewIndicatorIds())) {
                return this;
            }
            syncIds.getNewIndicatorIds().stream()
                    .map(sourcePack.getMapIndicatorRule()::get)
                    .filter(ShareUtil.XObject::notEmpty)
                    .forEach(src -> listIndicatorRule.add(CopyWrapper.create(CaseIndicatorRuleEntity::new)
                            .endFrom(src)
                            .setRuleType(EnumIndicatorRuleType.INDICATOR.getCode())
                            .setVariableId(mapNewId.get(src.getVariableId()))
                            .setCaseIndicatorRuleId(idGenerator.nextIdStr())
                            .setId(null)
                    ));
            return this;
        }
        private final List<CaseIndicatorExpressionRefEntity> listExpressionRef=new ArrayList<>();

        public SyncTargetPack fillExpressionRef(SyncSourcePack sourcePack,SyncIds syncIds) {
            if (ShareUtil.XObject.isEmpty(syncIds.getNewIndicatorIds())) {
                return this;
            }
            syncIds.getNewIndicatorIds().stream()
                    .map(sourcePack.getMapExpressionRef()::get)
                    .filter(ShareUtil.XObject::notEmpty)
                    .forEach(src -> listExpressionRef.add(CopyWrapper.create(CaseIndicatorExpressionRefEntity::new)
                            .endFrom(src)
                            .setReasonId(mapNewId.get(src.getReasonId()))
                            .setIndicatorExpressionId(mapNewId.computeIfAbsent(src.getIndicatorExpressionId(), k -> idGenerator.nextIdStr()))
                            .setCaseIndicatorExpressionRefId(idGenerator.nextIdStr())
                            .setId(null)
                    ));
            return this;
        }

        private final List<CaseIndicatorExpressionEntity> listExpression=new ArrayList<>();
        public SyncTargetPack fillExpression(SyncSourcePack sourcePack,SyncIds syncIds) {
            if (ShareUtil.XObject.isEmpty(syncIds.getNewIndicatorIds())) {
                return this;
            }
            syncIds.getNewIndicatorIds().stream()
                    .map(sourcePack.getMapExpression()::get)
                    .filter(ShareUtil.XObject::notEmpty)
                    .forEach(src -> listExpression.add(CopyWrapper.create(CaseIndicatorExpressionEntity::new)
                            .endFrom(src)
                            .setCasePrincipalId(mapNewId.get(src.getPrincipalId()))
                            .setMaxIndicatorExpressionItemId(mapNewId.computeIfAbsent(src.getMaxIndicatorExpressionItemId(), k -> idGenerator.nextIdStr()))
                            .setMinIndicatorExpressionItemId(mapNewId.computeIfAbsent(src.getMinIndicatorExpressionItemId(), k -> idGenerator.nextIdStr()))
                            .setCaseIndicatorExpressionId(mapNewId.computeIfAbsent(src.getIndicatorExpressionId(), k -> idGenerator.nextIdStr()))
                            .setId(null)
                    ));
            return this;
        }

        private final List<CaseIndicatorExpressionItemEntity> listExpressionItem=new ArrayList<>();

        public SyncTargetPack fillExpressionItem(SyncSourcePack sourcePack,SyncIds syncIds) {
            if (ShareUtil.XObject.isEmpty(syncIds.getNewIndicatorIds())) {
                return this;
            }
            syncIds.getNewIndicatorIds().stream()
                    .map(sourcePack.getMapExpressionItem()::get)
                    .filter(ShareUtil.XObject::notEmpty)
                    .flatMap(List::stream)
                    .forEach(src -> listExpressionItem.add(CopyWrapper.create(CaseIndicatorExpressionItemEntity::new)
                            .endFrom(src)
                            .setIndicatorExpressionId(mapNewId.get(src.getIndicatorExpressionId()))
                            .setCaseIndicatorExpressionItemId(mapNewId.computeIfAbsent(src.getIndicatorExpressionItemId(), k -> idGenerator.nextIdStr()))
                            .setId(null)
                            .setConditionValList(fillExperssionString(src.getConditionValList(), mapNewId))
                            .setResultValList(fillExperssionString(src.getResultValList(), mapNewId))
                    ));
            return this;
        }
        private final List<CaseIndicatorExpressionInfluenceEntity> listExpressionInfluence=new ArrayList<>();

        public SyncTargetPack fillExpressionInfluence(SyncSourcePack sourcePack,SyncIds syncIds) {
            if (ShareUtil.XObject.isEmpty(syncIds.getNewIndicatorIds())) {
                return this;
            }
            syncIds.getNewIndicatorIds().stream()
                    .map(sourcePack.getMapExpressionInfluence()::get)
                    .filter(ShareUtil.XObject::notEmpty)
                    .forEach(src -> listExpressionInfluence.add(new CaseIndicatorExpressionInfluenceEntity()
                            .setAppId(src.getAppId())
                            .setIndicatorInstanceId(mapNewId.get(src.getIndicatorInstanceId()))
                            .setInfluenceIndicatorInstanceIdList(fillExperssionString(src.getInfluenceIndicatorInstanceIdList(), mapNewId))
                            .setInfluencedIndicatorInstanceIdList(fillExperssionString(src.getInfluencedIndicatorInstanceIdList(), mapNewId))
                            .setId(null)
                    ));
            return this;
        }

        public SyncTargetPack fillData(SyncSourcePack sourcePack,SyncIds syncIds,boolean clearNewId) {
            if(ShareUtil.XObject.allEmpty(syncIds.getNewIndicatorIds(),syncIds.getNewIndicaorCategoryIds())){
                return this;
            }
            return this.fillNewIds(syncIds, clearNewId)
                    .fillIndicatorCatory(sourcePack, syncIds)
                    .fillIndicatorCatoryRef(sourcePack, syncIds)
                    .fillIndicator(sourcePack, syncIds)
                    .fillIndicatorRule(sourcePack, syncIds)
                    .fillExpressionRef(sourcePack, syncIds)
                    .fillExpression(sourcePack, syncIds)
                    .fillExpressionItem(sourcePack, syncIds)
                    .fillExpressionInfluence(sourcePack, syncIds);
        }

        private String fillExperssionString(String raw,Map<String,String> mapId){
            if(ShareUtil.XObject.isEmpty(raw)){
                return raw;
            }
            final String splitStr=EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr();
            return Arrays.stream(raw.split(splitStr))
                    .map(mapNewId::get)
                    .filter(ShareUtil.XObject::notEmpty)
                    .collect(Collectors.joining(splitStr));
        }

        public void clear(){
            mapNewId.clear();
            listIndicaorCatgory.clear();
            listIndicatorCategoryPrincipalRef.clear();
            listIndicatorCategoryRef.clear();
            listIndicator.clear();
            listIndicatorRule.clear();
            listExpressionRef.clear();
            listExpression.clear();
            listExpressionItem.clear();
            listExpressionInfluence.clear();
        }
    }


}
