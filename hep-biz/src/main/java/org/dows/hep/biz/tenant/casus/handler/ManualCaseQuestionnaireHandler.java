package org.dows.hep.biz.tenant.casus.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.CaseQuestionSelectModeEnum;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ManualCaseQuestionnaireHandler extends BaseCaseQuestionnaireHandler implements CaseQuestionnaireHandler{

    @PostConstruct
    @Override
    public void init() {
        CaseQuestionnaireFactory.register(CaseQuestionSelectModeEnum.MANUAL, this);
    }

    @Override
    public List<String> getQuestionIds(CaseQuestionnaireRequest caseQuestionnaire) {
        return caseQuestionnaire.getQuestionInstanceIdList();
    }
}
