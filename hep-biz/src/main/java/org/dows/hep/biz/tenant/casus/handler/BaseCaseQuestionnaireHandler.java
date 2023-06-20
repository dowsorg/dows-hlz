package org.dows.hep.biz.tenant.casus.handler;

import cn.hutool.core.bean.BeanUtil;
import org.dows.hep.api.base.question.QuestionEnabledEnum;
import org.dows.hep.api.base.question.QuestionSectionAccessAuthEnum;
import org.dows.hep.api.base.question.QuestionSectionGenerationModeEnum;
import org.dows.hep.api.base.question.QuestionSourceEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSectionItemRequest;
import org.dows.hep.api.base.question.request.QuestionSectionRequest;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireRequest;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnaireResponse;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseQuestionnaireBiz;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCaseQuestionnaireHandler implements ApplicationContextAware, CaseQuestionnaireHandler {
    private QuestionSectionBiz questionSectionBiz;
    private TenantCaseQuestionnaireBiz caseQuestionnaireBiz;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        questionSectionBiz = applicationContext.getBean(QuestionSectionBiz.class);
        caseQuestionnaireBiz = applicationContext.getBean(TenantCaseQuestionnaireBiz.class);
    }

    @Override
    public String handle(CaseQuestionnaireRequest caseQuestionnaire) {
        QuestionSectionRequest questionSectionRequest = buildQuestionSectionRequest(caseQuestionnaire);
        return questionSectionBiz.saveOrUpdQuestionSection(questionSectionRequest, QuestionSectionAccessAuthEnum.PRIVATE_VIEWING, QuestionSourceEnum.TENANT);
    }

    public abstract void init();

    public abstract List<String> getQuestionIds(CaseQuestionnaireRequest caseQuestionnaire);

    private QuestionSectionRequest buildQuestionSectionRequest(CaseQuestionnaireRequest caseQuestionnaire) {
        List<String> questionIds = getQuestionIds(caseQuestionnaire);
        return generateQuestionSectionRequest(caseQuestionnaire, questionIds);
    }

    private QuestionSectionRequest generateQuestionSectionRequest(CaseQuestionnaireRequest caseQuestionnaire, List<String> questionIds) {
        List<QuestionSectionItemRequest> questionSectionItemRequests = new ArrayList<>();
        for (int i = 0; i < questionIds.size(); i++) {
            QuestionRequest questionRequest = QuestionRequest.builder()
                    .questionInstanceId(questionIds.get(i))
                    .build();
            QuestionSectionItemRequest questionSectionItemRequest = QuestionSectionItemRequest.builder()
                    .enabled(QuestionEnabledEnum.ENABLED.getCode())
                    .required(0)
                    .sequence(i)
                    .question(questionRequest)
                    .build();
            questionSectionItemRequests.add(questionSectionItemRequest);
        }

        String questionSectionId = "";
        CaseQuestionnaireResponse caseQuestionnaire0 = caseQuestionnaireBiz.getCaseQuestionnaire(caseQuestionnaire.getCaseQuestionnaireId());
        if (!BeanUtil.isEmpty(caseQuestionnaire0)) {
            questionSectionId = caseQuestionnaire0.getQuestionSectionId();
        }

        return QuestionSectionRequest.builder()
                .questionSectionId(questionSectionId)
                .name(caseQuestionnaire.getQuestionSectionName())
                .enabled(QuestionEnabledEnum.ENABLED.getCode())
                .generationMode(QuestionSectionGenerationModeEnum.SELECT_REF)
                .sectionItemList(questionSectionItemRequests)
                .build();
    }
}
