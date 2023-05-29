package org.dows.hep.rest.base.evaluate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.evaluate.request.EvaluateCategoryRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateCategoryResponse;
import org.dows.hep.biz.base.evaluate.EvaluateCategoryBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @folder admin-hep/评估问卷-类目管理
 * @author fhb
 * @description
 * @date 2023/5/24 16:45
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "评估类目管理", description = "评估类目管理")
public class EvaluateCategRest {
    private final EvaluateCategoryBiz evaluateCategoryBiz;

    /**
     * @author fhb
     * @description 新增和更新评估类目
     * @date 2023/4/20 10:22
     * @param request - 请求对象
     * @return 新增或更新对象的 id
     */
    @Operation(summary = "新增和更新")
    @PostMapping("v1/baseEvaluate/evaluateCategory/saveOrUpdateEvaluateCategory")
    public String saveOrUpdateEvaluateCategory(@RequestBody @Validated EvaluateCategoryRequest request) {
        return evaluateCategoryBiz.saveOrUpdateEvaluateCategory(request);
    }

    /**
     * @author fhb
     * @description 批量新增和更新
     * @date 2023/5/23 16:14
     * @param
     * @return
     */
    @Operation(summary = "批量新增和更新")
    @PostMapping("v1/baseEvaluate/evaluateCategory/batchSaveOrUpd")
    public Boolean batchSaveOrUpd(@RequestBody @Validated List<EvaluateCategoryRequest> list) {
        return evaluateCategoryBiz.batchSaveOrUpd(list);
    }

    /**
     * @author fhb
     * @description 以树形结构返回该组（categoryGroup）下的类目
     * @date 2023/4/20 10:16
     * @param categoryGroup - 类目分组
     * @return 树形结构的类目集合
     */
    @Operation(summary = "根据 groupCode 查询所有类目")
    @GetMapping("v1/baseEvaluate/evaluateCategory/listByCategoryGroup")
    public List<EvaluateCategoryResponse> listByCategoryGroup(String categoryGroup) {
        return evaluateCategoryBiz.getChildrenByPid("0", categoryGroup);
    }

    /**
     * @author fhb
     * @description 删除or批量删除
     * @date 2023/4/20 10:23
     * @param evaluateCategoryIds - ids
     * @return Boolean
     */
    @Operation(summary = "删除or批量删除")
    @DeleteMapping("v1/baseEvaluate/evaluateCategory/delEvaluateCategory")
    public Boolean delEvaluateCategory(@RequestBody List<String> evaluateCategoryIds) {
        return evaluateCategoryBiz.delByIds(evaluateCategoryIds);
    }
}
