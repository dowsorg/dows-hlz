package org.dows.hep.biz.base.question.handler;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionCloneEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.springframework.stereotype.Component;

/**
 * @author fhb
 * @description 判断类题型
 * @date 2023/4/23 15:10
 */
@Component
@RequiredArgsConstructor
public class JudgmentQuestionTypeHandler implements QuestionTypeHandler {

    @Override
    public String save(QuestionRequest questionRequest) {
        return null;
    }

    @Override
    public Boolean update(QuestionRequest questionRequest) {
        return null;
    }

    @Override
    public String clone(QuestionRequest questionRequest, QuestionCloneEnum questionCloneEnum) {
        return null;
    }


}
