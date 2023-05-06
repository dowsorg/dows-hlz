package org.dows.hep.biz.base.question.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.springframework.stereotype.Component;

/**
 * @author fhb
 * @description 判断类题型
 * @date 2023/4/23 15:10
 */
@Component
@RequiredArgsConstructor
public class JudgmentQuestionTypeHandler implements QuestionTypeHandler {

    @PostConstruct
    @Override
    public void init() {
        QuestionTypeFactory.register(QuestionTypeEnum.JUDGMENT, this);
    }

    @Override
    public String save(QuestionRequest questionRequest) {
        return null;
    }

    @Override
    public boolean update(QuestionRequest questionRequest) {
        return Boolean.FALSE;
    }

    @Override
    public QuestionResponse get(String questionInstanceId) {
        return null;
    }


}
