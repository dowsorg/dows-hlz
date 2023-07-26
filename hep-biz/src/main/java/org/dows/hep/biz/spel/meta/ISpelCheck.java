package org.dows.hep.biz.spel.meta;

import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author : wuzl
 * @date : 2023/7/24 11:00
 */
public interface ISpelCheck {
    boolean check(StandardEvaluationContext context);
}
