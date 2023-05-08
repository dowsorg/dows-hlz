package org.dows.hep.rest.base.materials;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.materials.request.MaterialsPageRequest;
import org.dows.hep.api.base.materials.request.MaterialsRequest;
import org.dows.hep.api.base.materials.request.MaterialsSearchRequest;
import org.dows.hep.api.base.materials.response.MaterialsPageResponse;
import org.dows.hep.api.base.materials.response.MaterialsResponse;
import org.dows.hep.biz.base.materials.MaterialsManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:资料中心:资料信息
*
* @author lait.zhang
* @date 2023年4月21日 上午10:26:46
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "资料信息", description = "资料信息")
public class MaterialsRest {
    private final MaterialsManageBiz materialsBiz;

    /**
    * 新增和更新资料信息
    * @param
    * @return
    */
    @Operation(summary = "新增和更新资料信息")
    @PostMapping("v1/baseMaterials/materials/saveOrUpdMaterials")
    public String saveOrUpdMaterials(@RequestBody @Validated MaterialsRequest materials ) {
        return materialsBiz.saveOrUpdMaterials(materials);
    }

    /**
     * 分页
     *
     * @param
     * @return
     */
    @Operation(summary = "分页")
    @PostMapping("v1/baseMaterials/materials/pageMaterials")
    public IPage<MaterialsPageResponse> pageMaterials(@RequestBody @Validated MaterialsPageRequest materialsPageRequest) {
        return materialsBiz.pageMaterials(materialsPageRequest);
    }

    /**
     * 条件查询-无分页
     *
     * @param
     * @return
     */
    @Operation(summary = "条件查询-无分页")
    @PostMapping("v1/baseMaterials/materials/listMaterials")
    public List<MaterialsResponse> listMaterials(@RequestBody @Validated MaterialsSearchRequest materialsSearchRequest) {
        return materialsBiz.listMaterials(materialsSearchRequest);
    }

    /**
    * 根据ID获取详情
    * @param
    * @return
    */
    @Operation(summary = "根据ID获取详情")
    @GetMapping("v1/baseMaterials/materials/getMaterials")
    public MaterialsResponse getMaterials(@Validated String materialsId) {
        return materialsBiz.getMaterials(materialsId);
    }

    /**
    * 启用
    * @param
    * @return
    */
    @Operation(summary = "启用")
    @GetMapping("v1/baseMaterials/materials/enabledMaterials")
    public Boolean enabledMaterials(@Validated String materialsId) {
        return materialsBiz.enabledMaterials(materialsId);
    }

    /**
    * 禁用
    * @param
    * @return
    */
    @Operation(summary = "禁用")
    @GetMapping("v1/baseMaterials/materials/disabledMaterials")
    public Boolean disabledMaterials(@Validated String materialsId) {
        return materialsBiz.disabledMaterials(materialsId);
    }

    /**
     * 删除or批量删除
     *
     * @param
     * @return
     */
    @Operation(summary = "删除or批量删除")
    @DeleteMapping("v1/baseMaterials/materials/delMaterials")
    public Boolean delMaterials(List<String> materialsId) {
        return materialsBiz.delMaterials(materialsId);
    }

}