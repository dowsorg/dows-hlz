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
 * 事件类别管理(EventCateg)实体类
 *
 * @author lait
 * @since 2023-04-24 10:23:48
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "EventCateg", title = "事件类别管理")
@TableName("event_categ")
public class EventCategEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "分布式id")
    private String eventCategId;

    @Schema(title = "父类别id")
    private String categPid;

    @Schema(title = "名称")
    private String categName;

    @Schema(title = "父类别id路径")
    private String categIdPath;

    @Schema(title = "父类别名称路径")
    private String categNamePath;

    @Schema(title = "根类别")
    private String family;

    @Schema(title = "扩展属性")
    private String extend;

    @Schema(title = "排序号")
    private Integer seq;

    @Schema(title = "标记")
    private Boolean mark;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

