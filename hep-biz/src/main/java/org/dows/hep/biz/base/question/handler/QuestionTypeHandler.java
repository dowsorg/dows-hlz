package org.dows.hep.biz.base.question.handler;

import org.dows.hep.api.base.question.QuestionCloneEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;

public interface QuestionTypeHandler {
    void init();

    String save(QuestionRequest questionRequest);

    boolean update(QuestionRequest questionRequest);

    // 根据克隆类型进行不同形式的克隆
    String clone(QuestionRequest questionRequest, QuestionCloneEnum questionCloneEnum);
}
