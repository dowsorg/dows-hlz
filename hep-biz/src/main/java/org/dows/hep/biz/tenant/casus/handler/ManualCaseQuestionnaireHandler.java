package org.dows.hep.biz.tenant.casus.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionSectionGenerationModeEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSectionItemRequest;
import org.dows.hep.api.base.question.request.QuestionSectionRequest;
import org.dows.hep.api.tenant.casus.QuestionSelectModeEnum;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireRequest;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseBaseBiz;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ManualCaseQuestionnaireHandler implements CaseQuestionnaireHandler{
    private final TenantCaseBaseBiz baseBiz;
    private final QuestionSectionBiz questionSectionBiz;
    @PostConstruct
    @Override
    public void init() {
        CaseQuestionnaireFactory.register(QuestionSelectModeEnum.MANUAL, this);
    }

    @Override
    public String handle(CaseQuestionnaireRequest caseQuestionnaire) {
        List<String> questionIds = caseQuestionnaire.getQuestionInstanceIdList();
        QuestionSectionRequest questionSectionRequest = generateQuestionSectionRequest(caseQuestionnaire, questionIds);
        return questionSectionBiz.saveOrUpdQuestionSection(questionSectionRequest);
    }

    private QuestionSectionRequest generateQuestionSectionRequest(CaseQuestionnaireRequest caseQuestionnaire, List<String> questionIds) {
        List<QuestionSectionItemRequest> questionSectionItemRequests = new ArrayList<>();
        for (int i = 0; i < questionIds.size(); i++) {
            QuestionRequest questionRequest = QuestionRequest.builder()
                    .appId(baseBiz.getAppId())
                    .questionInstanceId(questionIds.get(i))
                    .build();
            QuestionSectionItemRequest questionSectionItemRequest = QuestionSectionItemRequest.builder()
                    .appId(baseBiz.getAppId())
                    .questionSectionItemId(baseBiz.getIdStr())
                    .enabled(1)
                    .required(0)
                    .sequence(i)
                    .questionRequest(questionRequest)
                    .build();
            questionSectionItemRequests.add(questionSectionItemRequest);
        }
        return QuestionSectionRequest.builder()
                .appId(baseBiz.getAppId())
                .questionSectionId(baseBiz.getIdStr())
                .name(caseQuestionnaire.getQuestionSectionName())
                .enabled(1)
                .accountId(caseQuestionnaire.getAccountId())
                .accountName(caseQuestionnaire.getAccountName())
                .generationMode(QuestionSectionGenerationModeEnum.SELECT)
                .sectionItemList(questionSectionItemRequests)
                .build();
    }


}
