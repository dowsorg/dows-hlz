package org.dows.hep.rest.base.question;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.request.QuestionSectionResultRequest;
import org.dows.hep.api.base.question.request.QuestionSectionResultSearchRequest;
import org.dows.hep.api.base.question.response.QuestionSectionResultResponse;
import org.dows.hep.biz.base.question.QuestionSectionResultBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:问题:问题集-答题记录
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "问题集[问卷]-答题记录", description = "问题集-答题记录")
public class QuestionSectionResultRest {
    private final QuestionSectionResultBiz questionSectionResultBiz;

    /**
    * 新增和更新问题集-答题
    * @param
    * @return
    */
    @Operation(summary = "新增和更新问题集-答题")
    @PostMapping("v1/baseQuestion/questionSectionResult/saveOrUpdQuestionSectionResult")
    public Boolean saveOrUpdQuestionSectionResult(@RequestBody @Validated QuestionSectionResultRequest questionSectionResult ) {
        return questionSectionResultBiz.saveOrUpdQuestionSectionResult(questionSectionResult);
    }

    /**
    * 分页查询问题集-答题记录
    * @param
    * @return
    */
    @Operation(summary = "分页查询问题集-答题记录")
    @PostMapping("v1/baseQuestion/questionSectionResult/pageQuestionSectionResult")
    public QuestionSectionResultResponse pageQuestionSectionResult(@RequestBody @Validated QuestionSectionResultSearchRequest questionSectionResultSearch ) {
        return questionSectionResultBiz.pageQuestionSectionResult(questionSectionResultSearch);
    }

    /**
    * 获取问题集-答题记录
    * @param
    * @return
    */
    @Operation(summary = "获取问题集-答题记录")
    @GetMapping("v1/baseQuestion/questionSectionResult/getQuestionSectionResult")
    public QuestionSectionResultResponse getQuestionSectionResult(@Validated String questionSectionResultId) {
        return questionSectionResultBiz.getQuestionSectionResult(questionSectionResultId);
    }


}