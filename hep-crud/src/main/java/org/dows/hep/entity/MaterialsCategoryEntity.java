package org.dows.hep.entity;

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

import java.util.Date;

/**
 * @author jx
 * @date 2023/4/24 17:01
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "MaterialsCategory", title = "资料类别")
@TableName("materials_category")
public class MaterialsCategoryEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "资料分类ID")
    private String materialsCategoryId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "bizCode")
    private String bizCode;

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

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
