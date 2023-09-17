package org.dows.hep.biz.spel.meta;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/7/24 10:57
 */
public interface ISpelEval {

    default SpelEvalResult evalDelta(StandardEvaluationContext context){
        return evalDeltaSum(context, null);
    }

    default SpelEvalResult eval(StandardEvaluationContext context){
        return evalSum(context, null);
    }

    SpelEvalResult evalDeltaSum(StandardEvaluationContext context, Map<String, SpelEvalSumResult> mapSum );

    SpelEvalResult evalSum(StandardEvaluationContext context, Map<String, SpelEvalSumResult> mapSum );

}
