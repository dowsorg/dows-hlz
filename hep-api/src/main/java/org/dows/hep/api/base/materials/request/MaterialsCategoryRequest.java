package org.dows.hep.api.base.materials.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/4/24 16:53
 */
@Data
@NoArgsConstructor
@Schema(name = "MaterialsCategory 对象", title = "资料类别Request")
public class MaterialsCategoryRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "资料分类ID")
    private String materialsCategoryId;

    @Schema(title = "资料类别")
    private String type;

    @Schema(title = "分类名称")
    private String categoryName;

    @Schema(title = "类别ID路径")
    private String materialsCategIdPath;

    @Schema(title = "类别name路径")
    private String materialsCategNamePath;

    @Schema(title = "创建者账号Id")
    private String accountId;

    @Schema(title = "创建者姓名")
    private String accountName;

    @Schema(title = "序号")
    private Integer sequence;

    @Schema(title = "状态")
    private Integer state;
}
