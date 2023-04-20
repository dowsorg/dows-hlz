package org.dows.hep.rest.base.question;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.request.QuestionDimensionRequest;
import org.dows.hep.api.base.question.response.QuestionDimensionResponse;
import org.dows.hep.biz.base.question.QuestionDimensionBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* @description project descr:问题:问题-维度
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "问题-维度", description = "问题-维度")
public class QuestionDimensionRest {
    private final QuestionDimensionBiz questionDimensionBiz;

    /**
    * 关联问题维度
    * @param
    * @return
    */
    @Operation(summary = "关联问题维度")
    @PostMapping("v1/baseQuestion/questionDimension/relateQuestionDimension")
    public Boolean relateQuestionDimension(@RequestBody @Validated QuestionDimensionRequest questionDimension ) {
        return questionDimensionBiz.relateQuestionDimension(questionDimension);
    }

    /**
    * 获取问题下所有维度
    * @param
    * @return
    */
    @Operation(summary = "获取问题下所有维度")
    @GetMapping("v1/baseQuestion/questionDimension/listQuestionDimension")
    public List<QuestionDimensionResponse> listQuestionDimension(@Validated String questionInstanceId) {
        return questionDimensionBiz.listQuestionDimension(questionInstanceId);
    }


}