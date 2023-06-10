package org.dows.hep.api.base.evaluate;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum EvaluateExpressionEnum {
    // limit
    LIMIT_LEFT_PARENTHESIS("(", "LIMIT_LEFT_PARENTHESIS", "LIMIT", "左圆括号"),
    LIMIT_RIGHT_PARENTHESIS(")", "LIMIT_RIGHT_PARENTHESIS", "LIMIT", "右圆括号"),
    LIMIT_LEFT_SQUARE_BRACKETS("[", "LIMIT_LEFT_SQUARE_BRACKETS", "LIMIT", "左方括号"),
    LIMIT_RIGHT_SQUARE_BRACKETS("]", "LIMIT_RIGHT_SQUARE_BRACKETS", "LIMIT", "右方括号"),
    LIMIT_BEGIN_END("#", "LIMIT_BEGIN_END", "LIMIT", "起始结束符"),


    // operand
    OPERAND_PLACEHOLDER("${}", "OPERAND_PLACEHOLDER", "OPERAND", "占位符"),


    // operator
    OPERATOR_ARITHMETIC_ADDITION("+", "OPERATOR_ARITHMETIC_ADDITION", "OPERATOR", "加法运算符"),
    OPERATOR_ARITHMETIC_SUBTRACTION("-", "OPERATOR_ARITHMETIC_SUBTRACTION", "OPERATOR", "减法运算符"),
    OPERATOR_ARITHMETIC_MULTIPLICATION("*", "OPERATOR_ARITHMETIC_MULTIPLICATION", "OPERATOR", "乘法运算符"),
    OPERATOR_ARITHMETIC_DIVISION("/", "OPERATOR_ARITHMETIC_DIVISION", "OPERATOR", "除法运算符"),
    OPERATOR_ARITHMETIC_REMAINDER("%", "OPERATOR_ARITHMETIC_REMAINDER", "OPERATOR", "求余运算符"),

    ;

    private final String symbol;
    private final String code;
    private final String group;
    private final String name;

    // 界限符
    public static List<EvaluateExpressionEnum> listLimitGroup() {
        return listGroup("LIMIT");
    }

    // 操作数
    public static List<EvaluateExpressionEnum> listOperandGroup() {
        return listGroup("OPERAND");
    }

    // 运算符
    public static List<EvaluateExpressionEnum> listOperatorGroup() {
        return listGroup("OPERATOR");
    }

    public static boolean isLimit(String inCode) {
        return listLimitGroup().stream()
                .anyMatch(item -> item.getCode().equals(inCode));
    }

    public static boolean isOperand(String inCode) {
        return listOperandGroup().stream()
                .anyMatch(item -> item.getCode().equals(inCode));
    }

    public static boolean isOperator(String inCode) {
        return listOperatorGroup().stream()
                .anyMatch(item -> item.getCode().equals(inCode));
    }

    public static boolean isLeft(String inCode) {
        if (LIMIT_LEFT_PARENTHESIS.getCode().equals(inCode)) {
            return Boolean.TRUE;
        }
        if (LIMIT_LEFT_SQUARE_BRACKETS.getCode().equals(inCode)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static boolean isRight(String inCode) {
        if (LIMIT_RIGHT_PARENTHESIS.getCode().equals(inCode)) {
            return Boolean.TRUE;
        }
        if (LIMIT_RIGHT_SQUARE_BRACKETS.getCode().equals(inCode)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static boolean isBeginOrEnd(String inCode) {
        if (LIMIT_BEGIN_END.getCode().equals(inCode)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static boolean isParenthesis(String inCode) {
        if (EvaluateExpressionEnum.LIMIT_LEFT_PARENTHESIS.getCode().equals(inCode)) {
            return Boolean.TRUE;
        }
        if (EvaluateExpressionEnum.LIMIT_RIGHT_PARENTHESIS.getCode().equals(inCode)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static boolean isSquareBrackets(String inCode) {
        if (EvaluateExpressionEnum.LIMIT_LEFT_SQUARE_BRACKETS.getCode().equals(inCode)) {
            return Boolean.TRUE;
        }
        if (EvaluateExpressionEnum.LIMIT_RIGHT_SQUARE_BRACKETS.getCode().equals(inCode)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static boolean isBrother(String aCode, String bCode) {
        if (isParenthesis(aCode) && isParenthesis(bCode) && !aCode.equals(bCode)) {
            return Boolean.TRUE;
        }
        if (isSquareBrackets(aCode) && isSquareBrackets(bCode) && !aCode.equals(bCode)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private static List<EvaluateExpressionEnum> listGroup(String group) {
        if (StrUtil.isBlank(group)) {
            return new ArrayList<>();
        }

        return Arrays.stream(values())
                .filter(item -> item.getGroup().equals(group))
                .toList();
    }
}
