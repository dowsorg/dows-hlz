package org.dows.hep.biz.base.evaluate;

import cn.hutool.core.collection.CollUtil;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.evaluate.EvaluateExpressionEnum;
import org.dows.hep.api.base.evaluate.ExperssESCEnum;

import java.util.LinkedList;
import java.util.List;

public class EvaluateExpressVerifier {
    public static void verify(List<String> metaExperssList) {
        if (CollUtil.isEmpty(metaExperssList)) {
            throw new BizException(ExperssESCEnum.EXPRESS_NON_NULL.getDescr());
        }

        // check size
        int size = metaExperssList.size();
        if (size <= 4) {
            throw new BizException(ExperssESCEnum.EXPRESS_GRAMMAR_ERROR.getDescr());
        }

        // check begin and end
        String begin = metaExperssList.get(0);
        if (!EvaluateExpressionEnum.isBeginOrEnd(begin)) {
            throw new BizException(ExperssESCEnum.EXPRESS_BEGIN_END_ERROR.getDescr());
        }
        String end = metaExperssList.get(size - 1);
        if (!EvaluateExpressionEnum.isBeginOrEnd(end)) {
            throw new BizException(ExperssESCEnum.EXPRESS_BEGIN_END_ERROR.getDescr());
        }

        // check first and last
        String first = metaExperssList.get(1);
        if (EvaluateExpressionEnum.isRight(first)) {
            throw new BizException(ExperssESCEnum.EXPRESS_FIRST_LAST_ERROR.getDescr());
        }
        if (EvaluateExpressionEnum.isOperator(first)) {
            throw new BizException(ExperssESCEnum.EXPRESS_FIRST_LAST_ERROR.getDescr());
        }
        String last = metaExperssList.get(size - 2);
        if (EvaluateExpressionEnum.isLeft(last)) {
            throw new BizException(ExperssESCEnum.EXPRESS_FIRST_LAST_ERROR.getDescr());
        }
        if (EvaluateExpressionEnum.isOperator(last)) {
            throw new BizException(ExperssESCEnum.EXPRESS_FIRST_LAST_ERROR.getDescr());
        }

        // 清除开始结束符号
        metaExperssList.remove(0);
        metaExperssList.remove(size - 1);

        // 使用栈进行校验
        LinkedList<String> expressStack = new LinkedList<>();
        metaExperssList.forEach(meta -> {
            // 遇到 right 符号需要开始校验了
            while (EvaluateExpressionEnum.isRight(meta)) {
                String pop = expressStack.pop();
                if (EvaluateExpressionEnum.isLimit(pop)) {
                    boolean isBrother = EvaluateExpressionEnum.isBrother(pop, meta);
                    if (isBrother) {
                        break;
                    } else {
                        throw new BizException(ExperssESCEnum.EXPRESS_GRAMMAR_ERROR.getDescr());
                    }
                }
            }
            expressStack.push(meta);
        });
    }
}
