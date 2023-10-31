package org.dows.hep.biz.eval.sync;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.api.enums.EnumIndicatorRuleType;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.sequence.api.IdGenerator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/10/25 9:44
 */
@Data
@Accessors(chain = true)
public class SyncTargetPack {
    private static final String APPId="3";

    public SyncTargetPack(IdGenerator idGenerator){
        this.idGenerator=idGenerator;
    }
    private final IdGenerator idGenerator;
    private final Map<String,String> mapNewId=new HashMap<>();


    //region facade
    public SyncTargetPack fillData(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack, boolean clearNewId) {
        if(ShareUtil.XObject.allEmpty(syncCurrentPack.getNewIndicatorIds(), syncCurrentPack.getNewIndicaorCategoryIds())){
            return this;
        }
        return this.fillNewIds(sourcePack,syncCurrentPack, clearNewId)
                .fillIndicatorCategory(sourcePack, syncCurrentPack)
                .fillIndicatorCatoryRef(sourcePack, syncCurrentPack)
                .fillIndicator(sourcePack, syncCurrentPack)
                .fillIndicatorRule(sourcePack, syncCurrentPack)
                .fillExpressionRef(sourcePack, syncCurrentPack)
                .fillExpression(sourcePack, syncCurrentPack)
                .fillExpressionItem(sourcePack, syncCurrentPack)
                .fillExpressionInfluence(sourcePack, syncCurrentPack);
    }
    public SyncTargetPack coverData(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack, boolean clearNewId) {
        return this.fillNewIds(sourcePack,syncCurrentPack, clearNewId)
                .fillOldIds(sourcePack, syncCurrentPack)
                .coverIndicatorCategory(sourcePack, syncCurrentPack)
                .coverIndicatorCatoryRef(sourcePack, syncCurrentPack)
                .coverIndicator(sourcePack, syncCurrentPack)
                .coverIndicatorRule(sourcePack, syncCurrentPack)
                .coverExpressionRef(sourcePack, syncCurrentPack)
                .coverExpression(sourcePack, syncCurrentPack)
                .coverExpressionItem(sourcePack, syncCurrentPack)
                .coverExpressionInfluence(sourcePack, syncCurrentPack);
    }
    public SyncTargetPack coverData(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack,  String indicatorId) {
        return this.fillNewIds(sourcePack,syncCurrentPack, true)
                .fillOldIds(sourcePack, syncCurrentPack, indicatorId)
                //.coverIndicatorCategory(sourcePack, syncCurrentPack,indicatorCategoryId)
                .coverIndicator(sourcePack, syncCurrentPack, indicatorId)
                .coverIndicatorRule(sourcePack, syncCurrentPack, indicatorId)
                .coverExpressionRef(sourcePack, syncCurrentPack, indicatorId)
                .coverExpression(sourcePack, syncCurrentPack, indicatorId)
                .coverExpressionItem(sourcePack, syncCurrentPack, indicatorId)
                .coverExpressionInfluence(sourcePack, syncCurrentPack, indicatorId);
    }
    //endregion
    public SyncTargetPack fillNewIds(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack, boolean clearFlag){
        if(clearFlag){
            mapNewId.clear();
        }
        syncCurrentPack.getCurIndicaorCatgory().values().forEach(i->mapNewId.put(i.getIndicatorCategoryId(), i.getCaseIndicatorCategoryId()));
        syncCurrentPack.getNewIndicaorCategoryIds().forEach(i->mapNewId.computeIfAbsent(i, k -> idGenerator.nextIdStr()));
        sourcePack.getMapIndicaorCatgory().values().forEach(i->mapNewId.computeIfAbsent(i.getIndicatorCategoryId(), k -> idGenerator.nextIdStr()));

        syncCurrentPack.getCurIndicaors().values().forEach(i->mapNewId.put(i.getIndicatorInstanceId(), i.getCaseIndicatorInstanceId()));
        syncCurrentPack.getNewIndicatorIds().forEach(i->mapNewId.computeIfAbsent(i, k -> idGenerator.nextIdStr()));
        sourcePack.getMapIndicator().values().forEach(i->mapNewId.computeIfAbsent(i.getIndicatorInstanceId(), k -> idGenerator.nextIdStr()));

        syncCurrentPack.getMapExpressionRef().values().forEach(i->mapNewId.computeIfAbsent(i.getIndicatorExpressionId(), k -> idGenerator.nextIdStr()));
        return this;
    }

