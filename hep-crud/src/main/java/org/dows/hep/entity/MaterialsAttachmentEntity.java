package org.dows.hep.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.framework.crud.api.CrudEntity;

/**
 * 资料-附件(MaterialsAttachment)实体类
 *
 * @author lait
 * @since 2023-04-23 09:47:03
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "MaterialsAttachment", title = "资料-附件")
@TableName("materials_attachment")
public class MaterialsAttachmentEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "资料附件ID")
    private String materialsAttachmentId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "资料ID")
    private String materialsId;

    @Schema(title = "文件名称")
    private String fileName;

    @Schema(title = "文件路径")
    private String fileUri;

    @Schema(title = "文件类型")
    private String fileType;

    @Schema(title = "序号")
    private Integer sequence;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

