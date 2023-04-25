package org.dows.hep.rest.base.materials;

import com.baomidou.dynamic.datasource.annotation.DS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.account.biz.constant.BaseConstant;
import org.dows.account.biz.util.JwtUtil;
import org.dows.hep.api.base.materials.request.MaterialsCategoryRequest;
import org.dows.hep.api.base.materials.request.MaterialsRequest;
import org.dows.hep.biz.base.materials.MaterialsCategoryBiz;
import org.dows.hep.entity.MaterialsCategoryEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jx
 * @date 2023/4/24 16:11
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "资料类别信息", description = "资料类别信息")
public class MaterialCategoryRest {
    private final MaterialsCategoryBiz materialsCategoryBiz;

    /**
     * 新增资料类别信息
     * @param
     * @return
     */
    @Operation(summary = "新增资料类别信息")
    @PostMapping("v1/baseMaterials/materialsCategory/saveMaterialsCategory")
    public Boolean saveMaterialsCategory(@RequestBody @Validated MaterialsCategoryRequest materials, HttpServletRequest request) {
        String token = request.getHeader("token");
        Map<String, Object> map = JwtUtil.parseJWT(token, BaseConstant.PROPERTIES_JWT_KEY);
        //1、获取登录账户和名称
        String accountId = map.get("accountId").toString();
        String accountName = map.get("accountName").toString();
        materials.setAccountId(accountId);
        materials.setAccountName(accountName);
        //2、保存账号
        return materialsCategoryBiz.saveMaterialsCategory(materials);
    }

    /**
     * 获取 资料类别信息 列表
     * @param
     * @return
     */
    @Operation(summary = "获取 资料类别信息 列表")
    @PostMapping("v1/baseMaterials/materialsCategory/listMaterialsCategory")
    public List<MaterialsCategoryEntity> listMaterialsCategory(@RequestBody @Validated MaterialsCategoryRequest materials) {
        return materialsCategoryBiz.listMaterialsCategory(materials);
    }

    /**
     * 删除 资料类别信息
     * @param
     * @return
     */
    @Operation(summary = "删除 资料类别信息")
    @DeleteMapping("v1/baseMaterials/materialsCategory/deleteCaterialsCategorys")
    public Integer deleteCaterialsCategorys(@RequestParam @Validated Set<String> materialsCategoryIds,@RequestParam String appId) {
        return materialsCategoryBiz.deleteCaterialsCategorys(materialsCategoryIds,appId);
    }

    /**
     * 排序 资料类别信息 列表
     * @param
     * @return
     */
    @Operation(summary = "排序 资料类别信息 列表")
    @PostMapping("v1/baseMaterials/materialsCategory/sortCaterialsCategoryList")
    public Boolean sortCaterialsCategoryList(@RequestBody List<MaterialsCategoryRequest> list) {
        return materialsCategoryBiz.sortCaterialsCategoryList(list);
    }
}
