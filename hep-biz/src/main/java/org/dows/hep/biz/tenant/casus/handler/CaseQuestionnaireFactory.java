package org.dows.hep.biz.tenant.casus.handler;

import org.dows.hep.api.tenant.casus.QuestionSelectModeEnum;

import java.util.HashMap;
import java.util.Map;

public class CaseQuestionnaireFactory {
    private static final Map<QuestionSelectModeEnum, CaseQuestionnaireHandler> HANDLER_REGISTERS = new HashMap<>();

    public static void register(QuestionSelectModeEnum questionSelectModeEnum, CaseQuestionnaireHandler caseQuestionnaireHandler) {
        if (null != questionSelectModeEnum) {
            HANDLER_REGISTERS.put(questionSelectModeEnum, caseQuestionnaireHandler);
        }
    }

    public static CaseQuestionnaireHandler get(QuestionSelectModeEnum questionSelectModeEnum) {
        return HANDLER_REGISTERS.get(questionSelectModeEnum);
    }
}
