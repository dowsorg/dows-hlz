package org.dows.hep.rest.question;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.question.request.QuestionSectionDimensionRequest;
import org.dows.hep.api.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.biz.question.QuestionSectionDimensionBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:问题:问题集-维度
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "问题集-维度")
public class QuestionSectionDimensionRest {
    private final QuestionSectionDimensionBiz questionSectionDimensionBiz;

    /**
    * 新增和更新问题集维度
    * @param
    * @return
    */
    @ApiOperation("新增和更新问题集维度")
    @PostMapping("v1/question/questionSectionDimension/saveOrUpdQuestionSectionDimension")
    public Boolean saveOrUpdQuestionSectionDimension(@RequestBody @Validated QuestionSectionDimensionRequest questionSectionDimension ) {
        return questionSectionDimensionBiz.saveOrUpdQuestionSectionDimension(questionSectionDimension);
    }

    /**
    * 获取问题集所有维度
    * @param
    * @return
    */
    @ApiOperation("获取问题集所有维度")
    @GetMapping("v1/question/questionSectionDimension/listQuestionSectionDimension")
    public List<QuestionSectionDimensionResponse> listQuestionSectionDimension(@Validated String questionSectionId) {
        return questionSectionDimensionBiz.listQuestionSectionDimension(questionSectionId);
    }

    /**
    * 删除问题集维度
    * @param
    * @return
    */
    @ApiOperation("删除问题集维度")
    @DeleteMapping("v1/question/questionSectionDimension/delQuestionSectionDimension")
    public Boolean delQuestionSectionDimension(@Validated String questionSectionDimensionIds ) {
        return questionSectionDimensionBiz.delQuestionSectionDimension(questionSectionDimensionIds);
    }


}