    private final Set<String> setOldIndicatorIds=new HashSet<>();

    private final Set<String> setOldIndicatorIds4Expression=new HashSet<>();
    public SyncTargetPack fillOldIds(SyncSourcePack sourcePack,SyncCurrentPack currentPack){
        currentPack.getCurIndicaors().forEach((k,v)->{
            if(ShareUtil.XObject.isEmpty(v.getIndicatorInstanceId())){
                return;
            }
            if(!sourcePack.getMapIndicator().containsKey(k)){
                setOldIndicatorIds.add(v.getCaseIndicatorInstanceId());
            }
        });
        currentPack.getMapExpressionRef().forEach((k,v)->{
            CaseIndicatorInstanceEntity indicator= currentPack.getCurIndicaors().get(k);
            if(ShareUtil.XObject.anyEmpty(indicator,()->indicator.getIndicatorInstanceId())) {
                return;
            }
            if(!sourcePack.getMapExpressionRef().containsKey(k)){
                setOldIndicatorIds4Expression.add(indicator.getCaseIndicatorInstanceId());
            }
        });
        setOldIndicatorIds4Expression.addAll(setOldIndicatorIds);
        return this;
    }
    public SyncTargetPack fillOldIds(SyncSourcePack sourcePack,SyncCurrentPack currentPack,String indicatorId){
        CaseIndicatorInstanceEntity indicator= currentPack.getCurIndicaors().get(indicatorId);
        if(ShareUtil.XObject.anyEmpty(indicator,()->indicator.getIndicatorInstanceId())) {
            return this;
        }
        if(!sourcePack.getMapExpressionRef().containsKey(indicatorId)){
            setOldIndicatorIds4Expression.add(indicator.getCaseIndicatorInstanceId());
        }
        return this;
    }

    private final List<CaseIndicatorCategoryEntity> listIndicaorCatgory=new ArrayList<>();

    private final List<CaseIndicatorCategoryPrincipalRefEntity> listIndicatorCategoryPrincipalRef=new ArrayList<>();

    //region IndicatorCategory
    public SyncTargetPack fillIndicatorCategory(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack){
        if(ShareUtil.XObject.isEmpty(syncCurrentPack.getNewIndicaorCategoryIds())) {
            return this;
        }
        syncCurrentPack.getNewIndicaorCategoryIds().stream()
                .map(sourcePack.getMapIndicaorCatgory()::get)
                .filter(ShareUtil.XObject::notEmpty)
                .forEach(src->{
                    addIndicatorCategory(src);
                    addIndicatorCategoryPrincipalRef(syncCurrentPack, src.getIndicatorCategoryId());
                });
        return this;
    }
    public SyncTargetPack coverIndicatorCategory(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        sourcePack.getMapIndicaorCatgory().forEach((indicatorCategoryId,src)->{
            coverIndicatorCategory(src, syncCurrentPack,indicatorCategoryId);
            addIndicatorCategoryPrincipalRef(syncCurrentPack, indicatorCategoryId);
        });
        return this;
    }
    public SyncTargetPack coverIndicatorCategory(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack, String indicatorCategoryId) {
        IndicatorCategoryEntity src=sourcePack.getMapIndicaorCatgory().get(indicatorCategoryId);
        if(ShareUtil.XObject.isEmpty(src)){
            return this;
        }
        return coverIndicatorCategory(src, syncCurrentPack,indicatorCategoryId);
    }
    public SyncTargetPack coverIndicatorCategory(IndicatorCategoryEntity src, SyncCurrentPack syncCurrentPack, String indicatorCategoryId) {
        CaseIndicatorCategoryEntity dst = syncCurrentPack.getCurIndicaorCatgory().get(indicatorCategoryId);
        if (ShareUtil.XObject.isEmpty(dst)) {
            return addIndicatorCategory(src);
        }
        listIndicaorCatgory.add(dst.setCategoryName(src.getCategoryName())
                .setSeq(dst.getSeq()));
        return this;
    }
    public SyncTargetPack addIndicatorCategory(IndicatorCategoryEntity src){
        listIndicaorCatgory.add(CopyWrapper.create(CaseIndicatorCategoryEntity::new)
                .endFrom(src)
                .setCaseIndicatorCategoryId(mapNewId.get(src.getIndicatorCategoryId()))
                .setId(null)
                .setDt(new Date())
        );
        return this;
    }
    public SyncTargetPack addIndicatorCategoryPrincipalRef(SyncCurrentPack syncCurrentPack, String indicatorCategoryId){
        listIndicatorCategoryPrincipalRef.add(new CaseIndicatorCategoryPrincipalRefEntity()
                .setAppId(APPId)
                .setPrincipalId(syncCurrentPack.getAccountId())
                .setIndicatorCategoryId(mapNewId.get(indicatorCategoryId))
                .setCaseIndicatorCategoryPrincipalRefId(idGenerator.nextIdStr())
                .setDt(new Date())
        );
        return this;
    }

