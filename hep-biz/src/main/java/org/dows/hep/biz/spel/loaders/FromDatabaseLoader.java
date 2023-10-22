package org.dows.hep.biz.spel.loaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.dao.IndicatorExpressionDao;
import org.dows.hep.biz.dao.IndicatorExpressionRefDao;
import org.dows.hep.biz.spel.meta.SpelInput;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.IndicatorExpressionEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.entity.IndicatorExpressionRefEntity;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/10/16 15:13
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FromDatabaseLoader extends BaseSpelLoader {
    private final IndicatorExpressionRefDao IndicatorExpressionRefDao;
    private final IndicatorExpressionDao IndicatorExpressionDao;

    @Override
    public SpelInput withReasonId(String experimentId, String experimentPersonId, String reasonId, Integer source,Integer... sources) {
        return null;
    }

    @Override
    public List<SpelInput> withReasonId(String experimentId, String experimentPersonId, Collection<String> reasonIds, Integer source,Integer... sources) {
        return null;
    }


    @Override
    public SpelInput withExpressionId(String experimentId, String experimentPersonId, String expressionId, Integer source) {
        return null;
    }

    @Override
    public List<SpelInput> withExpressionId(String experimentId, String experimentPersonId, Collection<String> expressionIds, Integer source) {
        return null;
    }

    public Map<String,List<SpelInput>> withReasonId(Collection<String> reasonIds){
        Map<String,List<SpelInput>> rst=new HashMap<>();
        if(ShareUtil.XObject.isEmpty(reasonIds)){
            return rst;
        }
        Map<String,String> mapOwnerResonId=new HashMap<>();
        List<IndicatorExpressionRefEntity> rowsExpressionRef=IndicatorExpressionRefDao.getByReasonId(null, reasonIds,
                IndicatorExpressionRefEntity::getReasonId,
                IndicatorExpressionRefEntity::getIndicatorExpressionId);
        if(ShareUtil.XObject.isEmpty(rowsExpressionRef)){
            return rst;
        }
        final List<String> expressionIds=new ArrayList<>();
        rowsExpressionRef.forEach(i->{
            expressionIds.add(i.getIndicatorExpressionId());
            mapOwnerResonId.put(i.getIndicatorExpressionId(), i.getReasonId());
        });
        
        List<IndicatorExpressionEntity> rowsExpression= IndicatorExpressionDao.getByExpressionId(expressionIds,null,
            IndicatorExpressionEntity::getIndicatorExpressionId,
            IndicatorExpressionEntity::getPrincipalId,
            IndicatorExpressionEntity::getSource,
            IndicatorExpressionEntity::getType,
            IndicatorExpressionEntity::getMaxIndicatorExpressionItemId,
            IndicatorExpressionEntity::getMinIndicatorExpressionItemId
        );
        if(ShareUtil.XObject.isEmpty(rowsExpression)){
              return rst;
        }
        Map<String,String> mapMinXMaxIds=new HashMap<>();
        expressionIds.clear();
        rowsExpression.forEach(i->{
            expressionIds.add(i.getIndicatorExpressionId());
            if(ShareUtil.XObject.notEmpty(i.getMinIndicatorExpressionItemId())){
                mapMinXMaxIds.put(i.getMinIndicatorExpressionItemId(), i.getIndicatorExpressionId());
            }
            if(ShareUtil.XObject.notEmpty(i.getMaxIndicatorExpressionItemId())) {
                mapMinXMaxIds.put(i.getMaxIndicatorExpressionItemId(), i.getIndicatorExpressionId());
            }
        });
        List<IndicatorExpressionItemEntity> rowsExpressionItem=IndicatorExpressionDao.getSubByLeadIds (expressionIds,
                IndicatorExpressionItemEntity::getIndicatorExpressionItemId,
                IndicatorExpressionItemEntity::getIndicatorExpressionId,
                IndicatorExpressionItemEntity::getConditionExpression,
                IndicatorExpressionItemEntity::getConditionNameList,
                IndicatorExpressionItemEntity::getConditionValList,
                IndicatorExpressionItemEntity::getResultExpression,
                IndicatorExpressionItemEntity::getResultNameList,
                IndicatorExpressionItemEntity::getResultValList,
                IndicatorExpressionItemEntity::getSeq
        );
        List<IndicatorExpressionItemEntity> rowsMinMaxExpressionItem=IndicatorExpressionDao.getSubBySubIds(mapMinXMaxIds.keySet(),
                IndicatorExpressionItemEntity::getIndicatorExpressionItemId,
                IndicatorExpressionItemEntity::getIndicatorExpressionId,
                IndicatorExpressionItemEntity::getConditionExpression,
                IndicatorExpressionItemEntity::getConditionNameList,
                IndicatorExpressionItemEntity::getConditionValList,
                IndicatorExpressionItemEntity::getResultExpression,
                IndicatorExpressionItemEntity::getResultNameList,
                IndicatorExpressionItemEntity::getResultValList,
                IndicatorExpressionItemEntity::getSeq
        );
        rowsExpressionItem.sort(Comparator.comparing(IndicatorExpressionItemEntity::getIndicatorExpressionItemId)
                .thenComparingInt(i->Optional.ofNullable(i.getSeq()).orElse(Integer.MAX_VALUE)));

        Map<String,IndicatorExpressionEntity> mapExpression=ShareUtil.XCollection.toMap(rowsExpression, IndicatorExpressionEntity::getIndicatorExpressionId);
        Map<String,List<IndicatorExpressionItemEntity>> mapExpressionItem=ShareUtil.XCollection.groupBy(rowsExpressionItem, IndicatorExpressionItemEntity::getIndicatorExpressionId);
        rowsMinMaxExpressionItem.forEach(i->{
            List<IndicatorExpressionItemEntity> dst=mapExpressionItem.get(mapMinXMaxIds.get(i.getIndicatorExpressionItemId()));
            if(null==dst){
                return;
            }
            dst.add(i);
        });
        mapExpression.forEach((k,v)->{
            String reasonId=mapOwnerResonId.get(k);
            List<SpelInput> dst= rst.computeIfAbsent(reasonId, x->new ArrayList<>());
            SpelInput input=new SpelInput(v.getSource()).setReasonId(reasonId);
            dst.add(fillInput(input,v,mapExpressionItem.get(k)));
        });
        return rst;
    }

    protected SpelInput fillInput(SpelInput rst, IndicatorExpressionEntity rowExpression, List<IndicatorExpressionItemEntity> rowsExpressionItem) {
        if (null == rst) {
            rst = new SpelInput();
        }

        rst.setExpressionId(rowExpression.getIndicatorExpressionId())
                .setIndicatorId(rowExpression.getPrincipalId())
                .setRandom(Optional.ofNullable(rowExpression.getType()).orElse(0).equals(1));
        if (ShareUtil.XObject.isEmpty(rowsExpressionItem)) {
            return rst;
        }
        rowsExpressionItem.sort(Comparator.comparingInt(i->Optional.ofNullable(i.getSeq()).orElse(Integer.MAX_VALUE)));
        List<SpelInput.SpelExpressionItem> expressionItems = new ArrayList<>();
        SpelInput.SpelExpressionItem expressionItem;
        for (IndicatorExpressionItemEntity item : rowsExpressionItem) {
            //公式下限
            if (item.getIndicatorExpressionItemId().equals(rowExpression.getMinIndicatorExpressionItemId())) {
                rst.setMin(BigDecimalUtil.tryParseDecimal(item.getResultExpression(), null));
                continue;
            }
            //公式上限
            if (item.getIndicatorExpressionItemId().equals(rowExpression.getMaxIndicatorExpressionItemId())) {
                rst.setMax(BigDecimalUtil.tryParseDecimal(item.getResultExpression(), null));
                continue;
            }
            //随机指标忽略公式
            if (rst.isRandom()) {
                continue;
            }
            expressionItem = buildExpressionItem(item);
            if (ShareUtil.XObject.isEmpty(expressionItem)) {
                continue;
            }
            expressionItems.add(expressionItem);
        }
        return rst.setExpressions(expressionItems);
    }


}
