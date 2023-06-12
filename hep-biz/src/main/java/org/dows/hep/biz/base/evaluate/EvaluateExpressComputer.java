package org.dows.hep.biz.base.evaluate;

import java.util.Stack;

public class EvaluateExpressComputer {
    public static void main(String[] args) {
        String expression = "3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3";
        double result = evaluateExpression(expression);
        System.out.println("Result: " + result);
    }

    public static double evaluateExpression(String expression) {
        // 操作数栈
        Stack<Double> operandStack = new Stack<>();
        // 运算符栈
        Stack<Character> operatorStack = new Stack<>();

        expression = expression.replaceAll("\\s+", ""); // 去除表达式中的空格

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch)) {
                StringBuilder operand = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    operand.append(expression.charAt(i));
                    i++;
                }
                i--;
                operandStack.push(Double.parseDouble(operand.toString()));
            } else if (ch == '(') {
                operatorStack.push(ch);
            } else if (ch == ')') {
                while (operatorStack.peek() != '(') {
                    double result = applyOperator(operatorStack.pop(), operandStack.pop(), operandStack.pop());
                    operandStack.push(result);
                }
                operatorStack.pop(); // 弹出左括号
            } else if (isOperator(ch)) {
                while (!operatorStack.isEmpty() && operatorPrecedence(operatorStack.peek()) >= operatorPrecedence(ch)) {
                    double result = applyOperator(operatorStack.pop(), operandStack.pop(), operandStack.pop());
                    operandStack.push(result);
                }
                operatorStack.push(ch);
            }
        }

        while (!operatorStack.isEmpty()) {
            double result = applyOperator(operatorStack.pop(), operandStack.pop(), operandStack.pop());
            operandStack.push(result);
        }

        return operandStack.pop();
    }

    public static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' || ch == '^';
    }

    public static int operatorPrecedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/', '%' -> 2;
            case '^' -> 3;
            default -> 0;
        };
    }

    public static double applyOperator(char operator, double operand2, double operand1) {
        return switch (operator) {
            case '+' -> operand1 + operand2;
            case '-' -> operand1 - operand2;
            case '*' -> operand1 * operand2;
            case '/' -> operand1 / operand2;
            case '%' -> operand1 % operand2;
            case '^' -> Math.pow(operand1, operand2);
            default -> 0;
        };
    }
}
