package org.dows.hep.biz.tenant.casus.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.tenant.casus.QuestionSelectModeEnum;
import org.dows.hep.api.tenant.casus.request.CaseQuestionSearchRequest;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireRequest;
import org.dows.hep.biz.base.question.QuestionCategBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseBaseBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseQuestionnaireBiz;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RandomCaseQuestionnaireHandler extends BaseCaseQuestionnaireHandler implements CaseQuestionnaireHandler {
    private final TenantCaseBaseBiz baseBiz;
    private final TenantCaseQuestionnaireBiz caseQuestionnaireBiz;
    private final QuestionCategBiz categBiz;

    @PostConstruct
    @Override
    public void init() {
        CaseQuestionnaireFactory.register(QuestionSelectModeEnum.RANDOM, this);
    }

    @Override
    public List<String> getQuestionIds(CaseQuestionnaireRequest caseQuestionnaire) {
        List<CaseQuestionnaireRequest.RandomMode> randomModeList = caseQuestionnaire.getRandomModeList();
        if (randomModeList == null || randomModeList.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据类目获取相应的问题
        List<String> questionIds = new ArrayList<>();
        randomModeList.forEach(item -> {
            // 该类目下，不同题型所需要的数量
            Map<QuestionTypeEnum, Integer> numMap = item.getNumMap();
            // 该类目下，不同题型的问题题目集合
            Map<String, List<QuestionResponse>> questionCollect = collectQuestionOfUsableQuestion(item, caseQuestionnaire.getCaseInstanceId());
            // 从问题题目集合中选出合适数量分配到 questionIds 中
            if (questionCollect != null && !questionCollect.isEmpty()) {
                questionCollect.forEach((questionType, questionList) -> {
                    QuestionTypeEnum questionTypeEnum = QuestionTypeEnum.getByCode(questionType);
                    if (numMap.get(questionTypeEnum) != null) {
                        Integer size = numMap.get(questionTypeEnum);
                        List<String> list = questionList.stream()
                                .map(QuestionResponse::getQuestionInstanceId)
                                .limit(size)
                                .toList();
                        questionIds.addAll(list);
                    }
                });
            }
        });
        return questionIds;
    }

    @Override
    public boolean needOriRequest() {
        return Boolean.FALSE;
    }

    private Map<String, List<QuestionResponse>> collectQuestionOfUsableQuestion(CaseQuestionnaireRequest.RandomMode randomMode, String caseInstanceId) {
        if (BeanUtil.isEmpty(randomMode) || StrUtil.isBlank(caseInstanceId)) {
            return new HashMap<>();
        }

        // list question by category
        CaseQuestionSearchRequest request = CaseQuestionSearchRequest.builder()
                .caseInstanceId(caseInstanceId)
                .l2CategId(randomMode.getL2CategId())
                .l1CategId(randomMode.getL1CategId()).build();
        return caseQuestionnaireBiz.collectQuestionOfUsableQuestion(request);
    }
}
