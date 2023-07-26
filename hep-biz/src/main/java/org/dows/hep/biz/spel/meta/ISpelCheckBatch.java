package org.dows.hep.biz.spel.meta;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/24 11:01
 */
public interface ISpelCheckBatch {
    List<SpelCheckResult> check(StandardEvaluationContext context);

}
