package org.dows.hep.api.base.materials.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/4/18 15:17
 */
@Data
@NoArgsConstructor
@Schema(name = "MaterialsAttachmentResponse 对象", title = "资料附件")
public class MaterialsAttachmentResponse {

    @Schema(title = "资料ID-更新时有")
    private String materialsId;

    @Schema(title = "资料附件ID")
    private String materialsAttachmentId;

    @Schema(title = "文件名称")
    private String fileName;

    @Schema(title = "文件路径")
    private String fileUri;

    @Schema(title = "文件类型")
    private String fileType;

    @Schema(title = "序号-默认按照上传顺序")
    private Integer sequence;
}
