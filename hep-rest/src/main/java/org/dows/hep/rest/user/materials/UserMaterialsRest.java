package org.dows.hep.rest.user.materials;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.materials.request.MaterialsPageRequest;
import org.dows.hep.api.base.materials.response.MaterialsPageResponse;
import org.dows.hep.api.base.materials.response.MaterialsResponse;
import org.dows.hep.biz.base.materials.MaterialsBaseBiz;
import org.dows.hep.biz.user.materials.UserMaterialsBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    private final MaterialsBaseBiz baseBiz;

    /**
    * 分页
    * @param
    * @return
    */
    @Operation(summary = "分页")
    @PostMapping("v1/userMaterials/userMaterials/pageMaterials")
    public IPage<MaterialsPageResponse> pageMaterials(@RequestBody @Validated MaterialsPageRequest materialsPageRequest, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        materialsPageRequest.setAccountId(accountId);
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

    /**
     * 下载资料
     *
     * @param
     * @return
     */
    @Operation(summary = "下载资料")
    @PostMapping("v1/userMaterials/userMaterials/download")
    public String download(@RequestParam String materialsId) {
        return userMaterialsBiz.download(materialsId);
    }


}