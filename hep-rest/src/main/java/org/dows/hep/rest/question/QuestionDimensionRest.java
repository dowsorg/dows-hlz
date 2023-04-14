package org.dows.hep.rest.question;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.question.request.QuestionDimensionRequest;
import org.dows.hep.api.question.response.QuestionDimensionResponse;
import org.dows.hep.biz.question.QuestionDimensionBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:问题:问题-维度
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "问题-维度")
public class QuestionDimensionRest {
    private final QuestionDimensionBiz questionDimensionBiz;

    /**
    * 关联问题维度
    * @param
    * @return
    */
    @ApiOperation("关联问题维度")
    @PostMapping("v1/question/questionDimension/relateQuestionDimension")
    public Boolean relateQuestionDimension(@RequestBody @Validated QuestionDimensionRequest questionDimension ) {
        return questionDimensionBiz.relateQuestionDimension(questionDimension);
    }

    /**
    * 获取问题下所有维度
    * @param
    * @return
    */
    @ApiOperation("获取问题下所有维度")
    @GetMapping("v1/question/questionDimension/listQuestionDimension")
    public List<QuestionDimensionResponse> listQuestionDimension(@Validated String questionInstanceId) {
        return questionDimensionBiz.listQuestionDimension(questionInstanceId);
    }


}