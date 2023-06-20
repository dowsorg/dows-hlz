package org.dows.hep.biz.base.question.handler;

import org.dows.hep.api.base.question.QuestionTypeEnum;

import java.util.HashMap;
import java.util.Map;

public class QuestionTypeFactory {
    private static final Map<QuestionTypeEnum, QuestionTypeHandler> HANDLER_REGISTERS = new HashMap<>();

    public static void register(QuestionTypeEnum questionTypeEnum, QuestionTypeHandler questionTypeHandler) {
        if (null != questionTypeEnum) {
            HANDLER_REGISTERS.put(questionTypeEnum, questionTypeHandler);
        }
    }

    public static QuestionTypeHandler get(QuestionTypeEnum questionTypeEnum) {
        return HANDLER_REGISTERS.get(questionTypeEnum);
    }
}
