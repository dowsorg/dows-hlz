package org.dows.hep.biz.eval.sync;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author : wuzl
 * @date : 2023/10/25 9:46
 */
@Data
@Accessors(chain = true)
public class SyncCurrentPack {

    public SyncCurrentPack(String accountId, boolean coverFlag){
        this.accountId=accountId;
        this.coverFlag=coverFlag;
    }
    private final String accountId;
    private final boolean coverFlag;

    private final Map<String, CaseIndicatorCategoryEntity> curIndicaorCatgory=new HashMap<>();
    private final Map<String, CaseIndicatorInstanceEntity> curIndicaors =new HashMap<>();

    private final Set<String> newIndicatorIds =new HashSet<>();

    private final Set<String> newIndicaorCategoryIds=new HashSet<>();

    private final Map<String,String> curCaseIndicatorIds=new HashMap<>();


    public SyncCurrentPack fillNewIds(SyncSourcePack sourcePack) {
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

    public SyncCurrentPack fillIndicaorCatgory(CaseIndicatorCategoryEntity src){
        curIndicaorCatgory.put(src.getIndicatorCategoryId(), src);
        return this;
    }

    public SyncCurrentPack fillIndicator(CaseIndicatorInstanceEntity src){
        if(ShareUtil.XObject.isEmpty(src.getIndicatorInstanceId())){
            return this;
        }
        curIndicaors.put(src.getIndicatorInstanceId(), src);
        curCaseIndicatorIds.put(src.getCaseIndicatorInstanceId(),src.getIndicatorInstanceId());
        return this;
    }

    private final Map<String, CaseIndicatorRuleEntity> mapIndicatorRule=new HashMap<>();
    public SyncCurrentPack fillIndicatorRule(CaseIndicatorRuleEntity src){
        String indicatorId=curCaseIndicatorIds.get(src.getVariableId());
        if(ShareUtil.XObject.isEmpty(indicatorId)){
            return this;
        }
        mapIndicatorRule.put(indicatorId, src);
        return this;
    }

    private final Map<String, CaseIndicatorExpressionRefEntity> mapExpressionRef=new HashMap<>();
    public SyncCurrentPack fillExpressionRef(CaseIndicatorExpressionRefEntity src){
        String indicatorId=curCaseIndicatorIds.get(src.getReasonId());
        if(ShareUtil.XObject.isEmpty(indicatorId)){
            return this;
        }
        mapExpressionRef.put(indicatorId, src);
        return this;
    }

    private final Map<String, CaseIndicatorExpressionEntity> mapExpression=new HashMap<>();
    public SyncCurrentPack fillExpression(CaseIndicatorExpressionEntity src){
        String indicatorId=curCaseIndicatorIds.get(src.getCasePrincipalId());
        if(ShareUtil.XObject.isEmpty(indicatorId)){
            return this;
        }
        mapExpression.put(indicatorId, src);
        return this;
    }
    private final Map<String,CaseIndicatorExpressionInfluenceEntity> mapExpressionInfluence=new HashMap<>();

    public SyncCurrentPack fillExpressionInfluence(CaseIndicatorExpressionInfluenceEntity src){
        String indicatorId=curCaseIndicatorIds.get(src.getIndicatorInstanceId());
        if(ShareUtil.XObject.isEmpty(indicatorId)){
            return this;
        }
        mapExpressionInfluence.put(indicatorId, src);
        return this;
    }
    public void clear(){
        curIndicaorCatgory.clear();
        curIndicaors.clear();
        newIndicatorIds.clear();
        newIndicaorCategoryIds.clear();
        curCaseIndicatorIds.clear();
        mapIndicatorRule.clear();
        mapExpressionRef.clear();
        mapExpression.clear();
        mapExpressionInfluence.clear();
    }

}
