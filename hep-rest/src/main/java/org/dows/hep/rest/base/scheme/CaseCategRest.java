package org.dows.hep.rest.base.scheme;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.request.CaseCategoryRequest;
import org.dows.hep.api.tenant.casus.response.CaseCategoryResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseCategoryBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @folder admin-hep/案例域-类目管理
 * @author fhb
 * @description
 * @date 2023/5/15 17:35
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "案例域-类目管理", description = "案例域-类目管理")
public class CaseCategRest {
    private final TenantCaseCategoryBiz caseCategoryBiz;

    /**
     * @author fhb
     * @description 新增和更新案例域类目
     * @date 2023/4/20 10:22
     * @param request - 请求对象
     * @return 新增或更新对象的 id
     */
    @Operation(summary = "新增和更新")
    @PostMapping("v1/baseCase/caseCategory/saveOrUpdCaseCategory")
    public String saveOrUpdCaseCategory(@RequestBody @Validated CaseCategoryRequest request) {
        return caseCategoryBiz.saveOrUpdateCaseCategory(request);
    }

    /**
     * @author fhb
     * @description 批量新增和更新
     * @date 2023/5/23 16:14
     * @param
     * @return
     */
    @Operation(summary = "批量新增和更新")
    @PostMapping("v1/baseCase/caseCategory/batchSaveOrUpd")
    public Boolean batchSaveOrUpd(@RequestBody @Validated List<CaseCategoryRequest> list) {
        return caseCategoryBiz.batchSaveOrUpd(list);
    }

    /**
     * @author fhb
     * @description 以树形结构返回该组（categoryGroup）下的类目
     * @date 2023/4/20 10:16
     * @param categoryGroup - 类目分组
     * @return 树形结构的类目集合
     */
    @Operation(summary = "根据 groupCode 查询所有类目")
    @GetMapping("v1/baseCase/caseCategory/listByCategoryGroup")
    public List<CaseCategoryResponse> listByCategoryGroup(String categoryGroup) {
        return caseCategoryBiz.getChildrenByPid("0", categoryGroup);
    }

    /**
     * @author fhb
     * @description 删除or批量删除
     * @date 2023/4/20 10:23
     * @param caseCategoryIds - questionCategoryId 数组的字符串表示
     * @return Boolean
     */
    @Operation(summary = "删除or批量删除")
    @DeleteMapping("v1/baseCase/caseCategory/delCaseCategory")
    public Boolean delCaseCategory(@RequestBody List<String> caseCategoryIds) {
        return caseCategoryBiz.delByIds(caseCategoryIds);
    }
}
