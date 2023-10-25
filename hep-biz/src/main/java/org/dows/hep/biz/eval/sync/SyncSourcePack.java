package org.dows.hep.biz.eval.sync;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/10/25 9:43
 */
@Data
@Accessors(chain = true)
public class SyncSourcePack {

    private final Map<String,String> mapOwnerId=new HashMap<>();


    private final Map<String, IndicatorCategoryEntity> mapIndicaorCatgory=new HashMap<>();

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
