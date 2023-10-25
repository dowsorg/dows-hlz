package org.dows.hep.biz.spel.loaders;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.enums.EnumString;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.spel.SpelVarKeyFormatter;
import org.dows.hep.biz.spel.meta.ISpelLoad;
import org.dows.hep.biz.spel.meta.SpelInput;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.CaseIndicatorExpressionItemEntity;
import org.dows.hep.entity.ExperimentIndicatorInstanceRsEntity;
import org.dows.hep.entity.IndicatorExpressionItemEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionItemEntity;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/7/21 17:14
 */
@Slf4j
public abstract class BaseSpelLoader implements ISpelLoad {

    protected SpelInput fillInput(SpelInput rst,String exptPersonId, SnapCaseIndicatorExpressionEntity rowExpression, List<SnapCaseIndicatorExpressionItemEntity> rowsExpressionItem) {
        if (null == rst) {
            rst = new SpelInput();
        }
        Optional<ExperimentIndicatorInstanceRsEntity> optIndicator=Optional.ofNullable( PersonIndicatorIdCache.Instance().getIndicatorById(exptPersonId,rowExpression.getCasePrincipalId()));
        rst.setExpressionId(rowExpression.getCaseIndicatorExpressionId())
                .setIndicatorId(optIndicator.map(ExperimentIndicatorInstanceRsEntity::getExperimentIndicatorInstanceId).orElse(null))
                .setRandom(Optional.ofNullable(rowExpression.getType()).orElse(0).equals(1))
                .setIndicatorMin(optIndicator.map(i->BigDecimalUtil.tryParseDecimal(i.getMin(),null)).orElse(null))
                .setIndicatorMax(optIndicator.map(i->BigDecimalUtil.tryParseDecimal(i.getMax(),null)).orElse(null));
        if (ShareUtil.XObject.isEmpty(rowsExpressionItem)) {
            return rst;
        }
        rowsExpressionItem.sort(Comparator.comparingInt(i->Optional.ofNullable(i.getSeq()).orElse(Integer.MAX_VALUE)));
        List<SpelInput.SpelExpressionItem> expressionItems = new ArrayList<>();
        SpelInput.SpelExpressionItem expressionItem;

        final boolean rawExpressionFlag= isRawExpression(rowExpression.getSource());
        for (SnapCaseIndicatorExpressionItemEntity item : rowsExpressionItem) {
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
            if (item.isMinOrMax()) {
                continue;
            }
            expressionItem = rawExpressionFlag ? buildExpressionItem(item) : buildExpressionItem(exptPersonId, item);
            if (ShareUtil.XObject.isEmpty(expressionItem)) {
                continue;
            }
            expressionItems.add(expressionItem);
        }
        return rst.setExpressions(expressionItems);
    }

    private Set<EnumIndicatorExpressionSource> rawSources=Set.of(EnumIndicatorExpressionSource.INDICATOR_JUDGE_RISK_FACTOR,
            EnumIndicatorExpressionSource.INDICATOR_JUDGE_CHECKRULE,
            EnumIndicatorExpressionSource.INDICATOR_JUDGE_REFINDICATOR,
            EnumIndicatorExpressionSource.INDICATOR_JUDGE_GOAL_CHECKRULE,
            EnumIndicatorExpressionSource.INDICATOR_JUDGE_GOAL_REFINDICATOR
            );
    protected boolean isRawExpression(Integer source){
        if(ShareUtil.XObject.isEmpty(source)){
            return false;
        }
        return rawSources.contains(EnumIndicatorExpressionSource.of(source));
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

    protected SpelInput.SpelExpressionItem buildExpressionItem(CaseIndicatorExpressionItemEntity src) {
        if (ShareUtil.XObject.isEmpty(src)) {
            return null;
        }
        return SpelInput.SpelExpressionItem.builder()
                .conditionExpression(buildExpressionString(src.getConditionExpression(), src.getConditionNameList(), src.getConditionValList()))
                .resultExpression(buildExpressionString(src.getResultExpression(), src.getResultNameList(), src.getResultValList()))
                .build();
    }
    protected SpelInput.SpelExpressionItem buildExpressionItem(IndicatorExpressionItemEntity src) {
        if (ShareUtil.XObject.isEmpty(src)) {
            return null;
        }
        return SpelInput.SpelExpressionItem.builder()
                .conditionExpression(buildExpressionString(src.getConditionExpression(), src.getConditionNameList(), src.getConditionValList()))
                .resultExpression(buildExpressionString(src.getResultExpression(), src.getResultNameList(), src.getResultValList()))
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
            String name=splitNames[i];
            String[] splits=name.split(EnumString.INDICATOR_EXPRESSION_SPLIT.getStr());
            boolean lastFlag=splits.length>1&&splits[1].endsWith("1");
            String raw=splitVals[i];
            String fix= PersonIndicatorIdCache.Instance().getIndicatorIdBySourceId(exptPersonId,raw);
            if(ShareUtil.XObject.isEmpty(fix)){
                missIds.add(fix);
            }else{
                splitVals[i]= SpelVarKeyFormatter.getVariableKey(fix,lastFlag);
            }
        }
        for(int i=0;i<splitNames.length;i++){
            rawExpression=rawExpression.replace(splitNames[i],splitVals[i]);
        }
        if(missIds.size()>0) {
            logError("buildExpression", "miss exptIndicatorId. person:%s expression:%s missIds:%s names:%s vals:%s",
                    exptPersonId, rawExpression, String.join(",", missIds), names, vals);
        }
        return rawExpression;
    }

    protected String buildExpressionString(String rawExpression, String names, String vals){
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
