package org.dows.hep.biz.spel.loaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.dao.CaseIndicatorExpressionDao;
import org.dows.hep.biz.dao.CaseIndicatorExpressionRefDao;
import org.dows.hep.biz.spel.SpelVarKeyFormatter;
import org.dows.hep.biz.spel.meta.ISpelLoad;
import org.dows.hep.biz.spel.meta.SpelInput;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.CaseIndicatorExpressionEntity;
import org.dows.hep.entity.CaseIndicatorExpressionItemEntity;
import org.dows.hep.entity.CaseIndicatorExpressionRefEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/10/16 15:13
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FromCaseLoader implements ISpelLoad {
    private final CaseIndicatorExpressionRefDao caseIndicatorExpressionRefDao;
    private final CaseIndicatorExpressionDao caseIndicatorExpressionDao;

    @Override
    public SpelInput withReasonId(String experimentId, String experimentPersonId, String reasonId, Integer source) {
        return null;
    }

    @Override
    public List<SpelInput> withReasonId(String experimentId, String experimentPersonId, Collection<String> reasonIds, Integer source) {
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

    public Map<String,List<SpelInput>> withReasonId(String casePersonId,Collection<String> reasonIds){
        Map<String,List<SpelInput>> rst=new HashMap<>();
        if(ShareUtil.XObject.isEmpty(reasonIds)){
            return rst;
        }
        Map<String,String> mapOwnerResonId=new HashMap<>();
        List<CaseIndicatorExpressionRefEntity> rowsExpressionRef=caseIndicatorExpressionRefDao.getByReasonId(null, reasonIds,
                CaseIndicatorExpressionRefEntity::getReasonId,
                CaseIndicatorExpressionRefEntity::getIndicatorExpressionId);
        if(ShareUtil.XObject.isEmpty(rowsExpressionRef)){
            return rst;
        }
        final List<String> expressionIds=new ArrayList<>();
        rowsExpressionRef.forEach(i->{
            expressionIds.add(i.getIndicatorExpressionId());
            mapOwnerResonId.put(i.getIndicatorExpressionId(), i.getReasonId());
        });
        
        List<CaseIndicatorExpressionEntity> rowsExpression= caseIndicatorExpressionDao.getByExperssionIds(expressionIds,null,
            CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId,
            CaseIndicatorExpressionEntity::getCasePrincipalId,
            CaseIndicatorExpressionEntity::getType,
            CaseIndicatorExpressionEntity::getMaxIndicatorExpressionItemId,
            CaseIndicatorExpressionEntity::getMinIndicatorExpressionItemId
        );
        if(ShareUtil.XObject.isEmpty(rowsExpression)){
              return rst;
        }
        Map<String,String> mapMinXMaxIds=new HashMap<>();
        expressionIds.clear();
        rowsExpression.forEach(i->{
            expressionIds.add(i.getCaseIndicatorExpressionId());
            if(ShareUtil.XObject.notEmpty(i.getMinIndicatorExpressionItemId())){
                mapMinXMaxIds.put(i.getMinIndicatorExpressionItemId(), i.getCaseIndicatorExpressionId());
            }
            if(ShareUtil.XObject.notEmpty(i.getMaxIndicatorExpressionItemId())) {
                mapMinXMaxIds.put(i.getMaxIndicatorExpressionItemId(), i.getCaseIndicatorExpressionId());
            }
        });
        List<CaseIndicatorExpressionItemEntity> rowsExpressionItem=caseIndicatorExpressionDao.getSubByLeadIds (expressionIds,
                CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId,
                CaseIndicatorExpressionItemEntity::getIndicatorExpressionId,
                CaseIndicatorExpressionItemEntity::getConditionExpression,
                CaseIndicatorExpressionItemEntity::getConditionNameList,
                CaseIndicatorExpressionItemEntity::getConditionValList,
                CaseIndicatorExpressionItemEntity::getResultExpression,
                CaseIndicatorExpressionItemEntity::getResultNameList,
                CaseIndicatorExpressionItemEntity::getResultValList,
                CaseIndicatorExpressionItemEntity::getSeq
        );
        List<CaseIndicatorExpressionItemEntity> rowsMinMaxExpressionItem=caseIndicatorExpressionDao.getSubBySubIds(mapMinXMaxIds.keySet(),
                CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId,
                CaseIndicatorExpressionItemEntity::getIndicatorExpressionId,
                CaseIndicatorExpressionItemEntity::getConditionExpression,
                CaseIndicatorExpressionItemEntity::getConditionNameList,
                CaseIndicatorExpressionItemEntity::getConditionValList,
                CaseIndicatorExpressionItemEntity::getResultExpression,
                CaseIndicatorExpressionItemEntity::getResultNameList,
                CaseIndicatorExpressionItemEntity::getResultValList,
                CaseIndicatorExpressionItemEntity::getSeq
        );
        rowsExpressionItem.sort(Comparator.comparing(CaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId)
                .thenComparingInt(i->Optional.ofNullable(i.getSeq()).orElse(Integer.MAX_VALUE)));

        Map<String,CaseIndicatorExpressionEntity> mapExpression=ShareUtil.XCollection.toMap(rowsExpression, CaseIndicatorExpressionEntity::getCaseIndicatorExpressionId);
        Map<String,List<CaseIndicatorExpressionItemEntity>> mapExpressionItem=ShareUtil.XCollection.groupBy(rowsExpressionItem, CaseIndicatorExpressionItemEntity::getIndicatorExpressionId);
        rowsMinMaxExpressionItem.forEach(i->{
            List<CaseIndicatorExpressionItemEntity> dst=mapExpressionItem.get(mapMinXMaxIds.get(i.getCaseIndicatorExpressionItemId()));
            if(null==dst){
                return;
            }
            dst.add(i);
        });
        mapExpression.forEach((k,v)->{
            String reasonId=mapOwnerResonId.get(k);
            List<SpelInput> dst= rst.computeIfAbsent(reasonId, x->new ArrayList<>());
            SpelInput input=new SpelInput().setReasonId(reasonId);
            dst.add(fillInput(input,v,mapExpressionItem.get(k)));
        });
        return rst;
    }

    protected SpelInput fillInput(SpelInput rst, CaseIndicatorExpressionEntity rowExpression, List<CaseIndicatorExpressionItemEntity> rowsExpressionItem) {
        if (null == rst) {
            rst = new SpelInput();
        }

        rst.setExpressionId(rowExpression.getCaseIndicatorExpressionId())
                .setIndicatorId(rowExpression.getPrincipalId())
                .setRandom(Optional.ofNullable(rowExpression.getType()).orElse(0).equals(1));
        if (ShareUtil.XObject.isEmpty(rowsExpressionItem)) {
            return rst;
        }
        rowsExpressionItem.sort(Comparator.comparingInt(i->Optional.ofNullable(i.getSeq()).orElse(Integer.MAX_VALUE)));
        List<SpelInput.SpelExpressionItem> expressionItems = new ArrayList<>();
        SpelInput.SpelExpressionItem expressionItem;
        for (CaseIndicatorExpressionItemEntity item : rowsExpressionItem) {
            //公式下限
            if (item.getCaseIndicatorExpressionItemId().equals(rowExpression.getMinIndicatorExpressionItemId())) {
                rst.setMin(BigDecimalUtil.tryParseDecimal(item.getResultExpression(), null));
                continue;
            }
            //公式上限
            if (item.getCaseIndicatorExpressionItemId().equals(rowExpression.getMaxIndicatorExpressionItemId())) {
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

    protected SpelInput.SpelExpressionItem buildExpressionItem(CaseIndicatorExpressionItemEntity src) {
        if (ShareUtil.XObject.isEmpty(src)) {
            return null;
        }
        return SpelInput.SpelExpressionItem.builder()
                .conditionExpression(buildExpressionString(src.getConditionExpression(), src.getConditionNameList(), src.getConditionValList()))
                .resultExpression(buildExpressionString(src.getResultExpression(), src.getResultNameList(), src.getResultValList()))
                .build();
    }

    protected String buildExpressionString(String rawExpression,String names,String vals){
        if(ShareUtil.XObject.isEmpty(names)){
            return rawExpression;
        }
        String[] splitNames=names.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr());
        if(ShareUtil.XObject.isEmpty(splitNames)){
            return rawExpression;
        }
        String[] splitVals=vals.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr());
        for(int i=0;i<splitVals.length;i++){
            String name=splitNames[i];
            String[] splits=name.split(EnumString.INDICATOR_EXPRESSION_SPLIT.getStr());
            boolean lastFlag=splits.length>1&&splits[1].endsWith("1");
            splitVals[i]= SpelVarKeyFormatter.getVariableKey(splitVals[i],lastFlag);
        }
        for(int i=0;i<splitNames.length;i++){
            rawExpression=rawExpression.replace(splitNames[i],splitVals[i]);
        }
        return rawExpression;
    }

    protected void logError(String func, String msg,Object... args){
        logError(null, func, msg, args);
    }
    protected void logError(Throwable ex, String func, String msg,Object... args){
        String str=String.format("%s.%s@%s[%s] %s", this.getClass().getName(), func, LocalDateTime.now(),this.hashCode(),
                String.format(Optional.ofNullable(msg).orElse(""), args));
        log.error(str,ex);

    }
}
