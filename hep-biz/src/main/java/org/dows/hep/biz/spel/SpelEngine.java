package org.dows.hep.biz.spel;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.spel.loaders.FromSnapshotLoader;
import org.dows.hep.biz.spel.meta.*;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;



/**
 * @author : wuzl
 * @date : 2023/7/21 11:09
 */
@Component
@Slf4j
public class SpelEngine {
    //region singleton
    private static volatile SpelEngine s_instance;

    public static SpelEngine Instance() {
        return s_instance;
    }

    private SpelEngine() {
        s_instance = this;
    }

    @Autowired
    private FromSnapshotLoader fromSnapshotLoader;

    @Autowired
    private ExperimentSpelCache experimentSpelCache;
    //endregion

    //region facade
    public ISpelFlow loadFromSnapshot(){
        return new SpelLoadProxy(fromSnapshotLoader);
    }

    public ISpelFlow loadFromSpelCache(){
        return new SpelLoadProxy(experimentSpelCache);
    }

    public ISpelExecute loadWith(SpelInput input) {
        return new SpelExecuteProxy(input);
    }

    public ISpelExecuteBatch loadWith(List<SpelInput> input) {
        return new SpelExecuteBatchProxy(input);
    }

    //endregeion

    private void logError(String func, String msg,Object... args){
        logError(null, func, msg, args);
    }
    private void logError(Throwable ex, String func, String msg,Object... args){
        String str=String.format("%s.%s@%s[%s] %s", this.getClass().getName(), func, LocalDateTime.now(),this.hashCode(),
                String.format(Optional.ofNullable(msg).orElse(""), args));
        log.error(str,ex);

    }


    //region sealed execute
    private boolean check(SpelInput input, StandardEvaluationContext context) {
        SpelCheckResult rst=coreCheck(new SpelExpressionParser(), input,context);
        return Optional.ofNullable(rst)
                .map(SpelCheckResult::getValBoolean)
                .orElse(false);
    }

    private List<SpelCheckResult> check(List<SpelInput> input, StandardEvaluationContext context) {
        List<SpelCheckResult> rst=new ArrayList<>();
        if(ShareUtil.XObject.isEmpty(input)){
            return rst;
        }
        ExpressionParser parser=new SpelExpressionParser();
        for(SpelInput item:input) {
            SpelCheckResult checkResult = coreCheck(parser, item, context);
            if (null == checkResult) {
                continue;
            }
            rst.add(checkResult);
        }
        return rst;
    }
    private SpelCheckResult coreCheck(ExpressionParser parser, SpelInput input, StandardEvaluationContext context) {
        if (ShareUtil.XObject.isEmpty(input)) {
            return null;
        }
        SpelCheckResult rst = SpelCheckResult.builder()
                .reasonId(input.getReasonId())
                .expressionId(input.getExpressionId())
                .build();
        if(input.isRandom()){
            return rst.setValBoolean(ShareUtil.XRandom.randomBoolean());
        }
        if (ShareUtil.XObject.isEmpty(input.getExpressions())) {
            return rst.setValBoolean(false);
        }
        Boolean val = false;
        for (SpelInput.SpelExpressionItem item : input.getExpressions()) {
            if(ShareUtil.XObject.notEmpty(item.getConditionExpression())){
                if(!coreGetBoolean(parser, item.getConditionExpression(), context)){
                    continue;
                }
                else{
                    val=true;
                    break;
                }
            }
            if (ShareUtil.XObject.isEmpty(item.getResultExpression())) {
                continue;
            }
            val = coreGetBoolean(parser, item.getResultExpression(), context);
            break;
        }
        return rst.setValBoolean(val);
    }

    private SpelEvalResult evalSum(SpelInput input, StandardEvaluationContext context, Map<String, SpelEvalSumResult> mapSum) {
        return coreEval(new SpelExpressionParser(), input, context, mapSum,false);
    }

    private List<SpelEvalResult> evalSum(List<SpelInput> input, StandardEvaluationContext context, Map<String, SpelEvalSumResult> mapSum) {
        List<SpelEvalResult> rst = new ArrayList<>();
        if (ShareUtil.XObject.isEmpty(input)) {
            return rst;
        }
        ExpressionParser parser = new SpelExpressionParser();
        for (SpelInput item : input) {
            SpelEvalResult evalRst = coreEval(parser, item, context, mapSum,false);
            if (ShareUtil.XObject.isEmpty(evalRst)) {
                continue;
            }
            rst.add(evalRst);
        }
        return rst;
    }

