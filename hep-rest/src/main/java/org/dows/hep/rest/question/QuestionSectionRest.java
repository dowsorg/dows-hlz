package org.dows.hep.rest.question;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.question.request.QuestionSectionRequest;
import org.dows.hep.api.question.request.QuestionSectionSearchRequest;
import org.dows.hep.api.question.response.QuestionSectionResponse;
import org.dows.hep.api.question.request.QuestionSectionSearchRequest;
import org.dows.hep.api.question.response.QuestionSectionResponse;
import org.dows.hep.api.question.response.QuestionSectionResponse;
import org.dows.hep.api.question.request.QuestionnaireGenerateElementsRequest;
import org.dows.hep.api.question.request.QuestionnaireMergeElementsRequest;
import org.dows.hep.api.question.request.QuestionsInSectionRequest;
import org.dows.hep.api.question.response.QuestionSectionResponse;
import org.dows.hep.biz.question.QuestionSectionBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:问题:
*
* @author lait.zhang
* @date 2023年4月14日 下午3:45:06
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "")
public class QuestionSectionRest {
    private final QuestionSectionBiz questionSectionBiz;

    /**
    * 新增和更新问题集[问卷]
    * @param
    * @return
    */
    @ApiOperation("新增和更新问题集[问卷]")
    @PostMapping("v1/question/questionSection/saveOrUpdQuestionSection")
    public String saveOrUpdQuestionSection(@RequestBody @Validated QuestionSectionRequest questionSection ) {
        return questionSectionBiz.saveOrUpdQuestionSection(questionSection);
    }

    /**
    * 分页问题集[问卷]
    * @param
    * @return
    */
    @ApiOperation("分页问题集[问卷]")
    @PostMapping("v1/question/questionSection/pageQuestionSection")
    public QuestionSectionResponse pageQuestionSection(@RequestBody @Validated QuestionSectionSearchRequest questionSectionSearch ) {
        return questionSectionBiz.pageQuestionSection(questionSectionSearch);
    }

    /**
    * 列出问题集[问卷]-无分页
    * @param
    * @return
    */
    @ApiOperation("列出问题集[问卷]-无分页")
    @PostMapping("v1/question/questionSection/listQuestionSection")
    public List<QuestionSectionResponse> listQuestionSection(@RequestBody @Validated QuestionSectionSearchRequest questionSectionSearch ) {
        return questionSectionBiz.listQuestionSection(questionSectionSearch);
    }

    /**
    * 根据ID获取详情
    * @param
    * @return
    */
    @ApiOperation("根据ID获取详情")
    @GetMapping("v1/question/questionSection/getQuestionSection")
    public QuestionSectionResponse getQuestionSection(@Validated String questionSectionId) {
        return questionSectionBiz.getQuestionSection(questionSectionId);
    }

    /**
    * 启用问题集[问卷]
    * @param
    * @return
    */
    @ApiOperation("启用问题集[问卷]")
    @GetMapping("v1/question/questionSection/enabledQuestionSection")
    public Boolean enabledQuestionSection(@Validated String questionSectionId) {
        return questionSectionBiz.enabledQuestionSection(questionSectionId);
    }

    /**
    * 禁用问题集[问卷]
    * @param
    * @return
    */
    @ApiOperation("禁用问题集[问卷]")
    @GetMapping("v1/question/questionSection/disabledQuestionSection")
    public Boolean disabledQuestionSection(@Validated String questionSectionId) {
        return questionSectionBiz.disabledQuestionSection(questionSectionId);
    }

    /**
    * 排序问题集[问卷]
    * @param
    * @return
    */
    @ApiOperation("排序问题集[问卷]")
    @GetMapping("v1/question/questionSection/sortQuestionSection")
    public Boolean sortQuestionSection(@Validated String questionSectionId, @Validated Integer sequence) {
        return questionSectionBiz.sortQuestionSection(questionSectionId,sequence);
    }

    /**
    * 交换问题集[问卷]
    * @param
    * @return
    */
    @ApiOperation("交换问题集[问卷]")
    @GetMapping("v1/question/questionSection/transposeQuestionSection")
    public Boolean transposeQuestionSection(@Validated String leftSectionId, @Validated String rightSectionId) {
        return questionSectionBiz.transposeQuestionSection(leftSectionId,rightSectionId);
    }

