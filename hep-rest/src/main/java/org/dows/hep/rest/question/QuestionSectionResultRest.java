package org.dows.hep.rest.question;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.question.request.QuestionSectionResultRequest;
import org.dows.hep.api.question.request.QuestionSectionResultSearchRequest;
import org.dows.hep.api.question.response.QuestionSectionResultResponse;
import org.dows.hep.api.question.response.QuestionSectionResultResponse;
import org.dows.hep.biz.question.QuestionSectionResultBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:问题:问题集-答题记录
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "问题集-答题记录")
public class QuestionSectionResultRest {
    private final QuestionSectionResultBiz questionSectionResultBiz;

    /**
    * 新增和更新问题集-答题
    * @param
    * @return
    */
    @ApiOperation("新增和更新问题集-答题")
    @PostMapping("v1/question/questionSectionResult/saveOrUpdQuestionSectionResult")
    public Boolean saveOrUpdQuestionSectionResult(@RequestBody @Validated QuestionSectionResultRequest questionSectionResult ) {
        return questionSectionResultBiz.saveOrUpdQuestionSectionResult(questionSectionResult);
    }

    /**
    * 分页查询问题集-答题记录
    * @param
    * @return
    */
    @ApiOperation("分页查询问题集-答题记录")
    @PostMapping("v1/question/questionSectionResult/pageQuestionSectionResult")
    public QuestionSectionResultResponse pageQuestionSectionResult(@RequestBody @Validated QuestionSectionResultSearchRequest questionSectionResultSearch ) {
        return questionSectionResultBiz.pageQuestionSectionResult(questionSectionResultSearch);
    }

    /**
    * 获取问题集-答题记录
    * @param
    * @return
    */
    @ApiOperation("获取问题集-答题记录")
    @GetMapping("v1/question/questionSectionResult/getQuestionSectionResult")
    public QuestionSectionResultResponse getQuestionSectionResult(@Validated String questionSectionResultId) {
        return questionSectionResultBiz.getQuestionSectionResult(questionSectionResultId);
    }


}