    //endregion

    private final List<CaseIndicatorCategoryRefEntity> listIndicatorCategoryRef=new ArrayList<>();

    //region IndicatorCatoryRef
    public SyncTargetPack fillIndicatorCatoryRef(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack){
        if(ShareUtil.XObject.isEmpty(syncCurrentPack.getNewIndicatorIds())) {
            return this;
        }
        syncCurrentPack.getNewIndicatorIds().stream()
                .map(sourcePack.getMapIndicatorCategoryRef()::get)
                .filter(ShareUtil.XObject::notEmpty)
                .flatMap(List::stream)
                .forEach(src-> addIndicatorCatoryRef(src));
        return this;
    }
    public SyncTargetPack coverIndicatorCatoryRef(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack){
        sourcePack.getMapIndicatorCategoryRef().values()
                .stream()
                .flatMap(List::stream)
                .forEach(src-> addIndicatorCatoryRef(src));
        return this;
    }
    public SyncTargetPack addIndicatorCatoryRef(IndicatorCategoryRefEntity src){
        listIndicatorCategoryRef.add(new CaseIndicatorCategoryRefEntity()
                .setAppId(APPId)
                .setIndicatorInstanceId(mapNewId.get(src.getIndicatorInstanceId()))
                .setIndicatorCategoryId(mapNewId.get(src.getIndicatorCategoryId()))
                .setCaseIndicatorCategoryRefId(idGenerator.nextIdStr())
                .setSeq(src.getSeq())
                .setDt(new Date())
        );
        return this;
    }
    //endregion
    private final List<CaseIndicatorInstanceEntity> listIndicator=new ArrayList<>();

    //region Indicator
    public SyncTargetPack fillIndicator(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        if (ShareUtil.XObject.isEmpty(syncCurrentPack.getNewIndicatorIds())) {
            return this;
        }
        syncCurrentPack.getNewIndicatorIds().stream()
                .map(sourcePack.getMapIndicator()::get)
                .filter(ShareUtil.XObject::notEmpty)
                .forEach(src -> addIndicator(src, syncCurrentPack));
        return this;
    }
    public SyncTargetPack coverIndicator(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        sourcePack.getMapIndicator().forEach((indicatorId,src)->coverIndicator(src, syncCurrentPack,indicatorId));
        return this;
    }

    public SyncTargetPack coverIndicator(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack, String indicatorId) {
        IndicatorInstanceEntity src=sourcePack.getMapIndicator().get(indicatorId);
        if(ShareUtil.XObject.isEmpty(src)){
            return this;
        }
        return coverIndicator(src, syncCurrentPack,indicatorId);
    }
    public SyncTargetPack coverIndicator(IndicatorInstanceEntity src, SyncCurrentPack syncCurrentPack, String indicatorId) {
        CaseIndicatorInstanceEntity dst= syncCurrentPack.getCurIndicaors().get(indicatorId);
        if(ShareUtil.XObject.isEmpty(dst)){
            return addIndicator(src, syncCurrentPack);
        }
        listIndicator.add(dst.setIndicatorName(src.getIndicatorName())
                .setIndicatorCategoryId(mapNewId.get(src.getIndicatorCategoryId()))
                .setDisplayByPercent(src.getDisplayByPercent())
                .setUnit(src.getUnit())
                .setCore(src.getCore())
                .setFood(src.getFood())
                .setType(src.getType())
                .setValueType(src.getValueType())
                .setDescr(src.getDescr()));

        return this;
    }
    public SyncTargetPack addIndicator(IndicatorInstanceEntity src, SyncCurrentPack syncCurrentPack) {
        listIndicator.add(CopyWrapper.create(CaseIndicatorInstanceEntity::new)
                .endFrom(src)
                .setCaseIndicatorInstanceId(mapNewId.get(src.getIndicatorInstanceId()))
                .setIndicatorCategoryId(mapNewId.get(src.getIndicatorCategoryId()))
                .setPrincipalId(syncCurrentPack.getAccountId())
                .setId(null)
                .setDt(new Date()));
        return this;
    }
    //endregion

