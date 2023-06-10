package org.dows.hep.biz.base.evaluate;

import cn.hutool.core.collection.CollUtil;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.evaluate.EvaluateExpressionEnum;
import org.dows.hep.api.base.evaluate.ExperssESCEnum;

import java.util.List;

public class ExpressComputer {
    public static double compute(List<String> metaExpressList) {
        // 利用栈循环计算
        return 0.0;
    }

    public static double compute3(List<String> list3) {
        if (CollUtil.isEmpty(list3)) {
            throw new BizException(ExperssESCEnum.EXPRESS_NON_NULL);
        }

        int size = list3.size();
        if (size != 3) {
            throw new BizException(ExperssESCEnum.EXPRESS_NON_NULL);
        }

        String ele1 = list3.get(0);
        String ele2 = list3.get(1);
        String ele3 = list3.get(2);

        double num1 = Double.parseDouble(ele1);
        double num2 = Double.parseDouble(ele3);

        double result = 0.0;
        if (EvaluateExpressionEnum.OPERATOR_ARITHMETIC_ADDITION.getCode().equals(ele2)) {
            result = num1 + num2;
        }
        if (EvaluateExpressionEnum.OPERATOR_ARITHMETIC_SUBTRACTION.getCode().equals(ele2)) {
            result = num1 - num2;
        }
        if (EvaluateExpressionEnum.OPERATOR_ARITHMETIC_MULTIPLICATION.getCode().equals(ele2)) {
            result = num1 * num2;
        }
        if (EvaluateExpressionEnum.OPERATOR_ARITHMETIC_DIVISION.getCode().equals(ele2)) {
            result = num1 / num2;
        }
        if (EvaluateExpressionEnum.OPERATOR_ARITHMETIC_REMAINDER.getCode().equals(ele2)) {
            result = num1 % num2;
        }
        return result;
    }
}
