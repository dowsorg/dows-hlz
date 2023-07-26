package org.dows.hep.biz.spel.meta;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/7/24 10:57
 */
public interface ISpelEvalBatch {

    default List<SpelEvalResult> eval(StandardEvaluationContext context){
        return evalSum(context, null);
    }

    List<SpelEvalResult> evalSum(StandardEvaluationContext context, Map<String, SpelEvalSumResult> mapSum );

}
