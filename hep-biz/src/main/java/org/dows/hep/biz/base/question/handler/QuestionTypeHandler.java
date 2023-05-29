package org.dows.hep.biz.base.question.handler;

import org.dows.hep.api.base.question.dto.QuestionRequestDTO;
import org.dows.hep.api.base.question.response.QuestionResponse;

public interface QuestionTypeHandler {
    void init();

    String save(QuestionRequestDTO questionRequest);

    boolean update(QuestionRequestDTO questionRequest);

    QuestionResponse get(String questionInstanceId);
}
