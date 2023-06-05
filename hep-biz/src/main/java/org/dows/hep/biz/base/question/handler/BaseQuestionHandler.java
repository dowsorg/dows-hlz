package org.dows.hep.biz.base.question.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.dto.QuestionResultRecordDTO;
import org.dows.hep.api.base.question.enums.QuestionESCEnum;
import org.dows.hep.api.base.question.request.QuestionDimensionRequest;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.response.QuestionDimensionResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.biz.base.question.QuestionDimensionBiz;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BaseQuestionHandler {
    private final QuestionDimensionBiz questionDimensionBiz;

    /**
     * @author fhb
     * @description 新增或更新问题维度
     * @date 2023/6/1 16:48
     * @param
     * @return
     */
    public Boolean saveOrUpdQuestionDimension(QuestionRequest question, String questionInstanceId) {
        List<String> dimensionIds = new ArrayList<>();
        List<String> ids = question.getDimensionIds();
        if (CollUtil.isNotEmpty(ids)) {
            dimensionIds.addAll(ids);
        } else {
            String dimensionId = question.getDimensionId();
            if (StrUtil.isNotBlank(dimensionId)) {
                dimensionIds.add(dimensionId);
            }
        }
        if (CollUtil.isNotEmpty(dimensionIds)) {
            QuestionDimensionRequest dimensionRequest = builderQuestionDimensionRequest(questionInstanceId, dimensionIds);
            questionDimensionBiz.relateQuestionDimension(dimensionRequest);
        }
        return Boolean.TRUE;
    }

    /**
     * @author fhb
     * @description 给 question-response 设置问题维度
     * @date 2023/6/1 16:49
     * @param
     * @return
     */
    public void setDimensionId(QuestionResponse questionResponse) {
        String questionInstanceId = questionResponse.getQuestionInstanceId();
        QuestionDimensionResponse questionDimensionResponse = questionDimensionBiz.listQuestionDimension(questionInstanceId);
        if (BeanUtil.isEmpty(questionDimensionResponse)) {
            return;
        }

        List<String> idList = questionDimensionResponse.getQuestionSectionDimensionIds();
        if (CollUtil.isEmpty(idList)) {
            return;
        }

        if (idList.size() > 1) {
            questionResponse.setDimensionIds(idList);
        } else {
            questionResponse.setDimensionId(idList.get(0));
        }
    }

    public void setQuestionResult(QuestionResponse questionResponse, QuestionResultRecordDTO recordDTO) {
        if (BeanUtil.isEmpty(questionResponse) || BeanUtil.isEmpty(recordDTO)) {
            return;
        }

        Map<String, String> questionResultMap = recordDTO.getQuestionResultMap();
        String questionInstanceId = questionResponse.getQuestionInstanceId();
        String questionResult = questionResultMap.get(questionInstanceId);
        questionResponse.setQuestionResult(questionResult);
    }

    private QuestionDimensionRequest builderQuestionDimensionRequest(String questionInstanceId, List<String> dimensionIds) {
        if (StrUtil.isBlank(questionInstanceId) || CollUtil.isEmpty(dimensionIds)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        QuestionDimensionRequest result = new QuestionDimensionRequest();
        result.setQuestionInstanceId(questionInstanceId);
        result.setQuestionSectionDimensionIds(dimensionIds);

        return result;
    }
}