    private SpelEvalResult evalDeltaSum(SpelInput input, StandardEvaluationContext context, Map<String, SpelEvalSumResult> mapSum) {
        return coreEval(new SpelExpressionParser(), input, context, mapSum,true);
    }

    private List<SpelEvalResult> evalDeltaSum(List<SpelInput> input, StandardEvaluationContext context, Map<String, SpelEvalSumResult> mapSum) {
        List<SpelEvalResult> rst = new ArrayList<>();
        if (ShareUtil.XObject.isEmpty(input)) {
            return rst;
        }
        ExpressionParser parser = new SpelExpressionParser();
        for (SpelInput item : input) {
            SpelEvalResult evalRst = coreEval(parser, item, context, mapSum,true);
            if (ShareUtil.XObject.isEmpty(evalRst)) {
                continue;
            }
            rst.add(evalRst);
        }
        return rst;
    }
    private SpelEvalResult coreEval(ExpressionParser parser, SpelInput input, StandardEvaluationContext context, Map<String, SpelEvalSumResult> mapSum,boolean deltaFlag) {
        if (ShareUtil.XObject.isEmpty(input)) {
            return null;
        }
        SpelEvalResult rst = SpelEvalResult.builder()
                .reasonId(input.getReasonId())
                .expressionId(input.getExpressionId())
                .indicatorId(input.getIndicatorId())
                .min(input.getMin())
                .max(input.getMax())
                .build();
        if (input.isRandom()) {
            //随机
            if (ShareUtil.XObject.notNumber(input.getMin())
                    || ShareUtil.XObject.notNumber(input.getMax())) {
                logError("coreEval", "invalidRandom input:%s", input);
                return rst;
            }
            BigDecimal val = ShareUtil.XRandom.randomBigDecimal(input.getMin(), input.getMax(), 2);
            coreEvalSum(mapSum, rst.setVal(val).setValNumber(val));
            return rst;
        }

        if (ShareUtil.XObject.isEmpty(input.getExpressions())) {
            return rst;
        }
        if(ShareUtil.XObject.notEmpty(input.getIndicatorId())) {
            Object curVal = context.lookupVariable(SpelVarKeyFormatter.getVariableKey(input.getIndicatorId(), false));
            rst.setCurVal(curVal);
        }

        Object val = null;
        for (SpelInput.SpelExpressionItem item : input.getExpressions()) {
            if (ShareUtil.XObject.isEmpty(item.getResultExpression())) {
                continue;
            }
            if (ShareUtil.XObject.isEmpty(item.getConditionExpression())
                    || coreGetBoolean(parser, item.getConditionExpression(), context)) {
                val = coreGetValue(parser, item.getResultExpression(), context);
                break;
            }
        }
        if (ShareUtil.XObject.isEmpty(val)) {
            return rst;
        }
        if (ShareUtil.XObject.notNumber(val)) {
            coreEvalSum(mapSum, rst.setVal(val).setValNumber(null));
            return rst;
        }
        BigDecimal valNumber=BigDecimalUtil.valueOf(val);
        if(!deltaFlag){
            return rst.setVal(val).setValNumber(valNumber);
        }

        BigDecimal curValNumber = BigDecimalUtil.valueOf(rst.getCurVal(), BigDecimal.ZERO);
        BigDecimal change = valNumber.subtract(curValNumber);
        if (input.hasFactor()) {
            change = change.multiply(input.getFactor());
        }
        if (ShareUtil.XObject.allEmpty(input.getMin(), input.getMax())) {
            coreEvalSum(mapSum, rst.setVal(change).setValNumber(change));
            return rst;
        }
        valNumber = curValNumber.add(change);
        if (ShareUtil.XObject.notEmpty(input.getMin()) && valNumber.compareTo(input.getMin()) < 0) {
            valNumber = input.getMin();
        }
        if (ShareUtil.XObject.notEmpty(input.getMax()) && input.getMax().compareTo(valNumber) < 0) {
            valNumber = input.getMax();
        }
        change = valNumber.subtract(curValNumber);
        coreEvalSum(mapSum, rst.setVal(change).setValNumber(change));
        return rst;
    }
    private void coreEvalSum(Map<String, SpelEvalSumResult> mapSum,SpelEvalResult item) {
        if (null==mapSum|| ShareUtil.XObject.isEmpty(item)) {
            return;
        }
        SpelEvalSumResult evalSumItem = mapSum.computeIfAbsent(item.getIndicatorId(), k -> new SpelEvalSumResult()
                .setExperimentIndicatorId(item.getIndicatorId())
                .setMin(item.getMin())
                .setMax(item.getMax()));
        evalSumItem.setCurVal(item.getCurVal());
        if (ShareUtil.XObject.notNumber(item.getVal())) {
            if (ShareUtil.XObject.notNumber(evalSumItem.getVal())) {
                evalSumItem.setVal(item.getVal()).setValNumber(null);
            }
            return;
        }
        BigDecimal sumNumber = BigDecimalUtil.valueOf(evalSumItem.getVal(), BigDecimal.ZERO);
        sumNumber = BigDecimalUtil.add(sumNumber, item.getValNumber());
        evalSumItem.setVal(sumNumber).setValNumber(sumNumber);
    }