    private final List<CaseIndicatorRuleEntity> listIndicatorRule=new ArrayList<>();

    //region IndicatorRule
    public SyncTargetPack fillIndicatorRule(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        if (ShareUtil.XObject.isEmpty(syncCurrentPack.getNewIndicatorIds())) {
            return this;
        }
        syncCurrentPack.getNewIndicatorIds().stream()
                .map(sourcePack.getMapIndicatorRule()::get)
                .filter(ShareUtil.XObject::notEmpty)
                .forEach(src -> addIndicatorRule(src));
        return this;
    }
    public SyncTargetPack coverIndicatorRule(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        sourcePack.getMapIndicatorRule().forEach((indicatorId,src)->coverIndicatorRule(src, syncCurrentPack,indicatorId,false));
        return this;
    }
    public SyncTargetPack coverIndicatorRule(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack, String indicatorId) {
        IndicatorRuleEntity src = sourcePack.getMapIndicatorRule().get(indicatorId);
        if (ShareUtil.XObject.isEmpty(src)) {
            return this;
        }
        return coverIndicatorRule(src, syncCurrentPack,indicatorId,true);
    }
    public SyncTargetPack coverIndicatorRule(IndicatorRuleEntity src, SyncCurrentPack syncCurrentPack, String indicatorId,boolean coverDef) {
        CaseIndicatorRuleEntity dst = syncCurrentPack.getMapIndicatorRule().get(indicatorId);
        if (ShareUtil.XObject.isEmpty(dst)) {
            return addIndicatorRule(src);
        }
        listIndicatorRule.add(dst.setRuleType(src.getRuleType())
                .setMin(src.getMin())
                .setMax(src.getMax())
                .setDef(coverDef?src.getDef():dst.getDef())
                .setDescr(src.getDescr())
        );
        return this;
    }
    public SyncTargetPack addIndicatorRule(IndicatorRuleEntity src) {
        listIndicatorRule.add(CopyWrapper.create(CaseIndicatorRuleEntity::new)
                .endFrom(src)
                .setRuleType(EnumIndicatorRuleType.INDICATOR.getCode())
                .setVariableId(mapNewId.get(src.getVariableId()))
                .setCaseIndicatorRuleId(idGenerator.nextIdStr())
                .setId(null)
                .setDt(new Date()));
        return this;
    }
    //endregion
    private final List<CaseIndicatorExpressionRefEntity> listExpressionRef=new ArrayList<>();

    //region ExpressionRef
    public SyncTargetPack fillExpressionRef(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        if (ShareUtil.XObject.isEmpty(syncCurrentPack.getNewIndicatorIds())) {
            return this;
        }
        syncCurrentPack.getNewIndicatorIds().stream()
                .map(sourcePack.getMapExpressionRef()::get)
                .filter(ShareUtil.XObject::notEmpty)
                .forEach(src -> addExpressionRef(src));
        return this;
    }
    public SyncTargetPack coverExpressionRef(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        sourcePack.getMapExpressionRef().forEach((indicatorId,src)->coverExpressionRef(src, syncCurrentPack,indicatorId));
        return this;
    }

