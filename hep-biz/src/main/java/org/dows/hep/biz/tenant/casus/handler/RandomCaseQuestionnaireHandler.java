package org.dows.hep.biz.tenant.casus.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionSectionGenerationModeEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSearchRequest;
import org.dows.hep.api.base.question.request.QuestionSectionItemRequest;
import org.dows.hep.api.base.question.request.QuestionSectionRequest;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.tenant.casus.QuestionSelectModeEnum;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireRequest;
import org.dows.hep.biz.base.question.QuestionInstanceBiz;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseBaseBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseQuestionnaireBiz;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RandomCaseQuestionnaireHandler implements CaseQuestionnaireHandler {
    private final TenantCaseBaseBiz baseBiz;
    private final TenantCaseQuestionnaireBiz caseQuestionnaireBiz;
    private final QuestionInstanceBiz questionInstanceBiz;
    private final QuestionSectionBiz questionSectionBiz;

    @PostConstruct
    @Override
    public void init() {
        CaseQuestionnaireFactory.register(QuestionSelectModeEnum.RANDOM, this);
    }

    @Override
    public String handle(CaseQuestionnaireRequest caseQuestionnaire) {
        List<CaseQuestionnaireRequest.RandomMode> randomModeList = caseQuestionnaire.getRandomModeList();
        if (randomModeList == null || randomModeList.isEmpty()) {
            return "";
        }

        // 根据类目获取相应的问题
        List<String> questionIds = new ArrayList<>();
        randomModeList.forEach(item -> {
            // 该类目下，不同题型所需要的数量
            Map<String, Integer> numMap = item.getNumMap();
            // 该类目下，不同题型的问题题目集合
            Map<String, List<QuestionResponse>> questionCollect = collectQuestionOfUsableQuestion(item, caseQuestionnaire.getCaseInstanceId());
            // 从问题题目集合中选出合适数量分配到 questionIds 中
            if (questionCollect != null && !questionCollect.isEmpty()) {
                questionCollect.forEach((questionType, questionList) -> {
                    if (numMap.get(questionType) != null) {
                        Integer size = numMap.get(questionType);
                        List<String> list = questionList.stream()
                                .map(QuestionResponse::getQuestionInstanceId)
                                .limit(size)
                                .toList();
                        questionIds.addAll(list);
                    }
                });
            }
        });

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

    private Map<String, List<QuestionResponse>> collectQuestionOfUsableQuestion(CaseQuestionnaireRequest.RandomMode randomMode, String caseInstanceId) {
        if (randomMode == null || randomMode.getQuestionCategIdPaths() == null) {
            return new HashMap<>();
        }

        // list question by category
        QuestionSearchRequest request = new QuestionSearchRequest();
        request.setAppId(baseBiz.getAppId());
        request.setCategIdList(randomMode.getQuestionCategIdPaths());
        return caseQuestionnaireBiz.collectQuestionOfUsableQuestion(request, caseInstanceId);
    }
}
