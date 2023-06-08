package org.dows.hep.biz.tenant.casus.handler;

import org.dows.hep.api.tenant.casus.CaseQuestionSelectModeEnum;

import java.util.HashMap;
import java.util.Map;

public class CaseQuestionnaireFactory {
    private static final Map<CaseQuestionSelectModeEnum, CaseQuestionnaireHandler> HANDLER_REGISTERS = new HashMap<>();

    public static void register(CaseQuestionSelectModeEnum caseQuestionSelectModeEnum, CaseQuestionnaireHandler caseQuestionnaireHandler) {
        if (null != caseQuestionSelectModeEnum) {
            HANDLER_REGISTERS.put(caseQuestionSelectModeEnum, caseQuestionnaireHandler);
        }
    }

    public static CaseQuestionnaireHandler get(CaseQuestionSelectModeEnum caseQuestionSelectModeEnum) {
        return HANDLER_REGISTERS.get(caseQuestionSelectModeEnum);
    }
}
