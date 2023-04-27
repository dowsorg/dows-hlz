package org.dows.hep.api.base.picture.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/4/27 11:05
 */
@Data
@NoArgsConstructor
@Schema(name = "Materials 对象", title = "资料Request")
public class PictureRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "资料ID")
    private String materialsId;

    @Schema(title = "资料分类ID")
    private String categoryId;

    @Schema(title = "资料分类名称")
    private String categoryName;

    @Schema(title = "资料ID")
    private String informationId;

    @Schema(title = "标题")
    private String title;

    @Schema(title = "资料简介")
    private String descr;

    @Schema(title = "资料类型")
    private String type;

    @Schema(title = "状态")
    private Boolean enabled;

    @Schema(title = "序号")
    private Integer sequence;

    @Schema(title = "创建者账号Id")
    private String accountId;

    @Schema(title = "创建者姓名")
    private String accountName;

    @Schema(title = "附件集合")
    private String materialsAttachment;

    @Schema(title = "页码")
    private Integer pageNo;

    @Schema(title = "页数")
    private Integer pageSize;
}
