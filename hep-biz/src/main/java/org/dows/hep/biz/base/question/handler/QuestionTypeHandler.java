package org.dows.hep.biz.base.question.handler;

import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.response.QuestionResponse;

public interface QuestionTypeHandler {
    void init();

    String save(QuestionRequest questionRequest);

    boolean update(QuestionRequest questionRequest);

    QuestionResponse get(String questionInstanceId);
}
