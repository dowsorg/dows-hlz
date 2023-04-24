package org.dows.hep.rest.base.picture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.materials.request.MaterialsRequest;
import org.dows.hep.biz.base.picture.PictureManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jx
 * @date 2023/4/24 15:37
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "图示管理", description = "图示管理")
public class PictureManageRest {
    private final PictureManageBiz pictureManageBiz;

    /**
     * 新增图示
     * @param
     * @return
     */
    @Operation(summary = "新增图示")
    @PostMapping("v1/basePicture/picture/savePicture")
    public String savePicture(@RequestBody @Validated MaterialsRequest materials ) {
        return pictureManageBiz.savePicture(materials);
    }
}
