package org.dows.hep.rest.base.picture;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.picture.request.PictureRequest;
import org.dows.hep.api.base.picture.response.PictureResponse;
import org.dows.hep.biz.base.picture.PictureManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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
    @PostMapping("v1/basePicture/picture/savePersonPicture")
    public Boolean savePicture(@RequestBody @Validated PictureRequest request) {
        return pictureManageBiz.savePicture(request);
    }

    /**
     * 删除图示
     * @param
     * @return
     */
    @Operation(summary = "删除图示")
    @DeleteMapping("v1/basePicture/picture/deletePersonPictures")
    public Integer deletePersonPictures(@RequestBody Set<String> ids,@RequestParam String appId) {
        return pictureManageBiz.deletePersonPictures(ids,appId);
    }

    /**
     * 图示列表
     * @param
     * @return
     */
    @Operation(summary = "图示列表")
    @PostMapping("v1/basePicture/picture/listPersonPictures")
    public IPage<PictureResponse> listPersonPictures(@RequestBody PictureRequest request) {
        return pictureManageBiz.listPersonPictures(request);
    }
}