    private boolean coreGetBoolean(ExpressionParser parser, String expression,StandardEvaluationContext context){
        if(ShareUtil.XObject.isEmpty(expression)){
            return false;
        }
        Object obj= coreGetValue(parser, expression, context);
        if(ShareUtil.XObject.isEmpty(obj)){
            return false;
        }
        if(obj instanceof Boolean){
            return (Boolean)obj;
        }
        return obj.toString().toLowerCase().equals("true");

    }

    private Object coreGetValue(ExpressionParser parser,String expression,StandardEvaluationContext context){
        return parser.parseExpression(expression).getValue(context);
    }
    private <T> T coreGetValue(ExpressionParser parser,String expression,StandardEvaluationContext context,Class<T> desiredResultType) {
        return parser.parseExpression(expression).getValue(context, desiredResultType);
    }

    //endregion

    //region sealed proxy
    private class SpelLoadProxy implements ISpelFlow {
        private SpelLoadProxy(ISpelLoad target){
            this.target=target;
        }
        private final ISpelLoad target;

        @Override
        public ISpelExecute withReasonId(String experimentId,String experimentPersonId,  String reasonId, Integer source) {
            return new SpelExecuteProxy(target.withReasonId(experimentId, experimentPersonId, reasonId, source));
        }

        @Override
        public ISpelExecuteBatch withReasonId(String experimentId,String experimentPersonId,  Collection<String> reasonIds, Integer source) {
            return new SpelExecuteBatchProxy(target.withReasonId(experimentId,  experimentPersonId,reasonIds, source));
        }

        @Override
        public ISpelExecute withExpressionId(String experimentId,String experimentPersonId,  String expressionId, Integer source) {
            return new SpelExecuteProxy( target.withExpressionId(experimentId, experimentPersonId, expressionId, source));
        }

        @Override
        public ISpelExecuteBatch withExpressionId(String experimentId,String experimentPersonId,  Collection<String> expressionIds, Integer source) {
            return new SpelExecuteBatchProxy( target.withExpressionId(experimentId, experimentPersonId,expressionIds, source));
        }
    }

    private class SpelExecuteProxy implements ISpelExecute {
        private SpelExecuteProxy(SpelInput target) {
            this.target = target;
        }

        private final SpelInput target;


        @Override
        public boolean check(StandardEvaluationContext context) {
            return SpelEngine.Instance().check(this.target, context);
        }

        @Override
        public SpelEvalResult evalDeltaSum(StandardEvaluationContext context, Map<String, SpelEvalSumResult> mapSum) {
            return SpelEngine.Instance().evalDeltaSum(this.target, context, mapSum);
        }

        @Override
        public SpelEvalResult evalSum(StandardEvaluationContext context, Map<String, SpelEvalSumResult> mapSum) {
            return SpelEngine.Instance().evalSum(this.target, context, mapSum);
        }


        @Override
        public SpelInput getInput() {
            return target;
        }
    }

    private class SpelExecuteBatchProxy implements ISpelExecuteBatch {
        private SpelExecuteBatchProxy(List<SpelInput> input) {
            target = input;
        }

        private final List<SpelInput> target;


        @Override
        public List<SpelCheckResult> check(StandardEvaluationContext context) {
            return SpelEngine.Instance().check(this.target, context);
        }

        @Override
        public List<SpelEvalResult> evalSum(StandardEvaluationContext context, Map<String, SpelEvalSumResult> mapSum) {
            return SpelEngine.Instance().evalSum(this.target, context, mapSum);
        }

        @Override
        public List<SpelEvalResult> evalDeltaSum(StandardEvaluationContext context, Map<String, SpelEvalSumResult> mapSum) {
            return SpelEngine.Instance().evalDeltaSum(this.target, context, mapSum);
        }

        @Override
        public List<SpelInput> getInput() {
            return target;
        }
    }
    //endregion

}
