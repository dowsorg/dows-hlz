package org.dows.hep.api.base.materials.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author fhb
 * @description
 * @date 2023/4/27 10:19
 */
@Data
@NoArgsConstructor
@Schema(name = "MaterialsPageResponse 对象", title = "问题分页Response")
public class MaterialsPageResponse {

    @Schema(title = "资料ID")
    private String materialsId;

    @Schema(title = "标题")
    private String title;

    @Schema(title = "资料简介")
    private String descr;

    @Schema(title = "创建者姓名")
    private String userName;

    @Schema(title = "创建者姓名")
    private String accountName;

    @Schema(title = "上传时间")
    private Date dt;

    @Schema(title = "上传时间-eg. 2022年5月3日星期二 17:49")
    private String uploadTime;

    @Schema(title = "序号")
    private Integer sequence;

    @Schema(title = "是否又权限操作")
    private Boolean canExe;

    @Schema(title = "账号ID")
    @JsonIgnore
    private String accountId;


}
