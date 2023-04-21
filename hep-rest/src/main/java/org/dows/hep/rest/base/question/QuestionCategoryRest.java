package org.dows.hep.rest.base.question;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.question.request.QuestionCategoryRequest;
import org.dows.hep.api.base.question.response.QuestionCategoryResponse;
import org.dows.hep.biz.base.question.QuestionCategBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/4/20 9:50
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "问题域类目", description = "问题域类目")
public class QuestionCategoryRest {
    private final QuestionCategBiz questionCategBiz;

    /**
     * @author fhb
     * @description 以树形结构返回该组（categoryGroup）下的类目
     * @date 2023/4/20 10:16
     * @param categoryGroup - 类目分组
     * @return 树形结构的类目集合
     */
    @Operation(summary = "查询所有类目-根据groupCode")
    @GetMapping("v1/baseQuestion/questionCategory/listByCategoryGroup")
    public Response<List<QuestionCategoryResponse>> listByCategoryGroup(String categoryGroup) {
        List<QuestionCategoryResponse> children = questionCategBiz.getChildrenByPid("0", categoryGroup);
        return Response.ok(children);
    }

    /**
     * @author fhb
     * @description 新增和更新问题域类目
     * @date 2023/4/20 10:22
     * @param request - 请求对象
     * @return 新增或更新对象的 id
     */
    @Operation(summary = "新增和更新")
    @PostMapping("v1/baseQuestion/questionCategory/saveOrUpdQuestionCategory")
    public Response<String> saveOrUpdQuestionCategory(@RequestBody @Validated QuestionCategoryRequest request) {
        String id = questionCategBiz.saveOrUpdateQuestionCategory(request);
        return Response.ok(id);
    }

    /**
     * @author fhb
     * @description 删除or批量删除
     * @date 2023/4/20 10:23
     * @param questionCategoryIds - questionCategoryId 数组的字符串表示
     * @return Boolean
     */
    @Operation(summary = "删除or批量删除")
    @DeleteMapping("v1/baseQuestion/questionCategory/delQuestionCategory")
    public Response<Boolean> delQuestionCategory(String questionCategoryIds) {
        Boolean aBoolean = questionCategBiz.delById(questionCategoryIds);
        return Response.ok(aBoolean);
    }
}
