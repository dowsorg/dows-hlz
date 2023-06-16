package org.dows.hep.api.base.tags.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dows.framework.crud.api.model.PageRequest;

import java.util.Set;

/**
 * @author jx
 * @date 2023/6/14 17:30
 */
@Data
@Schema(title = "标签 分页请求")
public class PageTagsRequest extends PageRequest {

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "搜索关键字", description = "搜索关键字")
    private String keyword;

    @Schema(title = "标签分类ID集合", description = "标签分类ID集合")
    private String tagsCategoryIds;
}
