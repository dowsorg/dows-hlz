package org.dows.hep.biz.base.question.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.dto.QuestionRequestDTO;
import org.dows.hep.api.base.question.dto.QuestionResultRecordDTO;
import org.dows.hep.api.base.question.enums.QuestionTypeEnum;
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
    public String save(QuestionRequestDTO request) {
        return null;
    }

    @Override
    public boolean update(QuestionRequestDTO request) {
        return Boolean.FALSE;
    }

    @Override
    public QuestionResponse get(String questionInstanceId, QuestionResultRecordDTO questionResultRecordDTO) {
        return null;
    }


}