    /**
    * 删除or批量删除问题集[问卷]
    * @param
    * @return
    */
    @ApiOperation("删除or批量删除问题集[问卷]")
    @DeleteMapping("v1/question/questionSection/delQuestionSection")
    public Boolean delQuestionSection(@Validated String questionSectionIds ) {
        return questionSectionBiz.delQuestionSection(questionSectionIds);
    }

    /**
    * 复制问题集[问卷]
    * @param
    * @return
    */
    @ApiOperation("复制问题集[问卷]")
    @PostMapping("v1/question/questionSection/copyQuestionSection")
    public String copyQuestionSection(@RequestBody @Validated String oriQuestionSectionId ) {
        return questionSectionBiz.copyQuestionSection(oriQuestionSectionId);
    }

    /**
    * 自动生成问题集[问卷]
    * @param
    * @return
    */
    @ApiOperation("自动生成问题集[问卷]")
    @PostMapping("v1/question/questionSection/generateQuestionSectionAutomatic")
    public String generateQuestionSectionAutomatic(@RequestBody @Validated QuestionnaireGenerateElementsRequest questionnaireGenerateElements ) {
        return questionSectionBiz.generateQuestionSectionAutomatic(questionnaireGenerateElements);
    }

    /**
    * 合并问题集[问卷]
    * @param
    * @return
    */
    @ApiOperation("合并问题集[问卷]")
    @PostMapping("v1/question/questionSection/mergeQuestionSection")
    public String mergeQuestionSection(@RequestBody @Validated QuestionnaireMergeElementsRequest questionnaireMergeElements ) {
        return questionSectionBiz.mergeQuestionSection(questionnaireMergeElements);
    }

    /**
    * 查询
    * @param
    * @return
    */
    @ApiOperation("查询")
    @PostMapping("v1/question/questionSection/listSectionQuestion")
    public QuestionSectionResponse listSectionQuestion(@RequestBody @Validated QuestionsInSectionRequest questionsInSection ) {
        return questionSectionBiz.listSectionQuestion(questionsInSection);
    }

    /**
    * 排序问题集-题目
    * @param
    * @return
    */
    @ApiOperation("排序问题集-题目")
    @GetMapping("v1/question/questionSection/sortSectionQuestion")
    public Boolean sortSectionQuestion(@Validated String questionSectionId, @Validated String questionSectionItemId, @Validated Integer sequence) {
        return questionSectionBiz.sortSectionQuestion(questionSectionId,questionSectionItemId,sequence);
    }

    /**
    * 交换问题集-题目顺序
    * @param
    * @return
    */
    @ApiOperation("交换问题集-题目顺序")
    @GetMapping("v1/question/questionSection/transposeSectionQuestion")
    public Boolean transposeSectionQuestion(@Validated String questionSectionId, @Validated String leftQuestionSectionItemId, @Validated String rightQuestionSectionItemId) {
        return questionSectionBiz.transposeSectionQuestion(questionSectionId,leftQuestionSectionItemId,rightQuestionSectionItemId);
    }

    /**
    * 启用问题集-题目
    * @param
    * @return
    */
    @ApiOperation("启用问题集-题目")
    @GetMapping("v1/question/questionSection/enabledSectionQuestion")
    public Boolean enabledSectionQuestion(@Validated String questionSectionId, @Validated String questionSectionItemId) {
        return questionSectionBiz.enabledSectionQuestion(questionSectionId,questionSectionItemId);
    }

    /**
    * 禁用问题集-题目
    * @param
    * @return
    */
    @ApiOperation("禁用问题集-题目")
    @GetMapping("v1/question/questionSection/disabledSectionQuestion")
    public Boolean disabledSectionQuestion(@Validated String questionSectionId, @Validated String questionSectionItemId) {
        return questionSectionBiz.disabledSectionQuestion(questionSectionId,questionSectionItemId);
    }

    /**
    * 删除or批量删除问题集-题目
    * @param
    * @return
    */
    @ApiOperation("删除or批量删除问题集-题目")
    @DeleteMapping("v1/question/questionSection/delSectionQuestion")
    public Boolean delSectionQuestion(@Validated String questionSectionId, @Validated String questionSectionItemIds ) {
        return questionSectionBiz.delSectionQuestion(questionSectionId,questionSectionItemIds);
    }


}