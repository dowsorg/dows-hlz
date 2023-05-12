package org.dows.hep.biz.tenant.casus.handler;

import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireRequest;

public interface CaseQuestionnaireHandler {
    void init();

    String handle(CaseQuestionnaireRequest caseQuestionnaire);
}
