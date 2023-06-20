package org.dows.hep.rest.base.question;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.QuestionAccessAuthEnum;
import org.dows.hep.api.base.question.QuestionSourceEnum;
import org.dows.hep.api.base.question.request.QuestionPageRequest;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSearchRequest;
import org.dows.hep.api.base.question.response.QuestionPageResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.biz.base.question.QuestionBaseBiz;
import org.dows.hep.biz.base.question.QuestionInstanceBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lait.zhang
 * @folder admin-hep/问题域-题目
 * @description project descr:问题:问题
 * @date 2023年4月18日 上午10:45:07
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "问题域-题目", description = "问题")
public class QuestionInstanceRest {
    private final QuestionInstanceBiz questionInstanceBiz;
    private final QuestionBaseBiz baseBiz;

    /**
    * 新增和更新
    * @param
    * @return
    */
    @Operation(summary = "新增和更新")
    @PostMapping("v1/baseQuestion/questionInstance/saveOrUpdQuestion")
    public String saveOrUpdQuestion(@RequestBody @Validated QuestionRequest question , HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        String accountName = baseBiz.getAccountName(request);
        question.setAccountId(accountId);
        question.setAccountName(accountName);
        return questionInstanceBiz.saveOrUpdQuestion(question, QuestionAccessAuthEnum.PUBLIC_VIEWING, QuestionSourceEnum.ADMIN);
    }

    /**
    * 分页
    * @param
    * @return
    */
    @Operation(summary = "分页")
    @PostMapping("v1/baseQuestion/questionInstance/pageQuestion")
    public IPage<QuestionPageResponse> pageQuestion(@RequestBody @Validated QuestionPageRequest questionPageRequest ) {
        return questionInstanceBiz.pageQuestion(questionPageRequest);
    }

    /**
    * 条件查询-无分页
    * @param
    * @return
    */
    @Operation(summary = "条件查询-无分页")
    @PostMapping("v1/baseQuestion/questionInstance/listQuestion")
    public List<QuestionResponse> listQuestion(@RequestBody @Validated QuestionSearchRequest questionSearch ) {
        return questionInstanceBiz.listQuestion(questionSearch);
    }

    /**
    * 根据ID获取详情
    * @param
    * @return
    */
    @Operation(summary = "根据ID获取详情")
    @GetMapping("v1/baseQuestion/questionInstance/getQuestion")
    public QuestionResponse getQuestion(@Validated String questionInstanceId) {
        return questionInstanceBiz.getQuestion(questionInstanceId);
    }

    /**
    * 启用
    * @param
    * @return
    */
    @Operation(summary = "启用")
    @GetMapping("v1/baseQuestion/questionInstance/enabledQuestion")
    public Boolean enabledQuestion(@Validated String questionInstanceId) {
        return questionInstanceBiz.enabledQuestion(questionInstanceId);
    }

    /**
    * 禁用
    * @param
    * @return
    */
    @Operation(summary = "禁用")
    @GetMapping("v1/baseQuestion/questionInstance/disabledQuestion")
    public Boolean disabledQuestion(@Validated String questionInstanceId) {
        return questionInstanceBiz.disabledQuestion(questionInstanceId);
    }

    /**
    * 排序
    * @param
    * @return
    */
    @Operation(summary = "排序")
    @GetMapping("v1/baseQuestion/questionInstance/sortQuestion")
    public Boolean sortQuestion(@Validated String string, @Validated Integer sequence) {
        return questionInstanceBiz.sortQuestion(string, sequence);
    }

    /**
    * 交换
    * @param
    * @return
    */
    @Operation(summary = "交换")
    @GetMapping("v1/baseQuestion/questionInstance/transposeQuestion")
    public Boolean transposeQuestion(@Validated String leftQuestionInstanceId, @Validated String rightQuestionInstanceId) {
        return questionInstanceBiz.transposeQuestion(leftQuestionInstanceId, rightQuestionInstanceId);
    }

    /**
     * 删除单选和多选题的选项
     * @param
     * @return
     */
    @Operation(summary = "删除单选和多选题的选项")
    @GetMapping("v1/baseQuestion/questionInstance/delQuestionOptions")
    public Boolean delQuestionOptions(String questionOptionId ) {
        return questionInstanceBiz.delQuestionOptions(questionOptionId);
    }

    /**
    * 删除or批量删除
    * @param
    * @return
    */
    @Operation(summary = "删除or批量删除")
    @DeleteMapping("v1/baseQuestion/questionInstance/delQuestion")
    public Boolean delQuestion(@RequestBody List<String> questionInstanceIds ) {
        return questionInstanceBiz.delQuestion(questionInstanceIds);
    }


}