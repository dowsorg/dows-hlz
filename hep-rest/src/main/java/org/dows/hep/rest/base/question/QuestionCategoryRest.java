package org.dows.hep.rest.base.question;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.request.QuestionCategoryRequest;
import org.dows.hep.api.base.question.response.QuestionCategoryResponse;
import org.dows.hep.biz.base.question.QuestionCategBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @folder admin-hep/问题域-类目管理
 * @author fhb
 * @description
 * @date 2023/4/20 9:50
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "问题域-类目管理", description = "问题域-类目管理")
public class QuestionCategoryRest {
    private final QuestionCategBiz questionCategBiz;

    /**
     * @author fhb
     * @description 新增和更新问题域类目
     * @date 2023/4/20 10:22
     * @param request - 请求对象
     * @return 新增或更新对象的 id
     */
    @Operation(summary = "新增和更新")
    @PostMapping("v1/baseQuestion/questionCategory/saveOrUpdQuestionCategory")
    public String saveOrUpdQuestionCategory(@RequestBody @Validated QuestionCategoryRequest request) {
        return questionCategBiz.saveOrUpdateQuestionCategory(request);
    }

    /**
     * @author fhb
     * @description 以树形结构返回该组（categoryGroup）下的类目
     * @date 2023/4/20 10:16
     * @param categoryGroup - 类目分组
     * @return 树形结构的类目集合
     */
    @Operation(summary = "根据 groupCode 查询所有类目")
    @GetMapping("v1/baseQuestion/questionCategory/listByCategoryGroup")
    public List<QuestionCategoryResponse> listByCategoryGroup(String categoryGroup) {
        return questionCategBiz.getTreeChildrenByPid("0", categoryGroup);
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
    public Boolean delQuestionCategory(@RequestBody List<String> questionCategoryIds) {
        return questionCategBiz.delByIds(questionCategoryIds);
    }
}
