package org.dows.hep.api.base.materials.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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

    @Schema(title = "资料ID-更新时有")
    private String materialsId;

    @Schema(title = "bizCode")
    @NotBlank(message = "业务代码不能为空")
    private String bizCode;

    @Schema(title = "资料分类ID")
    private String categoryId;

    @Schema(title = "标题")
    private String title;

    @Schema(title = "资料简介")
    private String descr;

    @Schema(title = "序号")
    private Integer sequence;

    @Schema(title = "附件集合")
    private List<MaterialsAttachmentRequest> materialsAttachments;

    @Schema(title = "创建者账号ID")
    @JsonIgnore
    private String accountId;

    @Schema(title = "创建者名")
    @JsonIgnore
    private String accountName;

}
