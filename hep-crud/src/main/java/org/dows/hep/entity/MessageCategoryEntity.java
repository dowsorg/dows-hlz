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
 * 消息类目(MessageCategory)实体类
 *
 * @author lait
 * @since 2023-04-28 10:26:52
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "MessageCategory", title = "消息类目")
@TableName("message_category")
public class MessageCategoryEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "父ID")
    private Long pid;

    @Schema(title = "类别父id")
    private String messageCategPid;

    @Schema(title = "类别ID")
    private String messageCategId;

    @Schema(title = "类别名")
    private String messageCategName;

    @Schema(title = "类别ID路径")
    private String messageCategIdPath;

    @Schema(title = "类别name路径")
    private String messageCategNamePath;

    @Schema(title = "类别组")
    private String messageCategGroup;

    @Schema(title = "序列号")
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

