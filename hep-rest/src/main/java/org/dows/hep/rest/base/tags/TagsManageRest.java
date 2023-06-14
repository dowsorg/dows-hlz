package org.dows.hep.rest.base.tags;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.tags.request.TagsInstanceRequest;
import org.dows.hep.api.base.tags.response.TagsInstanceResponse;
import org.dows.hep.biz.base.tags.TagsManageBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author jx
 * @date 2023/6/14 15:22
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "标签管理", description = "标签管理")
public class TagsManageRest {
    private final TagsManageBiz tagsManageBiz;

    /**
     * 新增或更新标签
     * @param
     * @return
     */
    @Operation(summary = "新增或更新标签")
    @PostMapping("v1/baseTags/tagsManage/insertOrUpdateTags")
    public Boolean insertOrUpdateTags(@RequestBody @Validated TagsInstanceRequest manageRequest) {
        return tagsManageBiz.insertOrUpdateTags(manageRequest);
    }

    /**
     * 查询标签
     * @param
     * @return
     */
    @Operation(summary = "查询标签")
    @GetMapping("v1/baseTags/tagsManage/getTagsByTagsId/{tagsId}")
    public TagsInstanceResponse getTagsByTagsId(@PathVariable @Validated String tagsId) {
        return tagsManageBiz.getTagsByTagsId(tagsId);
    }
}