    public SyncTargetPack coverExpressionRef(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack, String indicatorId) {
        IndicatorExpressionRefEntity src = sourcePack.getMapExpressionRef().get(indicatorId);
        if (ShareUtil.XObject.isEmpty(src)) {
            return this;
        }
        return coverExpressionRef(src, syncCurrentPack,indicatorId);
    }
    public SyncTargetPack coverExpressionRef(IndicatorExpressionRefEntity src, SyncCurrentPack syncCurrentPack, String indicatorId) {
        CaseIndicatorExpressionRefEntity dst= syncCurrentPack.getMapExpressionRef().get(indicatorId);
        if(ShareUtil.XObject.isEmpty(dst)){
            return addExpressionRef(src);
        }
        listExpressionRef.add(dst.setReasonId(mapNewId.get(src.getReasonId()))
                .setIndicatorExpressionId(mapNewId.computeIfAbsent(src.getIndicatorExpressionId(), v -> idGenerator.nextIdStr()))
        );
        return this;
    }
    public SyncTargetPack addExpressionRef(IndicatorExpressionRefEntity src) {
        listExpressionRef.add(CopyWrapper.create(CaseIndicatorExpressionRefEntity::new)
                .endFrom(src)
                .setReasonId(mapNewId.get(src.getReasonId()))
                .setIndicatorExpressionId(mapNewId.computeIfAbsent(src.getIndicatorExpressionId(), v -> idGenerator.nextIdStr()))
                .setCaseIndicatorExpressionRefId(idGenerator.nextIdStr())
                .setId(null)
                .setDt(new Date()));
        return this;
    }
    //endregion
    private final List<CaseIndicatorExpressionEntity> listExpression=new ArrayList<>();

    //region Expression
    public SyncTargetPack fillExpression(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        if (ShareUtil.XObject.isEmpty(syncCurrentPack.getNewIndicatorIds())) {
            return this;
        }
        syncCurrentPack.getNewIndicatorIds().stream()
                .map(sourcePack.getMapExpression()::get)
                .filter(ShareUtil.XObject::notEmpty)
                .forEach(src -> addExpression(src));
        return this;
    }
    public SyncTargetPack coverExpression(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        sourcePack.getMapExpression().forEach((indicatorId, src) -> coverExpression(src, syncCurrentPack,indicatorId));
        return this;
    }
    public SyncTargetPack coverExpression(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack, String indicatorId) {
        IndicatorExpressionEntity src = sourcePack.getMapExpression().get(indicatorId);
        if (ShareUtil.XObject.isEmpty(src)) {
            return this;
        }
        return coverExpression(src, syncCurrentPack,indicatorId);
    }
    public SyncTargetPack coverExpression(IndicatorExpressionEntity src, SyncCurrentPack syncCurrentPack, String indicatorId) {
        CaseIndicatorExpressionEntity dst = syncCurrentPack.getMapExpression().get(indicatorId);
        if (ShareUtil.XObject.isEmpty(dst)) {
            return addExpression(src);
        }
        listExpression.add(dst.setCaseIndicatorExpressionId(mapNewId.get(src.getIndicatorExpressionId()))
                .setIndicatorExpressionId(src.getIndicatorExpressionId())
                .setCasePrincipalId(mapNewId.get(src.getPrincipalId()))
                .setPrincipalId(src.getPrincipalId())
                .setMaxIndicatorExpressionItemId(mapNewId.computeIfAbsent(src.getMaxIndicatorExpressionItemId(), k -> idGenerator.nextIdStr()))
                .setMinIndicatorExpressionItemId(mapNewId.computeIfAbsent(src.getMinIndicatorExpressionItemId(), k -> idGenerator.nextIdStr()))
                .setType(src.getType())
                .setSource(src.getSource())
        );
        return this;
    }
    public SyncTargetPack addExpression(IndicatorExpressionEntity src){
        listExpression.add(CopyWrapper.create(CaseIndicatorExpressionEntity::new)
                .endFrom(src)
                .setCasePrincipalId(mapNewId.get(src.getPrincipalId()))
                .setMaxIndicatorExpressionItemId(mapNewId.computeIfAbsent(src.getMaxIndicatorExpressionItemId(), k -> idGenerator.nextIdStr()))
                .setMinIndicatorExpressionItemId(mapNewId.computeIfAbsent(src.getMinIndicatorExpressionItemId(), k -> idGenerator.nextIdStr()))
                .setCaseIndicatorExpressionId(mapNewId.computeIfAbsent(src.getIndicatorExpressionId(), v -> idGenerator.nextIdStr()))
                .setId(null)
                .setDt(new Date()));
        return this;
    }
    //endregion
    private final List<CaseIndicatorExpressionItemEntity> listExpressionItem=new ArrayList<>();

    //region ExpressionItem
    public SyncTargetPack fillExpressionItem(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        if (ShareUtil.XObject.isEmpty(syncCurrentPack.getNewIndicatorIds())) {
            return this;
        }
        syncCurrentPack.getNewIndicatorIds().stream()
                .map(sourcePack.getMapExpressionItem()::get)
                .filter(ShareUtil.XObject::notEmpty)
                .flatMap(List::stream)
                .forEach(src -> addExpressionItem(src));
        return this;
    }
    public SyncTargetPack coverExpressionItem(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        sourcePack.getMapExpressionItem().values().stream()
                .flatMap(List::stream)
                .forEach(src ->addExpressionItem(src));
        return this;
    }

