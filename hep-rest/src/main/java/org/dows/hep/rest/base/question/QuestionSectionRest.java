package org.dows.hep.rest.base.question;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.request.*;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:问题:问题集[问卷]
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "问题集[问卷]", description = "问题集[问卷]")
public class QuestionSectionRest {
    private final QuestionSectionBiz questionSectionBiz;

    /**
    * 新增和更新问题集[问卷]
    * @param
    * @return
    */
    @Operation(summary = "新增和更新问题集[问卷]")
    @PostMapping("v1/baseQuestion/questionSection/saveOrUpdQuestionSection")
    public String saveOrUpdQuestionSection(@RequestBody @Validated QuestionSectionRequest questionSection ) {
        return questionSectionBiz.saveOrUpdQuestionSection(questionSection);
    }

    /**
    * 分页问题集[问卷]
    * @param
    * @return
    */
    @Operation(summary = "分页问题集[问卷]")
    @PostMapping("v1/baseQuestion/questionSection/pageQuestionSection")
    public QuestionSectionResponse pageQuestionSection(@RequestBody @Validated QuestionSectionSearchRequest questionSectionSearch ) {
        return questionSectionBiz.pageQuestionSection(questionSectionSearch);
    }

    /**
    * 列出问题集[问卷]-无分页
    * @param
    * @return
    */
    @Operation(summary = "列出问题集[问卷]-无分页")
    @PostMapping("v1/baseQuestion/questionSection/listQuestionSection")
    public List<QuestionSectionResponse> listQuestionSection(@RequestBody @Validated QuestionSectionSearchRequest questionSectionSearch ) {
        return questionSectionBiz.listQuestionSection(questionSectionSearch);
    }

    /**
    * 根据ID获取详情
    * @param
    * @return
    */
    @Operation(summary = "根据ID获取详情")
    @GetMapping("v1/baseQuestion/questionSection/getQuestionSection")
    public QuestionSectionResponse getQuestionSection(@Validated String questionSectionId) {
        return questionSectionBiz.getQuestionSection(questionSectionId);
    }

    /**
    * 启用问题集[问卷]
    * @param
    * @return
    */
    @Operation(summary = "启用问题集[问卷]")
    @GetMapping("v1/baseQuestion/questionSection/enabledQuestionSection")
    public Boolean enabledQuestionSection(@Validated String questionSectionId) {
        return questionSectionBiz.enabledQuestionSection(questionSectionId);
    }

    /**
    * 禁用问题集[问卷]
    * @param
    * @return
    */
    @Operation(summary = "禁用问题集[问卷]")
    @GetMapping("v1/baseQuestion/questionSection/disabledQuestionSection")
    public Boolean disabledQuestionSection(@Validated String questionSectionId) {
        return questionSectionBiz.disabledQuestionSection(questionSectionId);
    }

    /**
    * 排序问题集[问卷]
    * @param
    * @return
    */
    @Operation(summary = "排序问题集[问卷]")
    @GetMapping("v1/baseQuestion/questionSection/sortQuestionSection")
    public Boolean sortQuestionSection(@Validated String questionSectionId, @Validated Integer sequence) {
        return questionSectionBiz.sortQuestionSection(questionSectionId,sequence);
    }

    /**
    * 交换问题集[问卷]
    * @param
    * @return
    */
    @Operation(summary = "交换问题集[问卷]")
    @GetMapping("v1/baseQuestion/questionSection/transposeQuestionSection")
    public Boolean transposeQuestionSection(@Validated String leftSectionId, @Validated String rightSectionId) {
        return questionSectionBiz.transposeQuestionSection(leftSectionId,rightSectionId);
    }

    /**
    * 删除or批量删除问题集[问卷]
    * @param
    * @return
    */
    @Operation(summary = "删除or批量删除问题集[问卷]")
    @DeleteMapping("v1/baseQuestion/questionSection/delQuestionSection")
    public Boolean delQuestionSection(@Validated String questionSectionIds ) {
        return questionSectionBiz.delQuestionSection(questionSectionIds);
    }

    /**
    * 复制问题集[问卷]
    * @param
    * @return
    */
    @Operation(summary = "复制问题集[问卷]")
    @PostMapping("v1/baseQuestion/questionSection/copyQuestionSection")
    public String copyQuestionSection(@RequestBody @Validated String oriQuestionSectionId ) {
        return questionSectionBiz.copyQuestionSection(oriQuestionSectionId);
    }

    /**
    * 自动生成问题集[问卷]
    * @param
    * @return
    */
    @Operation(summary = "自动生成问题集[问卷]")
    @PostMapping("v1/baseQuestion/questionSection/generateQuestionSectionAutomatic")
    public String generateQuestionSectionAutomatic(@RequestBody @Validated QuestionnaireGenerateElementsRequest questionnaireGenerateElements ) {
        return questionSectionBiz.generateQuestionSectionAutomatic(questionnaireGenerateElements);
    }

    /**
    * 查询问题集-问题
    * @param
    * @return
    */
    @Operation(summary = "查询问题集-问题")
    @PostMapping("v1/baseQuestion/questionSection/listSectionQuestion")
    public QuestionSectionResponse listSectionQuestion(@RequestBody @Validated QuestionsInSectionRequest questionsInSection ) {
        return questionSectionBiz.listSectionQuestion(questionsInSection);
    }

    /**
    * 排序问题集-题目
    * @param
    * @return
    */
    @Operation(summary = "排序问题集-题目")
    @GetMapping("v1/baseQuestion/questionSection/sortSectionQuestion")
    public Boolean sortSectionQuestion(@Validated String questionSectionId, @Validated String questionSectionItemId, @Validated Integer sequence) {
        return questionSectionBiz.sortSectionQuestion(questionSectionId,questionSectionItemId,sequence);
    }

    /**
    * 交换问题集-题目顺序
    * @param
    * @return
    */
    @Operation(summary = "交换问题集-题目顺序")
    @GetMapping("v1/baseQuestion/questionSection/transposeSectionQuestion")
    public Boolean transposeSectionQuestion(@Validated String questionSectionId, @Validated String leftQuestionSectionItemId, @Validated String rightQuestionSectionItemId) {
        return questionSectionBiz.transposeSectionQuestion(questionSectionId,leftQuestionSectionItemId,rightQuestionSectionItemId);
    }

    /**
    * 启用问题集-题目
    * @param
    * @return
    */
    @Operation(summary = "启用问题集-题目")
    @GetMapping("v1/baseQuestion/questionSection/enabledSectionQuestion")
    public Boolean enabledSectionQuestion(@Validated String questionSectionId, @Validated String questionSectionItemId) {
        return questionSectionBiz.enabledSectionQuestion(questionSectionId,questionSectionItemId);
    }

    /**
    * 禁用问题集-题目
    * @param
    * @return
    */
    @Operation(summary = "禁用问题集-题目")
    @GetMapping("v1/baseQuestion/questionSection/disabledSectionQuestion")
    public Boolean disabledSectionQuestion(@Validated String questionSectionId, @Validated String questionSectionItemId) {
        return questionSectionBiz.disabledSectionQuestion(questionSectionId,questionSectionItemId);
    }

    /**
    * 删除or批量删除问题集-题目
    * @param
    * @return
    */
    @Operation(summary = "删除or批量删除问题集-题目")
    @DeleteMapping("v1/baseQuestion/questionSection/delSectionQuestion")
    public Boolean delSectionQuestion(String questionSectionId, List<String> questionSectionItemIds ) {
        return questionSectionBiz.delSectionQuestion(questionSectionId, questionSectionItemIds);
    }


}