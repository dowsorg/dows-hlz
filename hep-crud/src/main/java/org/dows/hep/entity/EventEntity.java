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
 * 突发事件(Event)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:20
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Event", title = "突发事件")
@TableName("event")
public class EventEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "分布式id")
    private String eventId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "突发事件名称")
    private String eventName;

    @Schema(title = "图片")
    private String pic;

    @Schema(title = "分类id")
    private String eventCategId;

    @Schema(title = "分类名称")
    private String categName;

    @Schema(title = "分布id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;


    @Schema(title = "事件说明")
    private String descr;

    @Schema(title = "事件提示")
    private String tips;

    @Schema(title = "创建者账号")
    private String createAccountId;

    @Schema(title = "创建者名称")
    private String createAccountName;

    @Schema(title = "触发类型 0-条件触发 1-第一期 2-第二期...5-第5期")
    private Integer triggerType;

    @Schema(title = "触发期数")
    private String triggerPeriod;

    @Schema(title = "触发时间段 1-前期 2-中期 3-后期")
    private String triggerSpan;

    @Schema(title = "状态 0-启用 1-停用")
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

