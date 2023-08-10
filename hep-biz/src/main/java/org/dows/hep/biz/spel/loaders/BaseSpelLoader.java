package org.dows.hep.biz.spel.loaders;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.spel.SpelVarKeyFormatter;
import org.dows.hep.biz.spel.meta.ISpelLoad;
import org.dows.hep.biz.spel.meta.SpelInput;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionItemEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/7/21 17:14
 */
@Slf4j
public abstract class BaseSpelLoader implements ISpelLoad {

    protected SpelInput fillInput(SpelInput rst,String exptPersonId, SnapCaseIndicatorExpressionEntity rowExpression, Collection<SnapCaseIndicatorExpressionItemEntity> rowsExpressionItem) {
        if (null == rst) {
            rst = new SpelInput();
        }
        Optional<ExperimentIndicatorInstanceRsEntity> optIndicator=Optional.ofNullable( PersonIndicatorIdCache.Instance().getIndicatorById(exptPersonId,rowExpression.getCasePrincipalId()));
        rst.setExpressionId(rowExpression.getCaseIndicatorExpressionId())
                .setIndicatorId(optIndicator.map(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId).orElse(null))
                .setRandom(Optional.ofNullable(rowExpression.getType()).orElse(0).equals(1))
                .setMin(optIndicator.map(i->BigDecimalUtil.tryParseDecimal(i.getMin(),null)).orElse(null))
                .setMax(optIndicator.map(i->BigDecimalUtil.tryParseDecimal(i.getMax(),null)).orElse(null));
        if (ShareUtil.XObject.isEmpty(rowsExpressionItem)) {
            return rst;
        }
        List<SpelInput.SpelExpressionItem> expressionItems = new ArrayList<>();
        SpelInput.SpelExpressionItem expressionItem;
        for (SnapCaseIndicatorExpressionItemEntity item : rowsExpressionItem) {
            //公式下限
            if (null==rst.getMin() && item.getCaseIndicatorExpressionItemId().equals(rowExpression.getMinIndicatorExpressionItemId())) {
                rst.setMin(BigDecimalUtil.tryParseDecimal(item.getResultExpression(), null));
                continue;
            }
            //公式上限
            if (null==rst.getMax() && item.getCaseIndicatorExpressionItemId().equals(rowExpression.getMaxIndicatorExpressionItemId())) {
                rst.setMax(BigDecimalUtil.tryParseDecimal(item.getResultExpression(), null));
                continue;
            }
            //随机指标忽略公式
            if (rst.isRandom()) {
                continue;
            }
            expressionItem = buildExpressionItem(exptPersonId, item);
            if (ShareUtil.XObject.isEmpty(expressionItem)) {
                continue;
            }
            expressionItems.add(expressionItem);
        }
        return rst.setExpressions(expressionItems);
    }

    protected SpelInput.SpelExpressionItem buildExpressionItem(String exptPersonId, SnapCaseIndicatorExpressionItemEntity src) {
        if (ShareUtil.XObject.isEmpty(src)) {
            return null;
        }
        return SpelInput.SpelExpressionItem.builder()
                .conditionExpression(buildExpressionString(exptPersonId, src.getConditionExpression(), src.getConditionNameList(), src.getConditionValList()))
                .resultExpression(buildExpressionString(exptPersonId, src.getResultExpression(), src.getResultNameList(), src.getResultValList()))
                .build();
    }


    protected String buildExpressionString(String exptPersonId, String rawExpression,String names,String vals){
        if(ShareUtil.XObject.isEmpty(names)){
            return rawExpression;
        }
        String[] splitNames=names.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr());
        if(ShareUtil.XObject.isEmpty(splitNames)){
            return rawExpression;
        }
        String[] splitVals=vals.split(EnumString.INDICATOR_EXPRESSION_LIST_SPLIT.getStr());
        List<String> missIds=new ArrayList<>();
        for(int i=0;i<splitVals.length;i++){
            String raw=splitVals[i];
            String fix= PersonIndicatorIdCache.Instance().getIndicatorIdBySourceId(exptPersonId,raw);
            if(ShareUtil.XObject.isEmpty(fix)){
                missIds.add(fix);
            }else{
                splitVals[i]= SpelVarKeyFormatter.getVariableKey(fix);
            }
        }
        for(int i=0;i<splitNames.length;i++){
            rawExpression=rawExpression.replace(splitNames[i],splitVals[i]);
        }
        if(missIds.size()>0) {
            logError("buildExpression", "miss exptIndicatorId. person:%s expression:%s missIds:%s names:%s vals:%s",
                    exptPersonId, rawExpression, String.join(",", missIds), names, vals);
            return "";
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
