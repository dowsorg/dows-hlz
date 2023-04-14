package org.dows.hep.rest.base.question;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.question.request.QuestionSectionDimensionRequest;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.biz.base.question.QuestionSectionDimensionBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:问题:问题集-维度
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "问题集-维度", description = "问题集-维度")
public class QuestionSectionDimensionRest {
    private final QuestionSectionDimensionBiz questionSectionDimensionBiz;

    /**
    * 新增和更新问题集维度
    * @param
    * @return
    */
    @Operation(summary = "新增和更新问题集维度")
    @PostMapping("v1/baseQuestion/questionSectionDimension/saveOrUpdQuestionSectionDimension")
    public Boolean saveOrUpdQuestionSectionDimension(@RequestBody @Validated QuestionSectionDimensionRequest questionSectionDimension ) {
        return questionSectionDimensionBiz.saveOrUpdQuestionSectionDimension(questionSectionDimension);
    }

    /**
    * 获取问题集所有维度
    * @param
    * @return
    */
    @Operation(summary = "获取问题集所有维度")
    @GetMapping("v1/baseQuestion/questionSectionDimension/listQuestionSectionDimension")
    public List<QuestionSectionDimensionResponse> listQuestionSectionDimension(@Validated String questionSectionId) {
        return questionSectionDimensionBiz.listQuestionSectionDimension(questionSectionId);
    }

    /**
    * 删除问题集维度
    * @param
    * @return
    */
    @Operation(summary = "删除问题集维度")
    @DeleteMapping("v1/baseQuestion/questionSectionDimension/delQuestionSectionDimension")
    public Boolean delQuestionSectionDimension(@Validated String questionSectionDimensionIds ) {
        return questionSectionDimensionBiz.delQuestionSectionDimension(questionSectionDimensionIds);
    }


}