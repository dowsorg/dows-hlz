package org.dows.hep.api.base.materials.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/4/18 15:17
 */

@Data
@NoArgsConstructor
@Schema(name = "MaterialsResponse 对象", title = "资料")
public class MaterialsResponse {
    @Schema(title = "资料ID-更新时有")
    private String materialsId;

    @Schema(title = "标题")
    private String title;

    @Schema(title = "资料简介")
    private String descr;

    @Schema(title = "资料类型-预留")
    private String type;

    @Schema(title = "状态")
    private Integer enabled;

    @Schema(title = "序号")
    private Integer sequence;

    @Schema(title = "创建者账号ID")
    private String accountId;

    @Schema(title = "创建者名")
    private String accountName;

    @Schema(title = "上传时间-eg. 2022年5月3日星期二 17:49")
    private String uploadTime;

    @Schema(title = "附件集合")
    private List<MaterialsAttachmentResponse> materialsAttachments;
}
