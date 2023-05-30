package org.dows.hep.rest.base.question;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.enums.QuestionSectionAccessAuthEnum;
import org.dows.hep.api.base.question.enums.QuestionSourceEnum;
import org.dows.hep.api.base.question.request.QuestionSectionDelItemRequest;
import org.dows.hep.api.base.question.request.QuestionSectionRequest;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.biz.base.question.QuestionDomainBaseBiz;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:问题:问题集[问卷]
* @folder admin-hep/问题域-问卷
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "问题域-问题集[问卷]", description = "问题集[问卷]")
public class QuestionSectionRest {
    private final QuestionDomainBaseBiz baseBiz;
    private final QuestionSectionBiz questionSectionBiz;

    /**
    * 新增问题集[问卷]
    * @param
    * @return
    */
    @Operation(summary = "新增和更新")
    @PostMapping("v1/baseQuestion/questionSection/saveOrUpdQuestionSection")
    public String saveOrUpdQuestionSection(@RequestBody @Validated QuestionSectionRequest questionSection, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        String accountName = baseBiz.getAccountName(request);
        questionSection.setAccountId(accountId);
        questionSection.setAccountName(accountName);
        return questionSectionBiz.saveOrUpdQuestionSection(questionSection, QuestionSectionAccessAuthEnum.PUBLIC_VIEWING, QuestionSourceEnum.ADMIN);
    }

    /**
    * 列出问题集[问卷]-无分页
    * @param
    * @return
    */
    @Operation(summary = "列出问题集[问卷]-无分页")
    @PostMapping("v1/baseQuestion/questionSection/listQuestionSection")
    public List<QuestionSectionResponse> listQuestionSection(@RequestBody @Validated List<String> ids) {
        return questionSectionBiz.listQuestionSection(ids);
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
    * 删除or批量删除问题集[问卷]
    * @param
    * @return
    */
    @Operation(summary = "删除or批量删除问题集[问卷]")
    @DeleteMapping("v1/baseQuestion/questionSection/delQuestionSection")
    public Boolean delQuestionSection(@RequestBody List<String> questionSectionIds ) {
        return questionSectionBiz.delQuestionSection(questionSectionIds);
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
    public Boolean delSectionQuestion(@RequestBody QuestionSectionDelItemRequest request) {
        return questionSectionBiz.delSectionQuestion(request);
    }

}