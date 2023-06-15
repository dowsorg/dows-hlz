package org.dows.hep.api.base.tags.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/6/14 16:18
 */
@Data
@NoArgsConstructor
@Schema(name = "TagsManageRequest 对象", title = "标签管理Request")
public class TagsInstanceRequest {

    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "标签分布式ID")
    private String tagsId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "标签名称")
    private String name;

    @Schema(title = "标签分类ID")
    private String tagsCategoryId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;
}
