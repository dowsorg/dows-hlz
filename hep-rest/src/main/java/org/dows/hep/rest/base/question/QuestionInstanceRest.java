package org.dows.hep.rest.base.question;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.question.request.QuestionPageRequest;
import org.dows.hep.api.base.question.request.QuestionRequest;
import org.dows.hep.api.base.question.request.QuestionSearchRequest;
import org.dows.hep.api.base.question.response.QuestionPageResponse;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.biz.base.question.QuestionInstanceBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:问题:问题
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "问题", description = "问题")
public class QuestionInstanceRest {
    private final QuestionInstanceBiz questionInstanceBiz;

    /**
    * 新增和更新
    * @param
    * @return
    */
    @Operation(summary = "新增和更新")
    @PostMapping("v1/baseQuestion/questionInstance/saveOrUpdQuestion")
    public String saveOrUpdQuestion(@RequestBody @Validated QuestionRequest question ) {
        return questionInstanceBiz.saveOrUpdQuestion(question);
    }

    /**
    * 分页
    * @param
    * @return
    */
    @Operation(summary = "分页")
    @PostMapping("v1/baseQuestion/questionInstance/pageQuestion")
    public Response<Page<QuestionPageResponse>> pageQuestion(@RequestBody @Validated QuestionPageRequest questionPageRequest ) {
        Page<QuestionPageResponse> result = questionInstanceBiz.pageQuestion(questionPageRequest);
        return Response.ok(result);
    }

    /**
    * 条件查询-无分页
    * @param
    * @return
    */
    @Operation(summary = "条件查询-无分页")
    @PostMapping("v1/baseQuestion/questionInstance/listQuestion")
    public Response<List<QuestionResponse>> listQuestion(@RequestBody @Validated QuestionSearchRequest questionSearch ) {
        List<QuestionResponse> result = questionInstanceBiz.listQuestion(questionSearch);
        return Response.ok(result);
    }

    /**
    * 根据ID获取详情
    * @param
    * @return
    */
    @Operation(summary = "根据ID获取详情")
    @GetMapping("v1/baseQuestion/questionInstance/getQuestion")
    public Response<QuestionResponse> getQuestion(@Validated String questionInstanceId) {
        QuestionResponse result = questionInstanceBiz.getQuestion(questionInstanceId);
        return Response.ok(result);
    }

    /**
    * 启用
    * @param
    * @return
    */
    @Operation(summary = "启用")
    @GetMapping("v1/baseQuestion/questionInstance/enabledQuestion")
    public Response<Boolean> enabledQuestion(@Validated String questionInstanceId) {
        Boolean result = questionInstanceBiz.enabledQuestion(questionInstanceId);
        return Response.ok(result);
    }

    /**
    * 禁用
    * @param
    * @return
    */
    @Operation(summary = "禁用")
    @GetMapping("v1/baseQuestion/questionInstance/disabledQuestion")
    public Response<Boolean> disabledQuestion(@Validated String questionInstanceId) {
        Boolean result = questionInstanceBiz.disabledQuestion(questionInstanceId);
        return Response.ok(result);
    }

    /**
    * 排序
    * @param
    * @return
    */
    @Operation(summary = "排序")
    @GetMapping("v1/baseQuestion/questionInstance/sortQuestion")
    public Response<Boolean> sortQuestion(@Validated String string, @Validated Integer sequence) {
        Boolean result = questionInstanceBiz.sortQuestion(string, sequence);
        return Response.ok(result);
    }

    /**
    * 交换
    * @param
    * @return
    */
    @Operation(summary = "交换")
    @GetMapping("v1/baseQuestion/questionInstance/transposeQuestion")
    public Response<Boolean> transposeQuestion(@Validated String leftQuestionInstanceId, @Validated String rightQuestionInstanceId) {
        Boolean result = questionInstanceBiz.transposeQuestion(leftQuestionInstanceId, rightQuestionInstanceId);
        return Response.ok(result);
    }

    /**
    * 删除or批量删除
    * @param
    * @return
    */
    @Operation(summary = "删除or批量删除")
    @DeleteMapping("v1/baseQuestion/questionInstance/delQuestion")
    public Response<Boolean> delQuestion(List<String> questionInstanceIds ) {
        Boolean result = questionInstanceBiz.delQuestion(questionInstanceIds);
        return Response.ok(result);
    }


}