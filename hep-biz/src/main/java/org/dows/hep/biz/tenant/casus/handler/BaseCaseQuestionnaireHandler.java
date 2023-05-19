package org.dows.hep.biz.tenant.casus.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import org.dows.hep.api.base.question.QuestionEnabledEnum;
import org.dows.hep.api.base.question.QuestionSectionGenerationModeEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSectionItemRequest;
import org.dows.hep.api.base.question.request.QuestionSectionRequest;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireRequest;
import org.dows.hep.biz.base.question.QuestionInstanceBiz;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseBaseBiz;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCaseQuestionnaireHandler implements ApplicationContextAware, CaseQuestionnaireHandler {
    private TenantCaseBaseBiz baseBiz;
    private QuestionSectionBiz questionSectionBiz;
    private QuestionInstanceBiz questionInstanceBiz;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        baseBiz = applicationContext.getBean(TenantCaseBaseBiz.class);
        questionSectionBiz = applicationContext.getBean(QuestionSectionBiz.class);
    }

    @Override
    public String handle(CaseQuestionnaireRequest caseQuestionnaire) {
        QuestionSectionRequest questionSectionRequest = buildSectionRequest(caseQuestionnaire);
        return questionSectionBiz.saveOrUpdQuestionSection(questionSectionRequest);
    }

    public abstract void init();

    public abstract List<String> getQuestionIds(CaseQuestionnaireRequest caseQuestionnaire);

    public abstract boolean needOriRequest();

    private QuestionSectionRequest buildSectionRequest(CaseQuestionnaireRequest caseQuestionnaire) {
        // build current
        QuestionSectionRequest questionSectionRequest2 = buildCurrentRequest(caseQuestionnaire);

        // build ori
        if (needOriRequest()) {
            QuestionSectionRequest questionSectionRequest1 = buildOriRequest(caseQuestionnaire);
            merge(questionSectionRequest2, questionSectionRequest1);
        }

        return questionSectionRequest2;
    }

    private QuestionSectionRequest buildCurrentRequest(CaseQuestionnaireRequest caseQuestionnaire) {
        List<String> questionIds = getQuestionIds(caseQuestionnaire);
        return generateQuestionSectionRequest(caseQuestionnaire, questionIds);
    }

    private QuestionSectionRequest buildOriRequest(CaseQuestionnaireRequest caseQuestionnaire) {
        String questionSectionId = caseQuestionnaire.getQuestionSectionId();
        if (StrUtil.isBlank(questionSectionId)) {
            return new QuestionSectionRequest();
        }

        QuestionSectionResponse questionSection = questionSectionBiz.getQuestionSection(questionSectionId);
        return BeanUtil.copyProperties(questionSection, QuestionSectionRequest.class);
    }

    private void merge(QuestionSectionRequest result, QuestionSectionRequest questionSectionRequest) {
        List<QuestionSectionItemRequest> sectionItemList = result.getSectionItemList();
        List<QuestionSectionItemRequest> sectionItemList1 = questionSectionRequest.getSectionItemList();
        sectionItemList.addAll(sectionItemList1);
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
                    .enabled(QuestionEnabledEnum.ENABLED.getCode())
                    .required(0)
                    .sequence(i)
                    .questionRequest(questionRequest)
                    .build();
            questionSectionItemRequests.add(questionSectionItemRequest);
        }
        return QuestionSectionRequest.builder()
                .appId(baseBiz.getAppId())
                .name(caseQuestionnaire.getQuestionSectionName())
                .enabled(QuestionEnabledEnum.ENABLED.getCode())
                .generationMode(QuestionSectionGenerationModeEnum.SELECT)
                .sectionItemList(questionSectionItemRequests)
                .build();
    }
}
