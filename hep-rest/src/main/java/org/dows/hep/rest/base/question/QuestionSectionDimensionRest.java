package org.dows.hep.rest.base.question;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.biz.base.question.QuestionSectionDimensionBiz;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* @description project descr:问题:问题集-维度
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "问题集-维度", description = "问题集-维度")
public class QuestionSectionDimensionRest {
    private final QuestionSectionDimensionBiz questionSectionDimensionBiz;

    /**
    * 获取问题集所有维度
    * @param
    * @return
    */
    @Operation(summary = "获取问题集所有维度-无分页")
    @GetMapping("v1/baseQuestion/questionSectionDimension/listQuestionSectionDimension")
    public List<QuestionSectionDimensionResponse> listQuestionSectionDimension(String questionSectionId) {
        return questionSectionDimensionBiz.listQuestionSectionDimension(questionSectionId);
    }

    /**
    * 删除问题集维度
    * @param
    * @return
    */
    @Operation(summary = "删除or批量删除")
    @DeleteMapping("v1/baseQuestion/questionSectionDimension/delQuestionSectionDimension")
    public Boolean delQuestionSectionDimension(List<String> questionSectionDimensionIds ) {
        return questionSectionDimensionBiz.delQuestionSectionDimension(questionSectionDimensionIds);
    }


}