    public SyncTargetPack coverExpressionItem(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack, String indicatorId) {
        List<IndicatorExpressionItemEntity> srcList = sourcePack.getMapExpressionItem().get(indicatorId);
        if (ShareUtil.XObject.isEmpty(srcList)) {
            return this;
        }
        srcList.forEach(src -> addExpressionItem(src));
        return this;
    }
    public SyncTargetPack addExpressionItem(IndicatorExpressionItemEntity src){
        listExpressionItem.add(CopyWrapper.create(CaseIndicatorExpressionItemEntity::new)
                .endFrom(src)
                .setIndicatorExpressionId(mapNewId.get(src.getIndicatorExpressionId()))
                .setCaseIndicatorExpressionItemId(mapNewId.computeIfAbsent(src.getIndicatorExpressionItemId(), k -> idGenerator.nextIdStr()))
                .setId(null)
                .setConditionValList(fillExperssionString(src.getConditionValList(), mapNewId))
                .setResultValList(fillExperssionString(src.getResultValList(), mapNewId)));
        return this;
    }
    //endregion
    private final List<CaseIndicatorExpressionInfluenceEntity> listExpressionInfluence=new ArrayList<>();

    //region ExpressionInfluence
    public SyncTargetPack fillExpressionInfluence(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        if (ShareUtil.XObject.isEmpty(syncCurrentPack.getNewIndicatorIds())) {
            return this;
        }
        syncCurrentPack.getNewIndicatorIds().stream()
                .map(sourcePack.getMapExpressionInfluence()::get)
                .filter(ShareUtil.XObject::notEmpty)
                .forEach(src -> addExpressionInfluence(src));
        return this;
    }
    public SyncTargetPack coverExpressionInfluence(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack) {
        sourcePack.getMapExpressionInfluence().forEach((indicatorId, src) -> coverExpressionInfluence(src, syncCurrentPack,indicatorId));
        return this;
    }
    public SyncTargetPack coverExpressionInfluence(SyncSourcePack sourcePack, SyncCurrentPack syncCurrentPack, String indicatorId) {
        IndicatorExpressionInfluenceEntity src = sourcePack.getMapExpressionInfluence().get(indicatorId);
        if (ShareUtil.XObject.isEmpty(src)) {
            return this;
        }
        return coverExpressionInfluence(src, syncCurrentPack,indicatorId);
    }
    public SyncTargetPack coverExpressionInfluence(IndicatorExpressionInfluenceEntity src, SyncCurrentPack syncCurrentPack, String indicatorId){
        CaseIndicatorExpressionInfluenceEntity dst = syncCurrentPack.getMapExpressionInfluence().get(indicatorId);
        if (ShareUtil.XObject.isEmpty(dst)) {
            return addExpressionInfluence(src);
        }
        listExpressionInfluence.add(dst
                .setInfluenceIndicatorInstanceIdList(fillExperssionString(src.getInfluenceIndicatorInstanceIdList(), mapNewId))
                .setInfluencedIndicatorInstanceIdList(fillExperssionString(src.getInfluencedIndicatorInstanceIdList(), mapNewId))
        );
        return this;
    }
    public SyncTargetPack addExpressionInfluence(IndicatorExpressionInfluenceEntity src){
        listExpressionInfluence.add(new CaseIndicatorExpressionInfluenceEntity()
                .setAppId(src.getAppId())
                .setIndicatorInstanceId(mapNewId.get(src.getIndicatorInstanceId()))
                .setInfluenceIndicatorInstanceIdList(fillExperssionString(src.getInfluenceIndicatorInstanceIdList(), mapNewId))
                .setInfluencedIndicatorInstanceIdList(fillExperssionString(src.getInfluencedIndicatorInstanceIdList(), mapNewId))
                .setId(null)
                .setDt(new Date()));
        return this;
    }

    //endregion


    private String fillExperssionString(String raw,Map<String,String> mapId){
        if(ShareUtil.XObject.isEmpty(raw)){
            return raw;
        }
        final String splitStr= EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr();
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
