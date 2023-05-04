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
 * 消息定义(MessageDefine)实体类
 *
 * @author lait
 * @since 2023-04-28 10:26:53
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "MessageDefine", title = "消息定义")
@TableName("message_define")
public class MessageDefineEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "消息定义id")
    private String messageDefineId;

    @Schema(title = "消息类型")
    private String type;

    @Schema(title = "消息分类id")
    private String messageCategId;

    @Schema(title = "消息分类名称")
    private String messageCategName;

    @Schema(title = "标题")
    private String title;

    @Schema(title = "内容")
    private String content;

    @Schema(title = "链接")
    private String linkUrl;

    @Schema(title = "链接地址")
    private String linkDesc;

    @Schema(title = "接收者工厂[0-单用户 1-全体用户 2-实验用户 3-实验小组用户]")
    private Integer receiverFactory;

    @Schema(title = "引用id")
    private String refId;

    @Schema(title = "发布渠道[0-站内 1-im 99-全渠道]")
    private Integer publishChannel;

    @Schema(title = "发布时间 [不指定立即推送]")
    private Date publishTime;

    @Schema(title = "发布状态 [0-未发布 1-已发布]")
    private Boolean publishState;

    @Schema(title = "创建人")
    private String createBy;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

