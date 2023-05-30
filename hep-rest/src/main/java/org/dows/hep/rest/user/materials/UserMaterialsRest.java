package org.dows.hep.rest.user.materials;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.materials.request.MaterialsPageRequest;
import org.dows.hep.api.base.materials.response.MaterialsPageResponse;
import org.dows.hep.api.base.materials.response.MaterialsResponse;
import org.dows.hep.biz.user.materials.UserMaterialsBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:资料中心:资料信息
* @folder user-hep/资料中心
* @author lait.zhang
* @date 2023年4月24日 上午10:47:00
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "资料信息", description = "资料信息")
public class UserMaterialsRest {
    private final UserMaterialsBiz userMaterialsBiz;

    /**
    * 分页
    * @param
    * @return
    */
    @Operation(summary = "分页")
    @PostMapping("v1/userMaterials/userMaterials/pageMaterials")
    public IPage<MaterialsPageResponse> pageMaterials(@RequestBody @Validated MaterialsPageRequest materialsPageRequest ) {
        return userMaterialsBiz.pageMaterials(materialsPageRequest);
    }

    /**
    * 根据ID获取详情
    * @param
    * @return
    */
    @Operation(summary = "根据ID获取详情")
    @GetMapping("v1/userMaterials/userMaterials/getMaterials")
    public MaterialsResponse getMaterials(@Validated String materialsId) {
        return userMaterialsBiz.getMaterials(materialsId);
    }


}