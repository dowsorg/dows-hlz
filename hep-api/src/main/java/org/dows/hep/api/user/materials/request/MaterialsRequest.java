package org.dows.hep.api.user.materials.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author fhb
 * @description
 * @date 2023/4/18 15:02
 */
@Data
@NoArgsConstructor
@Schema(name = "MaterialsRequest 对象", title = "资料")
public class MaterialsRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "资料ID-更新时有")
    private String materialsId;

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

    @Schema(title = "创建者账号ID")
    private String accountId;

    @Schema(title = "创建者名")
    private String accountName;

    @Schema(title = "附件集合")
    private List<MaterialsAttachmentRequest> materialsAttachments;

}
